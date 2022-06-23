package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.ClientState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class MainMenuController extends GUIController {

    @FXML
    private Button quitButton;
    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        sendButton.setEffect(null);
        sendButton.setText("PLAY");
        quitButton.setEffect(null);
        quitButton.setText("QUIT");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 12);
        sendButton.setFont(font);
        quitButton.setFont(font);
        sendButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {

            sendButton.setEffect(new Bloom());

        });
        sendButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            sendButton.setEffect(null);
        });

        quitButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {

            quitButton.setEffect(new Bloom());

        });
        quitButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            quitButton.setEffect(null);
        });
    }

    @FXML
    public void addHoverEffectPlay(){
        System.out.println("HOVER SU PLAY");
        //sendButton.setOnMouseEntered(e -> sendButton.setEffect(new Bloom()));
    }
    @FXML
    public void addHoverEffectQuit(){
        quitButton.setOnMouseEntered(e -> quitButton.setEffect(new Bloom()));
    }

    @FXML
    public void resetEffectPlay(){
        sendButton.setOnMouseExited(e -> sendButton.setEffect(null));
    }
    @FXML
    public void resetEffectQuit(){
        quitButton.setOnMouseExited(e -> quitButton.setEffect(null));
    }

    @FXML
    public void mousePressedPlay(){
        resetEffectPlay();
        sendButton.setOnMouseEntered(e -> sendButton.setEffect(new DropShadow()));
    }

    @FXML
    public void mousePressedQuit(){
        resetEffectQuit();
        quitButton.setOnMouseEntered(e -> quitButton.setEffect(new DropShadow()));
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