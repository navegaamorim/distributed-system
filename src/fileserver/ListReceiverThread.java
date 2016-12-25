/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
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
            final int multiCastPort = 52684;
            final int bufferSize = 1024 * 4; //Maximum size of transfer object

            //Create Socket
            InetAddress group = InetAddress.getByName(multiCastAddress);
            MulticastSocket s = new MulticastSocket(multiCastPort);
            s.joinGroup(group);

            //Receive data
            System.out.println("Wating for datagram to be received...");

            //Create buffer
            byte[] buffer = new byte[bufferSize];
            s.receive(new DatagramPacket(buffer, bufferSize, group, multiCastPort));
            System.out.println("Datagram received!");

            //Deserialze object
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                Object readObject = ois.readObject();
                if (readObject instanceof String) {
                    String message = (String) readObject;
                    mChat.printMultiCastFileList(message);
                } else {
                    //mOut.print("The received object is not of type String!");
                }
            } catch (Exception e) {
                //mOut.print("No object could be read from the received UDP datagram.");
            }

        } catch (IOException ex) {
            Logger.getLogger(ListReceiverThread.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
