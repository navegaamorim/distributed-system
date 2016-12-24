/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileserver;

import chatserver.WorkerThread;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Client;
import models.UserDirectory;

/**
 *
 * @author navega
 */
public class FileSenderThread extends Thread {

    private Client mClient;
    private File mFile;

    public FileSenderThread(Client client, File file) {
        mClient = client;
        mFile = file;
    }

    @Override
    public void run() {

        while (true) {
            try {
                //Address
                String multiCastAddress = "224.0.0.1";
                final int multiCastPort = mClient.getSocket().getPort();

                //Create Socket
                System.out.println("Create Sender socket on address " + multiCastAddress + " and port " + multiCastPort + ".");
                InetAddress group = InetAddress.getByName(multiCastAddress);
                MulticastSocket socket = new MulticastSocket(multiCastPort);
                socket.joinGroup(group);

                //Prepare File list
                String dataFiles = "SENDER-> " + mFile.getName();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dataFiles);
                byte[] data = baos.toByteArray();

                //Send data
                socket.send(new DatagramPacket(data, data.length, group, multiCastPort));
                Thread.sleep(5000);

            } catch (IOException ex) {
                Logger.getLogger(ListSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ListSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
