package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;

import java.util.Collection;
import java.util.HashMap;

public class Constants {
    public static final int MIN_NUMBER_OF_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_PLAYERS = 3;
    public static final String DRAW = "isDraw";
    public static final String NO_DRAW = "noDraw";
    public static final int MAX_NUM_ISLANDS = 12;
    public static final int MAX_LOBBY_SIZE = 10;
    public static final int ISLAND_ID_NOT_RECEIVED = -1;
    public static final int ISLAND_THRESHOLD_FOR_GAME_OVER = 3;
    public static final int MAX_TOWER_NUMBER = 10;
    public static final String[] fxmlPaths = new String[]{"/fxml/MainMenu.fxml","/fxml/ConnectMenu.fxml","/fxml/MatchMakingMenu.fxml"};

    public static final String RESET = "\033[0m";

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String GREY = "\033[0;90m";
    public static final String Logo = "\n" +
            "███████╗██████╗░██╗░█████╗░███╗░░██╗████████╗██╗░░░██╗░██████╗\n" +
            "██╔════╝██╔══██╗██║██╔══██╗████╗░██║╚══██╔══╝╚██╗░██╔╝██╔════╝\n" +
            "█████╗░░██████╔╝██║███████║██╔██╗██║░░░██║░░░░╚████╔╝░╚█████╗░\n" +
            "██╔══╝░░██╔══██╗██║██╔══██║██║╚████║░░░██║░░░░░╚██╔╝░░░╚═══██╗\n" +
            "███████╗██║░░██║██║██║░░██║██║░╚███║░░░██║░░░░░░██║░░░██████╔╝\n" +
            "╚══════╝╚═╝░░╚═╝╚═╝╚═╝░░╚═╝╚═╝░░╚══╝░░░╚═╝░░░░░░╚═╝░░░╚═════╝░";

    private static final HashMap<Color, String> colorPerStudent;
        static{
            colorPerStudent = new HashMap<>();
            colorPerStudent.put(Color.RED, Constants.RED);
            colorPerStudent.put(Color.GREEN, Constants.GREEN);
            colorPerStudent.put(Color.YELLOW, Constants.YELLOW);
            colorPerStudent.put(Color.BLUE, Constants.BLUE);
            colorPerStudent.put(Color.PINK, Constants.PURPLE);

        }

    public static String getStudentsColor(Color c){
            return colorPerStudent.get(c);
    }
    }



