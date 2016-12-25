/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.net.Socket;
import java.util.ArrayList;
import chatserver.WorkerThread;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author navega
 */
public class Client {

    public static final String PATH = "/home/navega/NetBeansProjects/DistributedSystems/users/";

    private String userName;
    private String passwd;
    private Socket socket;
    private ArrayList<UserDirectory> mFiles = new ArrayList<>();

    private boolean state;

    public Client() {
    }

    public Client(Socket socket) {
        this.socket = socket;
        this.userName = null;
        this.passwd = null;
    }

    public Client(Socket socket, String name, String passwd) {
        this.socket = socket;
        this.userName = name;
        this.passwd = passwd;
    }

//    public Client(Socket socket, String name, ArrayList<UserDirectory> files) {
//        this.socket = socket;
//        this.userName = name;
//        this.passwd = null;
//        this.mFiles = files;
//    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isOnline() {
        return state;
    }

    public void setOnline(boolean state) {
        this.state = state;
    }

    public ArrayList<UserDirectory> getmFiles() {
        return mFiles;
    }

    public void setmFiles(ArrayList<UserDirectory> mFiles) {
        this.mFiles = mFiles;
    }

    public void findUserFiles() {
        try {
            mFiles = new ArrayList<>();
            File folder = new File(PATH + userName);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    mFiles.add(new UserDirectory(this, file));
                }
            }
        } catch (Exception e) {
            System.out.println("Problema com a lista de ficheiros");
        }
    }

    @Override
    public String toString() {
        return "Client{" + "userName=" + userName + ", passwd=" + passwd + ", socket=" + socket + ", mFiles=" + mFiles + ", state=" + state + '}';
    }

}
