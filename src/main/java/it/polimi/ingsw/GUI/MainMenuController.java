package it.polimi.ingsw.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;

public class MainMenuController extends GUIController {

    @FXML
    private Button closeButton;
    @FXML
    private Button sendButton;

    @FXML
    public void addHoverEffectPlay(){
        sendButton.setOnMouseEntered(e -> sendButton.setEffect(new Bloom()));
    }
    @FXML
    public void addHoverEffectQuit(){
        closeButton.setOnMouseEntered(e -> closeButton.setEffect(new Bloom()));
    }

    @FXML
    public void resetEffectPlay(){
        sendButton.setOnMouseExited(e -> sendButton.setEffect(null));
    }
    @FXML
    public void resetEffectQuit(){
        closeButton.setOnMouseExited(e -> closeButton.setEffect(null));
    }

    @FXML
    public void mousePressedPlay(){
        resetEffectPlay();
        sendButton.setOnMouseEntered(e -> sendButton.setEffect(new DropShadow()));
    }

    @FXML
    public void mousePressedQuit(){
        resetEffectQuit();
        closeButton.setOnMouseEntered(e -> closeButton.setEffect(new DropShadow()));
    }
    @FXML
    void openConnectionMenu(MouseEvent event) {
        gui.openConnectMenu();
    }

    @FXML
    void quit(MouseEvent event) {
        System.exit(0);
    }


}