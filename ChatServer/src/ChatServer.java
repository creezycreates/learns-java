// ChatServer.java
// Author: Christian Rikong
// Description: Multi-client chat server with public and private messaging support.

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatServer {

    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    // userId -> ClientSession
    private final ConcurrentMap<Integer, ClientSession> clients = new ConcurrentHashMap<>();
    private final AtomicInteger userIdCounter = new AtomicInteger(1000);

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("[SERVER] Listening on port " + port + " ...");

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            int userId = userIdCounter.getAndIncrement();
            pool.submit(new ClientHandler(socket, userId));
        }
    }

    // Broadcast message to all clients
    private void broadcast(String message) {
        for (ClientSession session : clients.values()) {
            session.out.println(message);
        }
    }

    // Send message to a specific client
    private void sendPrivateMessage(int targetUserId, String message, int senderId) {
        ClientSession target = clients.get(targetUserId);
        ClientSession sender = clients.get(senderId);

        if (target != null) {
            target.out.println("[Private from " + sender.displayName + "]: " + message);
            sender.out.println("[Private to " + target.displayName + "]: " + message);
        } else {
            sender.out.println("[SYSTEM] User ID " + targetUserId + " not found.");
        }
    }

    private void systemMessage(String text) {
        broadcast("[SYSTEM] " + text);
    }

    // Inner types
    private static class ClientSession {
        final int userId;
        volatile String displayName;
        final PrintWriter out;

        ClientSession(int userId, String displayName, PrintWriter out) {
            this.userId = userId;
            this.displayName = displayName;
            this.out = out;
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final int userId;

        ClientHandler(Socket socket, int userId) {
            this.socket = socket;
            this.userId = userId;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
            ) {
                String defaultName = "User" + userId;
                clients.put(userId, new ClientSession(userId, defaultName, out));

                out.println("Welcome to the chat!");
                out.println("Your ID is " + userId + " and your name is '" + defaultName + "'.");
                out.println("Commands: /name <newName>, /list, /pm <userId> <message>, /quit");
                out.println("Type your messages to chat publicly.");

                systemMessage(defaultName + " joined the chat.");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if (line.equalsIgnoreCase("/quit")) {
                        out.println("Goodbye!");
                        break;
                    } else if (line.equalsIgnoreCase("/list")) {
                        out.println("Connected users:");
                        for (ClientSession cs : clients.values()) {
                            out.println(" - " + cs.displayName + " (ID: " + cs.userId + ")");
                        }
                    } else if (line.startsWith("/name ")) {
                        String newName = line.substring(6).trim();
                        if (newName.isEmpty() || newName.contains(" ")) {
                            out.println("[SYSTEM] Invalid name. Avoid spaces; use letters/numbers/underscore.");
                        } else {
                            ClientSession cs = clients.get(userId);
                            String old = cs.displayName;
                            cs.displayName = newName;
                            systemMessage(old + " is now known as " + newName + ".");
                        }
                    } else if (line.startsWith("/pm ")) {
                        // Private message format: /pm <userId> <message>
                        String[] tokens = line.split(" ", 3);
                        if (tokens.length < 3) {
                            out.println("[SYSTEM] Usage: /pm <userId> <message>");
                        } else {
                            try {
                                int targetId = Integer.parseInt(tokens[1]);
                                String message = tokens[2];
                                sendPrivateMessage(targetId, message, userId);
                            } catch (NumberFormatException e) {
                                out.println("[SYSTEM] Invalid user ID format.");
                            }
                        }
                    } else {
                        // Public broadcast
                        ClientSession cs = clients.get(userId);
                        broadcast(cs.displayName + ": " + line);
                    }
                }
            } catch (IOException ioe) {
                System.out.println("[SERVER] I/O error with user " + userId + ": " + ioe.getMessage());
            } finally {
                ClientSession removed = clients.remove(userId);
                if (removed != null) {
                    systemMessage(removed.displayName + " left the chat.");
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
                System.out.println("[SERVER] Disconnected user " + userId);
            }
        }
    }

}
