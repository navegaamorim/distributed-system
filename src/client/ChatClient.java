package client;

import fileserver.ListReceiverThread;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import protocol.CommandProtocol;
import protocol.EnumProtocol;

/**
 *
 * @author 8130031
 * @author 8130257
 */
public class ChatClient implements Runnable {

    private static Socket clientSocket = null;
    private static PrintStream os = null;
    private static DataInputStream is = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    private String responseLine;

    public static void main(String[] args) {

        int portNumber = 2222;
        String host = "127.0.0.1";

        if (args.length < 2) {
            System.out.println("Uso: java ChatClient <host> <portNumber>\n" + "A usar host=" + host + ", porto=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Host desconhecido: " + host);
        } catch (IOException e) {
            System.err.println("I/O problemas em: " + host);
        }

        if (clientSocket != null && os != null && is != null) {
            try {
                String a = "";
                new Thread((Runnable) new ChatClient()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    public void run() {
        try {
            responseLine = "";
            while ((responseLine = is.readLine()) != null) {

                CommandProtocol protocol = new CommandProtocol(responseLine);
                EnumProtocol enumprotocol = protocol.getCommand();

                switch (enumprotocol) {
                    case HELP:
                        processHelp();
                        break;

                    case INFO:
                        processInfo();
                        break;

                    case ERROR:
                        processError();
                        break;

                    case MESSAGE:
                        processMessage();
                        break;

                    case PRIVATE:
                        processPrivate();
                        break;

                    case GROUP:
                        processGroup();
                        break;

                    case LIST:
                        new ListReceiverThread(this).start();
                        break;
                }

                if (responseLine.contains("/quit")) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    private void processMessage() {
        String user = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.indexOf(":"));
        String mensagem = responseLine.substring(responseLine.indexOf(":") + 1, responseLine.length());
        System.out.println("[public]" + "[" + user + "]" + "[" + mensagem + "]");
    }

    private void processPrivate() {
        String user = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.indexOf(":"));
        String mensagem = responseLine.substring(responseLine.indexOf(":") + 1, responseLine.length());
        System.out.println("[private]" + "[" + user + "]" + "[" + mensagem + "]");
    }

    private void processGroup() {
        String user = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.indexOf(":"));//user que enviou
        int maxIndex = responseLine.split(":").length - 1;
        String users = "";//users do grupo que receberam
        for (int i = 1; i < maxIndex; ++i) {
            users += responseLine.split(":")[i] + ";";
        }
        users = users.substring(0, users.length() - 1);//remover o ultimo ';'
        String mensagem = responseLine.split(":")[maxIndex];

        System.out.println("[group]" + "[" + user + "]" + "[" + users + "]" + "[" + mensagem + "]");
    }

    private void processInfo() {
        String mensagem = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.length());
        System.out.println("|| " + mensagem + " ||");
    }

    private void processError() {
        String mensagem = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.length());
        System.out.println("!! " + mensagem + " !!");
    }

    private void processHelp() {
        String mensagem = responseLine.substring(responseLine.indexOf(" ") + 1, responseLine.length());
        System.out.println(mensagem);
    }

    public void printMultiCastFileList(String text) {
        System.out.println(text);
    }
}
