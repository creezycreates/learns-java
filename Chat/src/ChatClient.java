// ChatClient.java
// Author: Christian Rikong
// Description: Chat client supporting public (/public) and private (/pm) messages.

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (
                Socket socket = new Socket(host, port);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("[CLIENT] Connected to " + host + ":" + port);
            System.out.println("[CLIENT] Available commands:");
            System.out.println("  /name <newName>       → Change your display name");
            System.out.println("  /list                 → Show connected users");
            System.out.println("  /public <message>     → Send a public message");
            System.out.println("  /pm <userId> <msg>    → Send a private message");
            System.out.println("  /quit                 → Exit chat");
            System.out.println("  (You can also just type text to send public messages)");

            Thread listener = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] Disconnected from server.");
                }
            }, "ServerListener");
            listener.setDaemon(true);
            listener.start();

            String input;
            while ((input = userIn.readLine()) != null) {
                if (input.trim().isEmpty()) continue;
                serverOut.println(input);
                if ("/quit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT] Error: " + e.getMessage());
            System.err.println("[CLIENT] Ensure the server is running and reachable.");
        }
    }


}
