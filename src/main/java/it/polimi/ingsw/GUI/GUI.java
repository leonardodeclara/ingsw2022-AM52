package it.polimi.ingsw.GUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.messages.ClientState;
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

/*
JavaFX defines a scene graph which is a tree data structure that has a single root node.
For your application (i.e. the code you posted), the root node is the primaryStage (this is the parameter in method start() in class Main).
The primaryStage can have several Scenes. Each Scene must have its own root node.
 */
public class GUI extends Application {
    ClientState currentState;
    boolean active;
    Stage stage;
    Scene MainMenu;

    public GUI(){
        currentState = ClientState.CONNECT_STATE;
        active = true;
    }

    private void renderScene(){
        //renderBackground(); //il background lo renderizziamo sempre
        switch(currentState){
            case CONNECT_STATE:
                //renderConnectWindow();
        }
    }


    public void setupScenes() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(Constants.MainMenuFxmlPath));
        MainMenu = new Scene(root);
    }

    public void setScene(Scene scene){
        stage.setScene(scene);
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

        setScene(MainMenu);

    }

    public static void main(String[] args) {
        launch(args);
    }

}