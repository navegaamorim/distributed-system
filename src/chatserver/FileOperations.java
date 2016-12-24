/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Client;
import models.UserDirectory;

/**
 *
 * @author navega
 */
public class FileOperations {

    public static boolean saveUserInFile(String key, String value) {

        HashMap<String, String> names = loadUsers();
        names.put(key, value);

        try {
            FileOutputStream fos;
            fos = new FileOutputStream("users.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(names);
            oos.close();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static HashMap<String, String> loadUsers() {
        FileInputStream fis;
        HashMap<String, String> users = new HashMap<>();
        try {
            fis = new FileInputStream("users.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            users = (HashMap<String, String>) ois.readObject();
            ois.close();

            for (String name : users.keySet()) {

                String key = name.toString();
                String value = users.get(name).toString();
                System.out.println(key + " " + value);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;

    }

    public static boolean createUserFolder(String username) {
        try {
            File dir = new File("users/" + username);
            dir.mkdir();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkIfUserExists(String username) {
        HashMap<String, String> users = FileOperations.loadUsers();

        for (String name : users.keySet()) {
            if (name.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkLogin(String username, String pass) {
        HashMap<String, String> users = FileOperations.loadUsers();

        for (String name : users.keySet()) {
            if (name.equals(username) && users.get(name).toString().equals(pass)) {
                return true;
            }
        }
        return false;
    }

    public static File getFileByName(Client client, String name) {
        File file = null;
        for (UserDirectory clientfile : client.getmFiles()) {

            if (clientfile.getFile().getName().equals(name)) {
                file = clientfile.getFile();
            }
        }
        return file;
    }

}
