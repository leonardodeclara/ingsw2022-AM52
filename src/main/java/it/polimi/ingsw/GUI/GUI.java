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


public class GUI extends Application implements UI{
    ClientState currentState;
    boolean active;
    Stage stage;
    ArrayList<Scene> scenes;
    ArrayList<GUIController> controllers;
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
        scenes = new ArrayList<>();
        controllers = new ArrayList<>();
        client = new Client(this);
        actionParser = new ActionParser();
        GB = new GameBoard();
        greyOverlay = new ImageView();
        greyOverlay.setImage(new Image("graphics/wait_gray_overlay.png"));
    }

    public void handleMessageFromServer(Message message){ //quando arriva il messaggio viene gestito come sulla CLI e viene cambiata/aggiornata la scena
        if(message instanceof ClientStateMessage){
            currentState = ((ClientStateMessage) message).getNewState();
            System.out.println("Vado nello stato "+currentState);
            renderScene(); //update scene in funzione del nuovo stato
        }else{ //il metodo update dei controller va chiamato manualmente. Si potrebbe pensare a un propertylistener
            ((UpdateMessage) message).update(GB); //aggiorna la gameboard
            GUIController currentController = controllers.get(scenes.indexOf(stage.getScene()));
            if(currentController instanceof UpdatableController) //Se l'update deve aggiornare anche la scena allora lo fa, altrimenti l'aggiornamento è propagato solo su GB
                ((UpdatableController) currentController).update();
        }
    }


    public void prepareView(ArrayList<Object> data){

    }

    private void renderScene(){ //nelle fasi più avanzate si aggiornerà la scena aggiungendo immagini o altro ma non si chiamerà più setScene
        switch (currentState) {
            case INSERT_NEW_GAME_PARAMETERS -> {
                setScene(3);
            }
            case WAIT_IN_LOBBY -> setScene(4);
            case WAIT_TURN -> {
                if (scenes.indexOf(stage.getScene()) == 3 || scenes.indexOf(stage.getScene()) == 4) //porcata imbarazzante
                    setScene(5);
                disableScene();
            }
            case SET_UP_WIZARD_PHASE -> {
                setScene(5);
                enableScene();
            }
            case SET_UP_TOWER_PHASE -> {
                setScene(6);
            }
        }
    }


    public void setupScenes() throws IOException {
        for(String path : fxmlPaths){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(fxmlLoader.load());
            scenes.add(scene);
            GUIController controller = fxmlLoader.getController();
            controller.setGUI(this);
            controller.setClient(this.client);
            controller.setActionParser(this.actionParser);
            controllers.add(controller);
        }
    }


    public void setScene(int index){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setScene(scenes.get(index));
                stage.show();
                GUIController currentController = controllers.get(index);
                if(currentController instanceof UpdatableController) //se la scena è aggiornabile con messaggi di update va inizializzata
                    ((UpdatableController)currentController).start(); //inizializzazione (non può essere fatta altrove)
            }
        });
    }

    public void disableScene(){ //ingrigisce la GUI dinamicamente (indipendentemente dalla scena renderizzata) e scrive ATTENDI IL TUO TURNO... al centro
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((AnchorPane)stage.getScene().getRoot()).getChildren().add(greyOverlay);
            }
        });
    }
//versione fancy: si itera su ogni elemento della scena e si fa GRAY = R + G +B / 3 o roba simile
    public void enableScene(){ //riabilita la GUI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((AnchorPane)stage.getScene().getRoot()).getChildren().remove(greyOverlay);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        Image icon = new Image("/graphics/logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("Eryantis");
        stage.setResizable(false);

        setupScenes();

        setScene(0);

    }

    public void connect(String ip,String portText) throws IOException {
        int port;
        try{
            port = Integer.parseInt(portText);
            clientSocket = new ClientSocket(ip,port,this);
            Thread socketThread = new Thread(clientSocket); //la sposti su un nuovo thread (parte run() in automatico)
            socketThread.start();
            setScene(2);
        }catch(NumberFormatException | UnknownHostException | SocketException e){
            //renderizza qualche messaggio di errore
        }

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