import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("server")) {
                System.out.println("Which port do you want to open the server in?");
                Scanner scanner = new Scanner(System.in);
                int port = scanner.nextInt();
                Server server = new Server(port);
                scanner.close();
                server.startServer();
            } else if (args[0].equals("client")) {
                System.out.println("What is the host/ip of the server?");
                Scanner scanner = new Scanner(System.in);
                String host = scanner.nextLine();
                System.out.println("What is the port of the server?");
                int port = scanner.nextInt();
                Client client = new Client(host, port);
                client.recieveMessage();
                client.sendMessage();
                scanner.close();
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
