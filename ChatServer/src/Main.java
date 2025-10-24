import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int port = 5555;
        if (args.length == 1) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        try {
            new ChatServer(port).start();
        } catch (IOException e) {
            System.err.println("[SERVER] Failed to start: " + e.getMessage());
        }
    }
}