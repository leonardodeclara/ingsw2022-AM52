package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;

import java.util.Collection;
import java.util.HashMap;

public class Constants {
    public static final int MIN_NUMBER_OF_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_PLAYERS = 3;
    public static final int NUM_PLAYABLE_PERSONALITY_CARDS = 3;
    public static final int NUM_EXISTING_PERSONALITY_CARDS = 12;
    public static final String DRAW = "isDraw";
    public static final String NO_DRAW = "noDraw";
    public static final String TIE = "tie";
    public static final int MAX_NUM_ISLANDS = 12;
    public static final int MAX_LOBBY_SIZE = 9;
    public static final int MAX_TABLE_SIZE = 10;
    public static final int ISLAND_ID_NOT_RECEIVED = -1;
    public static final int ISLAND_THRESHOLD_FOR_GAME_OVER = 3;
    public static final int MAX_TOWER_NUMBER = 10;
    public static final int MAX_STUDENTS_FOR_CARD_10_SWITCH = 2;
    public static final int MAX_BANS_NUMBER = 4;
    public static final int MAX_COINS_NUMBER = 20;
    public static final String[] fxmlPaths = new String[]{"/fxml/MainMenu.fxml","/fxml/ConnectMenu.fxml","/fxml/NicknameMenu.fxml","/fxml/MatchMakingMenu.fxml","/fxml/LobbyMenu.fxml","/fxml/WizardChoiceMenu.fxml","/fxml/TowerChoiceMenu.fxml","/fxml/GameTable.fxml"};
    public static final String MAIN_MENU_FXML = "/fxml/MainMenu.fxml";
    public static final String CONNECT_MENU_FXML = "/fxml/ConnectMenu.fxml";
    public static final String NICKNAME_MENU_FXML = "/fxml/NicknameMenu.fxml";
    public static final String MATCHMAKING_MENU_FXML = "/fxml/MatchMakingMenu.fxml";
    public static final String LOBBY_FXML = "/fxml/LobbyMenu.fxml";
    public static final String WIZARD_CHOICE_FXML = "/fxml/WizardChoiceMenu.fxml";
    public static final String TOWER_CHOICE_FXML = "/fxml/TowerChoiceMenu.fxml";
    public static final String GAME_TABLE_FXML = "/fxml/GameTable.fxml";
    public static final String RESOLUTION_TEST_FXML = "/fxml/ResolutionTest.fxml";
    public static double ISLAND_CIRCLE_RADIUS = 250;
    public static double CLOUD_CIRCLE_RADIUS = 50;
    public static double ISLAND_IMAGE_WIDTH = 100;
    public static double ISLAND_IMAGE_HEIGHT = 88.9;
    public static double CLOUD_IMAGE_WIDTH = 65;
    public static double CLOUD_IMAGE_HEIGHT = 58.5;
    public static double STUDENT_IMAGE_HEIGHT = 16;
    public static double STUDENT_IMAGE_WIDTH = 16;
    public static double STUDENTS_ISLAND_CIRCLE_RADIUS = 3;
    public static double STUDENTS_CLOUD_CIRCLE_RADIUS = 12;
    public static int CLOUD_PERSONALITY_OFFSET = 30;
    public static double ASSISTANT_X = 26;
    public static int ASSISTANT_Y_START = 11;
    public static int ASSISTANT_Y_OFFSET = 46;
    public static double ASSISTANT_IMAGE_HEIGHT = 125;
    public static double ASSISTANT_IMAGE_WIDTH = 85;
    public static double PERSONALITY_IMAGE_HEIGHT = 125;
    public static double PERSONALITY_IMAGE_WIDTH = 85;
    public static double COIN_IMAGE_HEIGHT = 56;
    public static double COIN_IMAGE_WIDTH = 49;
    public static double COIN_BOARD_START_X = 921;
    public static double COIN_BOARD_START_Y = 516;
    public static double STUDENT_BOARD_START_X = 942.6;
    public static double STUDENT_TABLE_START_Y = 146;
    public static double STUDENT_LOBBY_START_Y = 62;
    public static double STUDENT_TABLE_VGAP = 26;
    public static double STUDENT_TABLE_HGAP = 39;
    public static double STUDENT_LOBBY_VGAP = 32;
    public static double STUDENT_TABLE_WIDTH = 22;
    public static double STUDENT_TABLE_HEIGHT = 22;
    public static double TEACHER_BOARD_WIDTH = 27.57;
    public static double TEACHER_BOARD_HEIGHT = 24;
    public static double TEACHER_BOARD_START_X = 939.7;
    public static double TEACHER_BOARD_START_Y = 431;
    public static double TOWER_IMAGE_START_X = 971.6;
    public static double TOWER_IMAGE_START_Y = 480;
    public static double TOWER_TABLE_HGAP = 39;
    public static double TOWER_TABLE_VGAP = 36;
    public static double NAME_TO_BUTTON_VGAP = 110;
    public static double TOWER_IMAGE_HEIGHT = 40;
    public static double TOWER_IMAGE_WIDTH = 40;
    public static double BAN_IMAGE_HEIGHT = 30;
    public static double BAN_IMAGE_WIDTH = 30;
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



