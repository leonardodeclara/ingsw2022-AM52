package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.CLI.*;
import it.polimi.ingsw.client.GUI.GUIControllers.GUIController;
import it.polimi.ingsw.client.GUI.GUIControllers.UpdatableController;
import it.polimi.ingsw.client.ClientMessageBuilder;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.UpdateMessages.ActivePersonalityMessage;
import it.polimi.ingsw.messages.ServerMessages.ClientStateMessage;
import it.polimi.ingsw.messages.ServerMessages.ErrorMessage;
import it.polimi.ingsw.messages.UpdateMessages.*;
import it.polimi.ingsw.model.Tower;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.polimi.ingsw.Constants.*;

/**
 * Class GUI represents the main class for the Client Graphic Interface component. It initializes and updates the scene as well as handling messages coming
 * from ClientSocket
 */
public class GUI extends Application implements UI{
    private ClientState currentState;
    private Stage stage;
    private final HashMap<String,Scene> scenes;
    private final HashMap<Scene, GUIController> controllers;
    private final String[] fxmlPaths;
    private ClientSocket clientSocket;
    private final ClientMessageBuilder clientMessageBuilder;
    private final ActionParser actionParser;
    private final GameBoard GB;
    private final ImageView greyOverlay;
    private int wizardID;
    private Tower team;
    private Font gameFont;

    /**
     * Constructor GUI creates a new GUI instance.
     */
    public GUI(){
        currentState = ClientState.CONNECT_STATE;
        fxmlPaths = Arrays.copyOf(Constants.fxmlPaths,Constants.fxmlPaths.length);
        scenes = new HashMap<>();
        controllers = new HashMap<>();
        clientMessageBuilder = new ClientMessageBuilder(this);
        actionParser = new ActionParser();
        GB = new GameBoard();
        greyOverlay = new ImageView();
        greyOverlay.setImage(new Image("graphics/wait_gray_overlay.png"));
    }


