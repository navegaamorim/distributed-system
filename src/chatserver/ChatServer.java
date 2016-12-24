package chatserver;

import fileserver.ListSenderThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import models.Client;

public class ChatServer {

    public static void main(String args[]) throws IOException {

        ServerSocket serverSocket = null;
        ArrayList<WorkerThread> threads = new ArrayList<>();

        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServer <portNumber>\n" + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        FileOperations.loadUsers();

        while (true) {
            try {
                Client client = new Client();
                client.setSocket(serverSocket.accept());
                WorkerThread worker = new WorkerThread(client, threads);
                worker.start();
                threads.add(worker);
                new ListSenderThread().start();

            } catch (IOException e) {
                System.out.println(e);
                serverSocket.close();
            }
        }
    }

}
