import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("Do you want to start the server or client? (type \"cancel\" if you want to quit)");
        makeDecision();
    }

    public static void makeDecision() {
        Scanner scan = new Scanner(System.in);
        String decision = scan.nextLine();
        if (decision.equalsIgnoreCase("server")) {
            runServer();
        } else if (decision.equalsIgnoreCase("client")) {
            runClient();
        } else if (decision.equalsIgnoreCase("cancel")) {
            scan.close();
            return;
        } else {
            System.out.println("Invalid arguments");
            System.out.println("Valid arguments are \"server\", \"client\" or \"cancel\"");
            makeDecision();
        }
        scan.close();
    }

    public static void runServer() {
        System.out.println("Which port do you want to open the server in?");
        Scanner scanner = new Scanner(System.in);
        try {
            int port = scanner.nextInt();
            Server server = new Server(port);
            server.clientListener();
            server.serverInput();
            scanner.close();
        } catch (InputMismatchException e) {
            System.out.println("Invalid Port");
        }
    }

    public static void runClient() {
        System.out.println("What is the host/ip of the server?");
        Scanner scanner = new Scanner(System.in);
        try {
            String host = scanner.nextLine();
            System.out.println("What is the port of the server?");
            int port = scanner.nextInt();
            Client client = new Client(host, port);
            client.recieveMessage();
            client.sendMessage();
            scanner.close();
        } catch (InputMismatchException e) {
            System.out.println("Invalid Input");
        }
    }
}