    /**
     * Method handleMessageFromServer receives a Message instance from ClientSocket and react accordingly to the message type.
     * @param message received from server.
     * If the message is instance of ClientStateMessage, handleMessageFromServer changes the current client state and if the current scene is updatable, it gets updated
     * If the message is instance of ErrorMessage, handleMessageFromServer calls the current scene's controller error handling method
     * If the message is instance of UpdateMessage, handleMessageFromServer updates the GameBoard instance and updates the scene if it's in WAIT_TURN state
     * Since this method is called from another thread, Platform.runLater() ensures that the method is executed by this (GUI) thread to avoid run time exceptions
     */
    public void handleMessageFromServer(Message message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(message instanceof ClientStateMessage){
                    currentState = ((ClientStateMessage) message).getNewState();
                    renderScene();
                    GUIController currentController = controllers.get(stage.getScene());
                    if(currentController instanceof UpdatableController){
                        ((UpdatableController) currentController).update();
                    }
                }
                else if (message instanceof ErrorMessage){
                    GUIController currentController = controllers.get(stage.getScene());
                    currentController.handleErrorMessage(true);
                }
                else {
                    ((UpdateMessage) message).update(GB);
                    GUIController currentController = controllers.get(stage.getScene());
                    if(currentController instanceof UpdatableController && currentState.equals(ClientState.WAIT_TURN)){
                        if(message instanceof ActivePersonalityMessage
                                || message instanceof CloudUpdateMessage
                                || message instanceof IslandMergeUpdateMessage
                                || message instanceof IslandStudentsUpdateMessage
                                || message instanceof IslandTowersUpdateMessage
                                || message instanceof MotherNatureMovementUpdateMessage)
                        ((UpdatableController) currentController).update();
                    }
                }
            }
        });

    }

    /**
     * Method prepareView initializes the GameBoard with the game parameters (isExpert and numberOfPlayers)
     * @param data received from MatchMaking controller when the player has sent game parameters
     */
    public void prepareView(ArrayList<Object> data){
        GB.setNumberOfPlayers((Integer)data.get(0));
        GB.setExpertGame((Boolean)data.get(1));
    }
    /**
     * Method renderScene sets the scene accordingly to the currentState and eventually renders the wait gray screen overlay
     * In WAIT_TURN state it also does some additional operations to assure that the next scene is loaded in the correct moment
     */
    private void renderScene(){
        switch (currentState) {
            case INSERT_NEW_GAME_PARAMETERS -> {
                setScene(Constants.MATCHMAKING_MENU_FXML);
            }
            case WAIT_IN_LOBBY -> setScene(Constants.LOBBY_FXML);
            case WAIT_TURN -> {
                Scene currentScene = stage.getScene();
                if (currentScene.equals(scenes.get(Constants.MATCHMAKING_MENU_FXML)) || currentScene.equals(scenes.get(Constants.LOBBY_FXML)))
                    setScene(Constants.WIZARD_CHOICE_FXML);
                if (currentScene.equals(scenes.get(Constants.TOWER_CHOICE_FXML))){
                    setScene(Constants.GAME_TABLE_FXML);
                    stage.centerOnScreen();
                }

                setSceneShouldWait(true);

            }
            case SET_UP_WIZARD_PHASE -> {
                setScene(Constants.WIZARD_CHOICE_FXML);
                setSceneShouldWait(false);
            }
            case SET_UP_TOWER_PHASE -> {
                setScene(Constants.TOWER_CHOICE_FXML);
            }
            case PLAY_ASSISTANT_CARD ->{
                setScene(Constants.GAME_TABLE_FXML);

                setSceneShouldWait(false);
            }
            case MOVE_FROM_LOBBY -> {
                setSceneShouldWait(false);
            }
            case END_GAME -> {
                setSceneShouldWait(true);
                GUIController currentController = controllers.get(stage.getScene());
                if(currentController instanceof UpdatableController)
                    ((UpdatableController) currentController).endGame();
            }
        }
    }

    /**
     * Method setupScenes initializes all the scenes and controllers, saving them in <FXML Path, Scene> and <Scene, Controller> hashmaps
     */
    public void setupScenes() throws IOException {
        for(String path : fxmlPaths){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(fxmlLoader.load());
            scenes.put(path,scene);
            GUIController controller = fxmlLoader.getController();
            controller.setGUI(this);
            controller.setClient(this.clientMessageBuilder);
            controller.setActionParser(this.actionParser);
            controllers.put(scene,controller);
        }
    }

    /**
     * Method setScene changes the current scene accordingly to the fxml path received as parameter
     * @param path represents the FXML path of the scene to load
     */
    public void setScene(String path){
      stage.setScene(scenes.get(path));
      stage.show();
      GUIController currentController = controllers.get(stage.getScene());
      if(currentController instanceof UpdatableController){
          ((UpdatableController)currentController).start();
      }
    }

    /**
     * Method setSceneShouldWait calls the current scene's controller waitTurn setter, so that the update() method inside controller "knows"
     * if the wait overlay should be rendered or not.
     * @param value is true if the wait overlay should be added, false if it should be removed
     */
    private void setSceneShouldWait(boolean value){
        GUIController controller = controllers.get(stage.getScene());
        try{
            ((UpdatableController) controller).setWaitTurn(value);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Method disableScene adds to the stage the gray overlay.
     */
    public void disableScene(){
       if(!((AnchorPane)stage.getScene().getRoot()).getChildren().contains(greyOverlay)){
           ((AnchorPane)stage.getScene().getRoot()).getChildren().add(greyOverlay);
       }
        greyOverlay.toFront();
    }
    /**
     * Method enableScene removes from the stage the gray overlay.
     */
    public void enableScene(){ //riabilita la GUI
        ((AnchorPane)stage.getScene().getRoot()).getChildren().remove(greyOverlay);
    }

    /**
     * Method start sets the stage reference,sets the resolution, adds the icon to the window,
     * sets up all the scenes and the game font and finally renders the main menu
     * @param primaryStage is the GUI instance stage
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setOnCloseRequest(event->System.exit(0));

        Image icon = new Image("/graphics/logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("Eryantis");
        stage.setResizable(false);

        setupScenes();

        setScene(MAIN_MENU_FXML);

        gameFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 14);




    }


    /**
     * Method connect attempt a connection to the server by initializing a socket on a new thread and setting up the Nickname Menu scene
     * @param ip represents the server ip
     * @param portText represent the port the server is listening on converted to String type
     */
    public void connect(String ip, String portText) throws IOException {
        int port;
        try{
            port = Integer.parseInt(portText);
            clientSocket = new ClientSocket(ip,port,this);
            Thread socketThread = new Thread(clientSocket);
            socketThread.start();
            setScene(Constants.NICKNAME_MENU_FXML);
        }catch(NumberFormatException | UnknownHostException | SocketException e){
            GUIController controller = controllers.get(stage.getScene());
            controller.handleErrorMessage(true);
        }

    }

    public void openConnectMenu(){ //chiamato da MainMenuController quando si preme PLAY
        setScene(Constants.CONNECT_MENU_FXML);
    }

    /**
     * Method passToSocket sends a Message instance to the clientSocket instance so that it can be sent to the server
     * @param message represents the message to send
     */
    public void passToSocket(Message message){
        try {
            clientSocket.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method handleClosingServer removes the WAIT_TURN gray overlay
     * and calls the end game procedures of the current scene's controller
     * Since this method is called from another thread, Platform.runLater() ensures that the method is executed by this (GUI) thread to avoid run time exceptions
     */
    @Override
    public void handleClosingServer() {
        Platform.runLater(()->{
            GUIController currentController = controllers.get(stage.getScene());
            disableScene();
            if(currentController instanceof UpdatableController)
                ((UpdatableController) currentController).endGame();
        });
    }

    public int getNumOfPlayers(){
        return GB.getNumberOfPlayers();
    }

    public ClientState getCurrentState() {
        return currentState;
    }

    public ArrayList<Integer> getAvailableWizards(){
        return GB.getAvailableWizards();
    }

    public ArrayList<Tower> getAvailableTowers(){
        return GB.getAvailableTowers();
    }

    public ArrayList<ClientIsland> getIslands(){
        return GB.getIslands();
    }

    public ArrayList<ClientCloud> getClouds(){
        return GB.getClouds();
    }

    public void setWizard(int wizard){ //chiamato dal controller quando fa send
        wizardID = wizard;
    }

    public void setTeam(Tower team){
        this.team = team;
    }

    public HashMap<Integer,Integer> getDeck(){
        String playerNickname = GB.getNickname();
        return GB.getClientBoards().get(playerNickname).getDeck();
    }

    public int getWizard(){
        return wizardID;
    }

    public String getPlayerNickname(){return GB.getNickname();}

    public void setPlayerNickname(String stringNickname) {
        GB.setNickname(stringNickname);
    }

    public List<String> getOtherPlayers(){
        List<String> players = GB.getPlayersNicknames();
        players.remove(GB.getNickname());
        return players;
    }

    public GameBoard getGB() {
        return GB;
    }

    public HashMap<String,Integer> getTurnCards(){
        return GB.getTurnCards();
    }

    public ArrayList<ClientBoard> getClientBoards(){
        return new ArrayList<>(GB.getClientBoards().values());
    }
    public ClientBoard getPlayerBoard(String playerNickname){
        return GB.getClientBoards().get(playerNickname);
    }
    public ClientBoard getOwningPlayerBoard(){return GB.getOwningPlayerClientBoard();}
    public ArrayList<ClientPersonality> getPersonalityCards(){
        return GB.getPersonalities();
    }

    public double getScreenX(){
        return ((AnchorPane)stage.getScene().getRoot()).getWidth();
    }

    public double getScreenY(){
        return ((AnchorPane)stage.getScene().getRoot()).getHeight();
    }

    public void addElementToScene(Node n){
        ((AnchorPane)stage.getScene().getRoot()).getChildren().add(n);
    }
    public String getWinner(){return GB.getWinner();}
    public Font getGameFont(){
        return gameFont;
    }
    public void removeElementFromScene(Node n){
        ((AnchorPane)stage.getScene().getRoot()).getChildren().remove(n);
    }





    public static void main(String[] args) {
        launch(args);
    }


}