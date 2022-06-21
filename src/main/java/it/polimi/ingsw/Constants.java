package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;

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
    public static final int MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_2_PLAYERS = 3;
    public static final int MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_3_PLAYERS = 4;
    public static final int CARD_1_STUDENTS_TO_MOVE = 1;
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
    public static double ISLAND_IMAGE_WIDTH = 100*1.15;
    public static double ISLAND_IMAGE_HEIGHT = 88.9*1.15;
    public static double PERSONALITY_OFFSET_Y = 20;
    public static double CLOUD_IMAGE_WIDTH = 65;
    public static double CLOUD_IMAGE_HEIGHT = 58.5;
    public static double STUDENT_IMAGE_HEIGHT = 16;
    public static double STUDENT_IMAGE_WIDTH = 16;
    public static double STUDENTS_ISLAND_CIRCLE_RADIUS = 22;
    public static double STUDENTS_CLOUD_CIRCLE_RADIUS = 12;
    public static int CLOUD_PERSONALITY_OFFSET = 50;
    public static double ASSISTANT_X = 26;
    public static int ASSISTANT_Y_START = 11;
    public static int ASSISTANT_Y_OFFSET = 46;
    public static double ASSISTANT_IMAGE_HEIGHT = 125;
    public static double ASSISTANT_IMAGE_WIDTH = 85;
    public static int CURRENT_ASSISTANT_VGAP = 150;
    public static double COLOR_CHOICEBOX_VGAP = 70;
    public static double COLOR_CHOICEBOX_WIDTH = 150;
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
    public static double MOTHER_NATURE_HEIGHT=116;
    public static double MOTHER_NATURE_WIDTH=109.44;
    public static double MOTHER_NATURE_OFFSET_X=3;
    public static double MOTHER_NATURE_OFFSET_Y=-7;
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

    public static String personalityDescription(int cardId){
        String description="";
            switch (cardId) {
                case 1 -> {
                    description += ("CHOOSE A STUDENT TILE AND PLACE IT ON YOUR\n");
                    description += ("ISLAND OF CHOICE. A STUDENT WILL THEN BE PICKED\n");
                    description += ("FROM THE BAG AND PLACED BACK ON THIS CARD\n");
                }
                case 2-> {
                    description += ("DURING THIS TURN TAKE OVER TEACHERS\n");
                    description += ("EVEN IF IN YOUR TABLE THERE ARE AS MANY\n");
                    description += ("STUDENTS AS IN THE CURRENT OWNER'S TABLE\n");
                }
                case 3-> {
                    description += ("CHOOSE AN ISLAND AND CALCULATE THE PLAYERS'\n");
                    description += ("INFLUENCE AS IF MOTHER NATURE HAD ENDED ITS MOVEMENT\n");
                    description += ("THERE. DURING THIS TURN MOTHER NATURE WILL MOVE AS USUAL\n");
                    description += ("AND THE PLAYERS' INFLUENCE WILL BE COMPUTED AS ALWAYS\n");
                }
                case 4-> {
                    description += ("YOU CAN MOVE MOTHER NATURE UP TO TWO PLACES MORE\n");
                    description += ("THAN WHAT YOUR CURRENT ASSISTANT CARD SAYS.\n");
                }
                case 5-> {
                    description += ("PLACE A BAN TILE ON AN ISLAND OF CHOICE.\n");
                    description += ("THE NEXT TIME MOTHER NATURE IS ENDING ITS MOVEMENT\n");
                    description += ("THERE PUT BACK THE TILE WITHOUT COMPUTING\n");
                    description += ("THE PLAYERS INFLUENCE ON THAT ISLAND AND WITHOUT PLACING TOWERS\n");
                }
                case 6 -> {
                    description += ("DURING ISLANDS' INFLUENCE COMPUTATION \n");
                    description += ("TOWERS WILL NOT BE COUNTED\n");

                }
                case 7 -> {
                    description += ("TAKE UP TO THREE STUDENTS FROM THIS CARD\n");
                    description += ("AND SWAP THEM WITH AS MANY STUDENTS\n");
                    description += ("PLACED IN YOUR TABLE'S ENTRANCE\n");
                }
                case 8 -> {
                    description += ("DURING THIS TURN TWO POINTS ARE ADDED\n");
                    description += ("TO YOUR INFLUENCE COMPUTATION\n");
                }
                case 9 -> {
                    description += ("CHOOSE A STUDENT'S COLOR: DURING THE INFLUENCE \n");
                    description += ("COMPUTATION THAT COLOR WILL NOT PROVIDE INFLUENCE\n");
                }
                case 10 -> {
                    description += ("YOU CAN SWAP UP TO TWO STUDENTS\n");
                    description += ("BETWEEN YOUR BOARD'S TABLE AND ENTRANCE\n");
                }
                case 11 -> {
                    description += ("PICK A STUDENT FROM THIS CARD AND PLACE IT\n");
                    description += ("IN YOUR TABLE. THEN A NEW STUDENT TILE WILL\n");
                    description += ("BE PICKED FROM THE BAG AND PUT HERE\n");
                }
                case 12 -> {
                    description += ("CHOOSE A STUDENT'S COLOR: EVERY PLAYER\n");
                    description += ("(INCLUDING YOU) HAS TO PUT BACK IN THE BAG\n");
                    description += ("THREE TABLE'S STUDENT TILES OF THAT COLOR.\n");
                    description += ("PLAYERS THAT HAVE LESS THAN THREE WILL PUT\n");
                    description += ("BACK AS MANY AS THEY HAVE\n");
                }
            }
            return description;
    }
}



