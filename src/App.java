import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("server")) {
                Server server = new Server(9999);
                server.startServer();
            } else if (args[0].equals("client")) {
                Client client = new Client("localhost", 9999);
                client.recieveMessage();
                client.sendMessage();
            } else {
                System.out.println("Invalid arguments");
                System.out.println("Valid arguments are \"server\" or \"client\"");
            }
        } else {
            System.out.println("Invalid arguments");
            System.out.println("Valid arguments are \"server\" or \"client\"");
        }
    }
}
