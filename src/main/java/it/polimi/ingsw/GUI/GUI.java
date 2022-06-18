package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.polimi.ingsw.Constants.*;


public class GUI extends Application implements UI{
    private ClientState currentState;
    private boolean active;
    private Stage stage;
    private HashMap<String,Scene> scenes;
    private HashMap<Scene,GUIController> controllers;
    private String[] fxmlPaths;
    private ClientSocket clientSocket;
    private Client client;
    private ActionParser actionParser;
    private GameBoard GB;
    private ImageView greyOverlay;
    private int wizardID; //andrebbero messe in GameBoard ma è una sbatta
    private Tower team; //andrebbero messe in GameBoard ma è una sbatta
    private Font gameFont;


    //TODO aggiungere pulsanti belli
    //TODO sistemare wait screen (problemi di render order quando togliamo setSceneShouldWait() e lo sostituiamo con enable e disable)
    //TODO aggiungere send di messaggio di Disconnect quando si chiude la finestra
    //TODO aggiungere schermata ENDGAME

    //la GUI si occupa dell'aggiunta e rimozione degli elementi dinamici (compreso wait screen). lo facciamo qui per una questione di duplicazione codice
    //dato che abbiamo almeno 3 controller che sfruttano i metodi di aggiunta e rimozione.
    //ridichiararli all'interno di ogni singolo controller sarebbe tempo sprecato e non darebbe alcun vantaggio concreto secondo me
    public GUI(){
        currentState = ClientState.CONNECT_STATE;
        active = true;
        fxmlPaths = Arrays.copyOf(Constants.fxmlPaths,Constants.fxmlPaths.length);
        scenes = new HashMap<>();
        controllers = new HashMap<>();
        client = new Client(this);
        actionParser = new ActionParser();
        GB = new GameBoard();
        greyOverlay = new ImageView();
        greyOverlay.setImage(new Image("graphics/wait_gray_overlay.png"));
    }

    public void handleMessageFromServer(Message message){ //quando arriva il messaggio viene gestito come sulla CLI e viene cambiata/aggiornata la scena
        Platform.runLater(new Runnable() { //il thread client socket non l'auth per lavorare con javaFX, quindi tutto l'aggiornamento della GUI avviene dopo nel thread ad hoc
            @Override
            public void run() {
                if(message instanceof ClientStateMessage){
                    currentState = ((ClientStateMessage) message).getNewState();
                    System.out.println("Vado nello stato "+currentState);
                    renderScene(); //update scene in funzione del nuovo stato
                    GUIController currentController = controllers.get(stage.getScene());
                    //forse sarebbe comodo mettere currentController come attributo di gui
                    System.out.println("La scena attuale è "+stage.getScene() + " e il controller "+currentController);
                    if(currentController instanceof UpdatableController){  //Se l'update deve aggiornare anche la scena allora lo fa, altrimenti l'aggiornamento è propagato solo su GB
                        ((UpdatableController) currentController).update();
                        System.out.println("Aggiorno il controller "+currentController);
                    }
                }
                else if (message instanceof ErrorMessage){
                    System.out.println("Errore! ");
                    GUIController currentController = controllers.get(stage.getScene());
                    currentController.handleErrorMessage(true);
                    //si potrebbe aggiungere all'interfaccia GUIController un metodo handleErrorMessage
                    // che permette di gestire in maniera diverso il messaggio di errore
                }
                else {
                    ((UpdateMessage) message).update(GB); //aggiorna la gameboard
                }
            }
        });

    }


    public void prepareView(ArrayList<Object> data){
        GB.setNumberOfPlayers((Integer)data.get(0));
        GB.setExpertGame((Boolean)data.get(1));
    }

