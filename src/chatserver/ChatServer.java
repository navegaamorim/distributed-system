package chatserver;

import util.FileOperations;
import fileserver.ListSenderThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import models.Client;
import util.PortManager;

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
        
        FileOperations.printAllUsers();
        new ListSenderThread().start();
                
        while (true) {
            try {
                Client client = new Client();
                client.setSocket(serverSocket.accept());
                
                WorkerThread worker = new WorkerThread(client, threads);
                worker.start();
                threads.add(worker);
                
            } catch (IOException e) {
                System.out.println(e);
                serverSocket.close();
            }
        }
    }
    
}
