/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import chatserver.ChatServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

                String key = name;
                String value = users.get(name);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    public static void printAllUsers() {
        HashMap<String, String> users = loadUsers();
        for (String name : users.keySet()) {
            String key = name;
            String value = users.get(name);
            System.out.println("Nome: " + key + " | " + "Pass: " + value);
        }
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

    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

}
