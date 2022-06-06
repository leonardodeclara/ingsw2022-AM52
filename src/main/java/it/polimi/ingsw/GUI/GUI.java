package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.ClientStateMessage;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Tower;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class GUI extends Application implements UI{
    ClientState currentState;
    boolean active;
    Stage stage;
    HashMap<String,Scene> scenes;
    HashMap<Scene,GUIController> controllers;
    String[] fxmlPaths;
    ClientSocket clientSocket;
    Client client;
    ActionParser actionParser;
    GameBoard GB;
    ImageView greyOverlay;

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
                    System.out.println("La scena attuale è "+stage.getScene() + " e il controller "+currentController);
                    if(currentController instanceof UpdatableController){  //Se l'update deve aggiornare anche la scena allora lo fa, altrimenti l'aggiornamento è propagato solo su GB
                        ((UpdatableController) currentController).update();
                        System.out.println("Aggiorno il controller "+currentController);
                    }
                }else{
                    ((UpdateMessage) message).update(GB); //aggiorna la gameboard
                }
            }
        });

    }


    public void prepareView(ArrayList<Object> data){

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
                if (currentScene.equals(scenes.get(Constants.TOWER_CHOICE_FXML)))
                    setScene(Constants.GAME_TABLE_FXML);

                disableScene();
            }
            case SET_UP_WIZARD_PHASE -> { //mancano overlay di selezione e cornici intorno alle immagini
                setScene(Constants.WIZARD_CHOICE_FXML);
                enableScene();
            }
            case SET_UP_TOWER_PHASE -> { //mancano overlay di selezione
                setScene(Constants.TOWER_CHOICE_FXML);
            }
            case PLAY_ASSISTANT_CARD ->{
                setScene(Constants.GAME_TABLE_FXML); //ancora non operativa
            }
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
      if(currentController instanceof UpdatableController) //se la scena è aggiornabile con messaggi di update va inizializzata
        ((UpdatableController)currentController).start(); //inutilizzato per ora (basta il metodo update) ma non rimuovere finchè non si è sicuri che non serva

    }

    public void disableScene(){ //ingrigisce la GUI dinamicamente (indipendentemente dalla scena renderizzata) e scrive ATTENDI IL TUO TURNO... al centro
       if(!((AnchorPane)stage.getScene().getRoot()).getChildren().contains(greyOverlay))  //sistemare e aggiungere scritta
           ((AnchorPane)stage.getScene().getRoot()).getChildren().add(greyOverlay);
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

        Image icon = new Image("/graphics/logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("Eryantis");
        stage.setResizable(false);

        setupScenes();

        setScene(Constants.MAIN_MENU_FXML);


    }

    public void connect(String ip,String portText) throws IOException { //chiamato da ConnectMenuController quando si preme Connect
        int port;
        try{
            port = Integer.parseInt(portText);
            clientSocket = new ClientSocket(ip,port,this);
            Thread socketThread = new Thread(clientSocket); //la sposti su un nuovo thread (parte run() in automatico)
            socketThread.start();
            setScene(Constants.NICKNAME_MENU_FXML);
        }catch(NumberFormatException | UnknownHostException | SocketException e){
            //renderizza qualche messaggio di errore
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





























    public static void main(String[] args) {
        launch(args);
    }

}