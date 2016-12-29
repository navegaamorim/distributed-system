package fileserver;

import client.ChatClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.PortManager;

/**
 *
 * @author 8130031
 */
public class ListReceiverThread extends Thread {

    private ChatClient mChat;

    public ListReceiverThread(ChatClient chat) {
        this.mChat = chat;
    }

    @Override
    public void run() {
        try {
            //Address
            String multiCastAddress = "224.0.0.1";
            final int multiCastPort = PortManager.FILE_LIST_PORT;
            final int bufferSize = 1024 * 4; //Maximum size of transfer object

            //Create Socket
            InetAddress group = InetAddress.getByName(multiCastAddress);
            MulticastSocket s = new MulticastSocket(multiCastPort);
            s.joinGroup(group);

            //Receive data
            mChat.printMultiCastFileList("A espera de receber a lista...");

            //Create buffer
            byte[] buffer = new byte[bufferSize];
            s.receive(new DatagramPacket(buffer, bufferSize, group, multiCastPort));
            System.out.println("\nLista de Ficheiros");

            //Deserialze object
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                Object readObject = ois.readObject();
                if (readObject instanceof String) {
                    String message = (String) readObject;
                    mChat.printMultiCastFileList(message);
                } else {
                    mChat.printMultiCastFileList("Problemas com os dados recebidos da lista");
                }
            } catch (Exception e) {
                //mOut.print("No object could be read from the received UDP datagram.");
            }

        } catch (IOException ex) {
            Logger.getLogger(ListReceiverThread.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
