package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.GUI.Clickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Enum ClientState defines in a finite state machine way the possible states in which a client can be found.
 * Each state provides context and error messages for both CLI and GUI interfaces. It also determines what can be clicked in the graphical user interface during each state.
 */
public enum ClientState {
    CONNECT_STATE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Enter your nickname:");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("This nickname has already been taken! Retry");
            return texts;
        }


        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }

    },
    INSERT_NEW_GAME_PARAMETERS(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Enter the number of players (2/3) and the game mode (base o expert) to start:");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry:");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },
    WAIT_IN_LOBBY(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Waiting for other players to join..");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Error!");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },
    WAIT_TURN(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Wait for your turn..");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return getCLIContextMessage(GB);
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Error!:");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },
    SET_UP_WIZARD_PHASE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose your wizard deck");
            texts.add("Available decks:"+GB.getAvailableWizards());
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("This wizard has already been chosen! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },
    SET_UP_TOWER_PHASE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose your team color: ");
            texts.add("Available towers:"+GB.getAvailableTowers());
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("This tower has already been chosen! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },PLAY_ASSISTANT_CARD(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose an assistant card!");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return getCLIContextMessage(GB);
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("The chosen card is not available! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ASSISTANT));
        }
    },
    MOVE_FROM_LOBBY(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            int playersNumber = GB.getNumberOfPlayers();
            if (playersNumber==Constants.MIN_NUMBER_OF_PLAYERS){
                texts.add("Choose three students to move from your lobby to your table or an island.");
                texts.add("For example, write move x,y,z to -1,1,2 to move x to the table, y to island 1 and z to island 2");
            }
            else if (playersNumber==Constants.MAX_NUMBER_OF_PLAYERS){
                texts.add("Choose four students to move from your lobby to your table or an island.");
                texts.add("For example, write move x,y,w,z, to -1,-1,1,2 to move x and y to the table, w to island 1 and z to island 2");
            }

            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by writing play personality x, with x cardId.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose three students to move from your lobby to your table or an island.");
            texts.add("Drag and drop them on the destination. Eventually click CONFIRM");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by clicking on it! Eventually click CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList<>(List.of(Clickable.LOBBY_STUDENT, Clickable.PERSONALITY));
        }
    },
    MOVE_MOTHER_NATURE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("You can now move Mother Nature.");
            texts.add("For example write move mn 5 to move her 5 islands further");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by writing play personality x, with x cardId.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("You can now move Mother Nature by clicking on the destination island. Eventually click CONFIRM.");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by clicking on it! Eventually click CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND, Clickable.PERSONALITY));
        }
    },
    PICK_CLOUD(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Pick a cloud! Its students will be moved on your lobby.");
            texts.add("For example write empty cloud 1 to pick cloud 1");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by writing play personality x, with x cardId.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Pick a cloud! Its students will be moved on your lobby.");
            texts.add("Click on the cloud and then CONFIRM");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by clicking on it! Eventually click CONFIRM.");
            return texts;
        }


        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.CLOUD, Clickable.PERSONALITY));
        }
    },
    END_TURN(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("That's the end of your turn! To close it write end");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by writing play personality x, with x cardId.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("That's the end of your turn! To close it click END TURN");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("You can also play a personality card by clicking on it! Eventually click END TURN.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY));
        }
    },
    CHOOSE_STUDENT_FOR_CARD_1(1){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a student! Write move 1 in 1 to move the first card student on island 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a student! Drag and drop on the destination island and click CONFIRM");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT));
        }
    },
    CHOOSE_ISLAND_FOR_CARD_3(3){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose an island! Write influence on 1 to compute influence on island 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Click on the chosen island and press CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND));
        }
    },
    CHOOSE_ISLAND_FOR_CARD_5(5){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose an island! To ban island 1 write ban 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Click on the island you wish to ban and press CONFIRM");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid island choice");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND));
        }
    },
    SWAP_STUDENTS_FOR_CARD_7(7){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose up to three card students to swap them with as many students from your lobby.");
            texts.add("Write swap x,y with w,z to make them move!");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose up to three card students by clicking on them to swap them with as many students from your lobby.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Illegal swap!");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT,Clickable.LOBBY_STUDENT));
        }
    },
    CHOOSE_COLOR_FOR_CARD_9(9){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a color to ban! For example to ban red write ban red");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a color to ban!");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("This color cannot be chosen");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.COLOR));
        }
    },
    CHOOSE_STUDENTS_FOR_CARD_10(10){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose up to two students from your table and as many from your lobby.");
            texts.add("Write swap red,pink with 1,2 to make them move!");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose up to two students from your table and as many from your lobby by clicking on them.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Illegal swap!");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.LOBBY_STUDENT,Clickable.TABLE_STUDENT));
        }
    },
    CHOOSE_STUDENT_FOR_CARD_11(11){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a student to move! To move the first card students write move 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a student to move by clicking on it and then CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT));
        }
    },
    CHOOSE_COLOR_FOR_CARD_12(12){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a color to steal! Write steal pink to steal 3 pink students from every player.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Choose a color to steal!");
            return texts;

        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Invalid choice! Retry");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.COLOR));
        }
    },
    END_GAME(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("The game has ended!\n");
            if (GB.getWinner()==null)
                texts.add("Someone has disconnected");
            else if(GB.getWinner().equals(Constants.TIE))
                texts.add("The game has ended on a tie.");
            else
                texts.add("The winner is " + GB.getWinner().toUpperCase() + "!");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("The game has ended!");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            return null;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    };

    private final int optionalID;

    /**
     * Constructor creates a ClientState.
     * @param optionalID: ID given to the state: IDs different from zero are relative to expert mode.
     */
    ClientState(int optionalID) {
        this.optionalID=optionalID;
    }

    /**
     * @return the optional ID of the state.
     */
    public int getOptionalID() {
        return optionalID;
    }


    public static Optional<ClientState> valueOf(int optionalID){
        return Arrays.stream(values())
                .filter(clientState -> clientState.optionalID == optionalID)
                .findFirst();
    }

    /**
     * Method getCLIContextMessage returns a specific state's context message to print on CLI interface.
     * @param GB GameBoard instance to which CLI elements are linked.
     * @return ArrayList of strings to be printed.
     */
    public abstract ArrayList<String> getCLIContextMessage(GameBoard GB);

    /**
     * Method getGUIContextMessage returns a specific state's context message to print on GUI interface.
     * @param GB GameBoard instance to which GUI elements are linked.
     * @return ArrayList of strings to be printed.
     */
    public abstract ArrayList<String> getGUIContextMessage(GameBoard GB);

    /**
     * Method getServerErroreMessage returns a specific state's error text to be printed as a result of a server error.
     * @return ArrayList of strings to be printed.
     */
    public abstract ArrayList<String> getServerErrorMessage();

    /**
     * Method getInputErrorMessage returns a specific state's error text to be printed as a result of an input error.
     * @return ArrayList of strings to be printed.
     */
    public ArrayList<String> getInputErrorMessage(){
        ArrayList<String> texts = new ArrayList<>();
        texts.add("Invalid input! Retry.");
        return texts;
    }

    /**
     * Method getClickableList returns the list of Clickable elements representing items that can be clicked during a specific client state.
     * @return ArrayList of Clickable elements for the client state.
     */
    public abstract ArrayList<Clickable> getClickableList();

}
