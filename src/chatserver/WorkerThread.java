package chatserver;

import util.FileOperations;
import fileserver.FileReceiverThread;
import fileserver.FileSenderThread;
import fileserver.ListSenderThread;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import models.Client;
import protocol.CommandProtocol;
import protocol.EnumProtocol;
import util.PortManager;

public class WorkerThread extends Thread {

    private DataInputStream is = null;
    private PrintStream os = null;
    private ArrayList<WorkerThread> mThreads;
    private Client mClient;

    private PrintWriter out;
    private BufferedReader in;
    private String mInputLine;

    private CommandProtocol mProtocol;
    private EnumProtocol mEnumProtocol;

    // private static ArrayList<UserDirectory> mDirectories = new ArrayList<>();
    public WorkerThread(Client client, ArrayList<WorkerThread> threads) {
        this.mClient = client;
        this.mThreads = threads;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(mClient.getSocket().getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(mClient.getSocket().getInputStream()));

            while ((mInputLine = in.readLine()) != null) {
                mProtocol = new CommandProtocol(mInputLine);
                mEnumProtocol = mProtocol.getCommand();

                switch (mEnumProtocol) {
                    case HELP:
                        out.println(mProtocol.buildHelp());
                        break;

                    case REGISTER:
                        executeRegister();
                        break;

                    case LOGIN:
                        executeLogin();
                        break;

                    case LOGOUT:
                        executeLogOut();
                        break;

                    case MESSAGE:
                        executeMessage();
                        break;

                    case PRIVATE:
                        executePrivate();
                        break;

                    case GROUP:
                        executeGroup();
                        break;

                    case LIST:
                        executeList();
                        break;

                    case DOWNLOAD:
                        executeDownload();
                        break;

                }

                if (mInputLine.equals("/quit")) {
                    break;
                }

            }
            out.close();
            in.close();
            mClient.getSocket().close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeRegister() {
        String user = mInputLine.split(" ")[1];
        String pass = mInputLine.split(" ")[2];
        boolean result = FileOperations.checkIfUserExists(user);

        if (!result) {
            if (FileOperations.saveUserInFile(user, pass) && FileOperations.createUserFolder(user)) {
                mClient.setUserName(user);
                mClient.setPasswd(pass);
                mClient.setOnline(false);
                out.println(mProtocol.buildInfo("Registo efetuado com Sucesso"));

            } else {
                out.println(mProtocol.buildInfo("Erro no Registo"));
            }

        } else {
            out.println(mProtocol.buildInfo("Esse username ja existe"));
        }
    }

    private void executeLogin() {
        String user = mInputLine.split(" ")[1];
        String pass = mInputLine.split(" ")[2];

        boolean isOnline = getClientLoginStatus(user);

        if (isOnline) {
            out.println(mProtocol.buildInfo("Ja tem sessao iniciada noutro local"));

        } else if (FileOperations.checkLogin(user, pass)) {
            mClient.setUserName(user);
            mClient.setPasswd(pass);
            mClient.setOnline(true);
            mClient.setMultiCastPort(PortManager.getMultiCastPort());

            ListSenderThread.addClient(mClient);
            new FileReceiverThread(mClient, mClient.getMultiCastPort()).start();

            out.println(mProtocol.buildInfo("Bem Vindo " + mClient.getUserName()));
        }
    }

    private void executeLogOut() {
        out.println(mProtocol.buildInfo("Ate Ja " + mClient.getUserName()));
        mClient.setOnline(false);
    }

    private void executeMessage() throws IOException {
        if (mClient.isOnline()) {
            String mensagem = mProtocol.buildMessage(mInputLine, mClient.getUserName());
            for (WorkerThread worker : mThreads) {
                if (!worker.equals(this) && worker.mClient.isOnline()) {
                    PrintWriter outWriter = new PrintWriter(worker.mClient.getSocket().getOutputStream(), true);
                    outWriter.println(mensagem);
                }
            }
        }
    }

    private void executePrivate() throws IOException {
        if (mClient.isOnline()) {
            String mensagem = mProtocol.buildPrivate(mInputLine, mClient.getUserName());
            String dest = mInputLine.split(" ")[1];

            for (WorkerThread worker : mThreads) {
                if (!worker.equals(this) && worker.mClient.isOnline() && worker.mClient.getUserName().equals(dest)) {
                    PrintWriter outWriter = new PrintWriter(worker.mClient.getSocket().getOutputStream(), true);
                    outWriter.println(mensagem);
                }
            }
        }
    }

    private void executeGroup() throws IOException {
        if (mClient.isOnline()) {
            int maxIndex = mInputLine.split(" ").length - 1;

            //obter os utilizadores online do grupo
            ArrayList<String> usersName = new ArrayList<>();
            for (int i = 1; i < maxIndex; ++i) {
                String name = mInputLine.split(" ")[i];
                if (getClientByName(name).isOnline()) {
                    usersName.add(name);
                }
            }
            //mensagem para o grupo
            String mensagem = mInputLine.split(" ")[maxIndex];

            for (WorkerThread worker : mThreads) {
                if (!worker.equals(this) && worker.mClient.isOnline()) {
                    for (String name : usersName) {
                        if (worker.mClient.getUserName().equals(name)) {
                            PrintWriter outWriter = new PrintWriter(worker.mClient.getSocket().getOutputStream(), true);
                            outWriter.println(mProtocol.buildGroup(mClient.getUserName(), usersName, mensagem));
                        }
                    }
                }
            }
        }
    }

    private void executeList() {
        if (mClient.isOnline()) {
            out.println("/list");
        }
    }

    private void executeDownload() throws IOException {
        int maxIndex = mInputLine.split(" ").length;
        String nomeuser = mInputLine.split(" ")[1];
        Client client = getClientByName(nomeuser);

        //File file = FileOperations.getFileByName(client, nomeficheiro);
        //String nomeficheiro = mInputLine.split(" ")[2];
        //out.println("/info " + client.toString() + file.getAbsolutePath());
        //new FileSenderThread(client, file).start();
        //out.println("/info " + nomeuser + " | " + nomeficheiro + " | " + client.getUserName() + " | " + file.getAbsolutePath());
        for (WorkerThread worker : mThreads) {
            if (worker != this && worker.mClient.equals(client)) {
                PrintWriter outWriter = new PrintWriter(client.getSocket().getOutputStream(), true);
                outWriter.println(mProtocol.buildInfo("Vai transferir dados para " + mClient.getUserName()));

                for (int i = 2; i < maxIndex; ++i) {
                    String nomefile = mInputLine.split(" ")[i];
                    File file = FileOperations.getFileByName(client, nomefile);
                    new FileSenderThread(client, file, mClient.getMultiCastPort()).start();
                }
                //out.println("/info " + nomefile);
                //new FileSenderThread(client, file, mClient.getSocket().getPort()).start();
            }
        }
    }

    private void executeMultiDownload() {
        int maxIndex = mInputLine.split(" ").length;

        String nomeuser = mInputLine.split(" ")[1];
        String nomeficheiro = mInputLine.split(" ")[2];

        for (int i = 2; i < maxIndex; ++i) {
            String nomefile = mInputLine.split(" ")[i];
            out.println("/info " + nomefile);

        }

        //Client client = getClientByName(nomeuser);
        //File file = FileOperations.getFileByName(client, nomeficheiro);
    }

    private Client getClientByName(String name) {
        Client client = new Client();
        for (WorkerThread thread : mThreads) {
            if (thread.mClient.getUserName().equals(name)) {
                client = thread.mClient;
            }
        }
        return client;
    }

    private boolean getClientLoginStatus(String name) {
        boolean isOnline = false;
        for (WorkerThread thread : mThreads) {

            if (thread.mClient.getUserName() != null) {
                if (thread.mClient.getUserName().equals(name)) {
                    isOnline = thread.mClient.isOnline();
                }
            }
        }
        return isOnline;
    }

}
