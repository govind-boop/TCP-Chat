import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
    ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void startServer() {
        System.out.println("Server Started!");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;

        private String clientId;
        private String clientNickname;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientId = bufferedReader.readLine();
                this.clientNickname = bufferedReader.readLine();
                clientHandlers.add(this);
                broadcast(clientNickname + " joined the chat!");
                System.out.println(clientNickname + " connected to the server!");
            } catch (IOException e) {
                shutdownClient();
            }
        }

        @Override
        public void run() {
            String message;

            while (socket.isConnected()) {
                try {
                    message = bufferedReader.readLine();
                    if (message.startsWith("#srvCommand ")) {
                        handleServerCommand(message);
                    } else {
                        broadcast(message);
                    }
                } catch (IOException e) {
                    shutdownClient();
                    break;
                }
            }
        }

        public void handleServerCommand(String message) {
            String[] messageSplit = message.split(" ");
            String command = messageSplit[1];
            String[] args = new String[messageSplit.length - 2];
            for (int i = 0; i < messageSplit.length - 2; i++) {
                args[i] = messageSplit[i + 2];
            }

            switch (command) {
                case "nick":
                    System.out.println(clientNickname + " changed their nickname to " + args[0]);
                    clientNickname = args[0];
                    break;
                case "quit":
                    shutdownClient();
                    break;
                case "list":
                    ArrayList<String> nicknames = new ArrayList<String>();
                    for (ClientHandler clientHandler : clientHandlers) {
                        nicknames.add(clientHandler.clientNickname);
                    }
                    Collections.sort(nicknames);
                    for (ClientHandler clientHandler : clientHandlers) {
                        try {
                            if (clientHandler.clientId.equals(clientId)) {
                                for (String nick : nicknames) {
                                    if (nick.equals(clientNickname)) {
                                        clientHandler.bufferedWriter.write(nick + " (you)");
                                        clientHandler.bufferedWriter.newLine();
                                        clientHandler.bufferedWriter.flush();
                                    } else {
                                        clientHandler.bufferedWriter.write(nick);
                                        clientHandler.bufferedWriter.newLine();
                                        clientHandler.bufferedWriter.flush();
                                    }
                                }

                            }
                        } catch (IOException e) {
                            shutdownClient();
                        }
                    }
            }
        }

        public void broadcast(String message) {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (!clientHandler.clientId.equals(clientId)) {
                        clientHandler.bufferedWriter.write(message);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    shutdownClient();
                }
            }
        }

        public void removeClientHandler() {
            clientHandlers.remove(this);
            System.out.println(clientNickname + " disconnected!");
            broadcast(clientNickname + " left the chat!");
        }

        public void shutdownClient() {
            removeClientHandler();
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}