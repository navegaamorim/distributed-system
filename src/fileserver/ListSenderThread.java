package fileserver;

import java.io.ByteArrayOutputStream;
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
import util.PortManager;

/**
 *
 * @author 8130031
 * @author 8130257
 */
public class ListSenderThread extends Thread {

    private static ArrayList<Client> mClients;
    private ArrayList<UserDirectory> mDirectories;

    public ListSenderThread() {
        mClients = new ArrayList<>();
        mDirectories = new ArrayList<>();
    }

    @Override
    public void run() {

        while (true) {
            try {
                //Address
                String multiCastAddress = "224.0.0.1";
                final int multiCastPort = PortManager.FILE_LIST_PORT;

                //Create Socket
                InetAddress group = InetAddress.getByName(multiCastAddress);
                MulticastSocket socket = new MulticastSocket(multiCastPort);
                socket.joinGroup(group);

                //Prepare File list
                mDirectories.clear();
                String dataFiles = "";
                for (Client client : mClients) {
                    client.findUserFiles();
                    for (UserDirectory directory : client.getmFiles()) {
                        mDirectories.add(directory);
                    }
                }

                for (int i = 0; i < mDirectories.size(); ++i) {
                    dataFiles += mDirectories.get(i).getOwner().getUserName() + " || " + mDirectories.get(i).getFile().getName() + "\n";
                }

                System.out.println(dataFiles);

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

    public static void addClient(Client client) {
        mClients.add(client);
    }

    public static void removeClient(Client client) {
        mClients.remove(client);
    }

}