    private void renderScene(){ //nelle fasi più avanzate si aggiornerà la scena aggiungendo immagini o altro ma non si chiamerà più setScene
        switch (currentState) {
            case INSERT_NEW_GAME_PARAMETERS -> { //vanno usati i pulsanti nella cartella graphics per tutti quelli presenti in game
                setScene(Constants.MATCHMAKING_MENU_FXML);
            }
            case WAIT_IN_LOBBY -> setScene(Constants.LOBBY_FXML);
            case WAIT_TURN -> {
                Scene currentScene = stage.getScene();
                if (currentScene.equals(scenes.get(Constants.MATCHMAKING_MENU_FXML)) || currentScene.equals(scenes.get(Constants.LOBBY_FXML)))
                    setScene(Constants.WIZARD_CHOICE_FXML);
                if (currentScene.equals(scenes.get(Constants.TOWER_CHOICE_FXML))){
                    setScene(Constants.GAME_TABLE_FXML);
                    stage.centerOnScreen();//vedere se si può fare in maniera diversa
                }

                setSceneShouldWait(true); //sarebbe meglio chiamare direttamente enableScene e disableScene ma dava problemi (probabilmente per il render order)
                //rivedere

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
            case END_GAME -> {}
        }
    }


    public void setupScenes() throws IOException {
        for(String path : fxmlPaths){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(fxmlLoader.load());
            scenes.put(path,scene);
            GUIController controller = fxmlLoader.getController();
            controller.setGUI(this);
            controller.setClient(this.client);
            controller.setActionParser(this.actionParser);
            controllers.put(scene,controller);
        }
    }


    public void setScene(String path){
      stage.setScene(scenes.get(path));
      stage.show();
      GUIController currentController = controllers.get(stage.getScene());
      if(currentController instanceof UpdatableController){
          ((UpdatableController)currentController).start(); //inutilizzato per ora (basta il metodo update) ma non rimuovere finchè non si è sicuri che non serva
      }
    }

    private void setSceneShouldWait(boolean value){ //si potrebbe chiamare da renderScene direttamente disable ed enable ma così penso sia p
        GUIController controller = controllers.get(stage.getScene());
        try{
            ((UpdatableController) controller).setWaitTurn(value);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void disableScene(){ //ingrigisce la GUI dinamicamente (indipendentemente dalla scena renderizzata) e scrive ATTENDI IL TUO TURNO... al centro
       if(!((AnchorPane)stage.getScene().getRoot()).getChildren().contains(greyOverlay)){
           ((AnchorPane)stage.getScene().getRoot()).getChildren().add(greyOverlay);
       }
        greyOverlay.toFront();
    }

//versione fancy: si itera su ogni elemento della scena e si fa GRAY = R + G +B / 3 o roba simile
    public void enableScene(){ //riabilita la GUI
        ((AnchorPane)stage.getScene().getRoot()).getChildren().remove(greyOverlay);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //game table non viene aperto nè in fullscreen (farlo non darebbe nè vantaggi nè svantaggi, è preferenza personale)
        // e il resizing automatico non è implementato per questioni di tempo
        //la risoluzione è settata manualmente con un rateo di 16:9 e una dimensione ottimale per schermi 1920x1080
        stage = primaryStage;
        stage.setOnCloseRequest(event->System.exit(0));
        //equivalente della chiusura forzata da cli, poi bisogna gestire per gli altri client il messaggio di fine partita

        Image icon = new Image("/graphics/logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("Eryantis");
        stage.setResizable(false);

        setupScenes();

        setScene(MAIN_MENU_FXML);

        gameFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 14);




    }

    public ClientState getCurrentState() {
        return currentState;
    }

    public void connect(String ip, String portText) throws IOException { //chiamato da ConnectMenuController quando si preme Connect
        int port;
        try{
            port = Integer.parseInt(portText);
            clientSocket = new ClientSocket(ip,port,this);
            Thread socketThread = new Thread(clientSocket); //la sposti su un nuovo thread (parte run() in automatico)
            socketThread.start();
            setScene(Constants.NICKNAME_MENU_FXML);
        }catch(NumberFormatException | UnknownHostException | SocketException e){
            //renderizza qualche messaggio di errore
            e.printStackTrace();
        }

    }

    public void openConnectMenu(){ //chiamato da MainMenuController quando si preme PLAY
        setScene(Constants.CONNECT_MENU_FXML);
    }

    public void passToSocket(Message message){ //chiamato dai controller
        try {
            clientSocket.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }    }

    public int getNumOfPlayers(){
        return GB.getNumberOfPlayers();
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

    public void setTeam(Tower team){ //chiamato dal controller quando fa send
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
        return new ArrayList<ClientBoard>(GB.getClientBoards().values());
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

    public Font getGameFont(){
        return gameFont;
    }
    public void removeElementFromScene(Node n){
        ((AnchorPane)stage.getScene().getRoot()).getChildren().remove(n);
    }

    public void clearScene(){
        ((AnchorPane)stage.getScene().getRoot()).getChildren().removeAll();
    }



























    public static void main(String[] args) {
        launch(args);
    }


}