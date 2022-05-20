package it.polimi.ingsw.GUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.application.Application;
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
    String[] fxmlPaths;
    ClientSocket clientSocket;
    Client client;

    //GUI visualizza la scena
    //GUIUpdater riceve i messaggi di update da clientsocket (lo farei in GUI ma non si può perchè già estende Applications)
    //la GUI ha la ref di un ActionParser che trasforma i click in parametri sfusi e li passa a Client (che li trasforma in messaggi e li manda)
    //la GUI per comunicare con il server ha necessariamente bisogno di currentState
    //per ge
    public GUI(){
        currentState = ClientState.CONNECT_STATE;
        active = true;
        fxmlPaths = Arrays.copyOf(Constants.fxmlPaths,Constants.fxmlPaths.length);
        scenes = new ArrayList<>();
        client = new Client(this);

    }

    public void handleMessageFromServer(Message message){

    }


    public void prepareView(ArrayList<Object> data){ //con questa inizializziamo il render della partita

    }

    private void renderScene(){
        //renderBackground(); //il background lo renderizziamo sempre
        switch(currentState){
            case CONNECT_STATE:
                //renderConnectWindow();
        }
    }


    public void setupScenes() throws IOException {
        for(String path : fxmlPaths){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(fxmlLoader.load());
            scenes.add(scene);
            GUIController controller = fxmlLoader.getController();
            controller.setGUI(this);
        }
    }


    public void setScene(int index){
        stage.setScene(scenes.get(index));
        stage.show();
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
            //switcha a matchmakingscreen
        }catch(NumberFormatException | UnknownHostException | SocketException e){
            //renderizza qualche messaggio di errore
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}