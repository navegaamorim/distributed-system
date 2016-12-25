/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol;

import java.util.ArrayList;

/**
 *
 * @author navega
 */
public class CommandProtocol {

    private final String command;

    public CommandProtocol(String command) {
        this.command = command;
    }

    public EnumProtocol getCommand() {

        if (command.startsWith("/help")) {
            return EnumProtocol.HELP;
        }

        if (command.startsWith("/info")) {
            return EnumProtocol.INFO;
        }

        if (command.startsWith("/register")) {
            return EnumProtocol.REGISTER;
        }

        if (command.startsWith("/login")) {
            return EnumProtocol.LOGIN;
        }

        if (command.startsWith("/logout")) {
            return EnumProtocol.LOGOUT;
        }

        if (command.startsWith("/message")) {
            return EnumProtocol.MESSAGE;
        }

        if (command.startsWith("/private")) {
            return EnumProtocol.PRIVATE;
        }

        if (command.startsWith("/group")) {
            return EnumProtocol.GROUP;
        }

        if (command.startsWith("/list")) {
            return EnumProtocol.LIST;
        }

        if (command.startsWith("/download")) {
            return EnumProtocol.DOWNLOAD;
        }

        if (command.startsWith("/multidownload")) {
            return EnumProtocol.MULTIDOWNLOAD;
        }

        return EnumProtocol.ERROR;
    }

    public String buildInfo(String message) {
        return "/info " + message;
    }

    public String buildHelp() {
        return "/help [/register] Para se registar \n"
                + "/help [/login user pass] Para fazer login \n"
                + "/help [/logout] Para fazer logout \n"
                + "/help [/message mensagem] Para enviar uma mensagem para todos os utilizadores online \n"
                + "/help [/private user mensagem] Para enviar uma mensagem privada \n"
                + "/help [/group userX userY mensagem] Para enviar uma mensagem de grupo \n"
                + "/help [/list] Para ver a lista de ficheiros de todos os utilizadores online \n"
                + "/help [/download user file] Para fazer download de um ficheiro \n"
                + "/help [/quit] Para terminar o ChatClient";
    }

    public String buildMessage(String inputline, String clientName) {
        return "/message " + clientName + ":" + inputline.substring(inputline.indexOf(" ") + 1, inputline.length());
    }

    public String buildPrivate(String inputline, String clientName) {
        return "/private " + clientName
                + ":"
                + inputline.substring(inputline.indexOf(" ", inputline.indexOf(" ") + 1) + 1, inputline.length());
    }

    public String buildGroup(String clientName, ArrayList<String> usersName, String mensagem) {
        String usertext = ":";
        for (String user : usersName) {
            usertext += user + ":";
        }

        return "/group " + clientName + usertext + mensagem;
    }

}
