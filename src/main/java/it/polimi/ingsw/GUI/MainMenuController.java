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
    void openConnectionMenu(MouseEvent event) {
        gui.openConnectMenu();
    }

    @FXML
    void quit(MouseEvent event) {
        System.exit(0);
    }


}