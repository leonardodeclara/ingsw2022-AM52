package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.client.GUI.GUI;

public class EriantysClient {
    private static final String CLI_ARGUMENT = "-cli";
    private static final String GUI_ARGUMENT = "-gui";

    public static void main(String[] args) {
        if (args.length == 0 || GUI_ARGUMENT.equals(args[0])) GUI.main(args);
        else if(CLI_ARGUMENT.equals(args[0]))
            CLI.main(args);
        else System.out.println("Command not found, application is closing.");
    }
}
