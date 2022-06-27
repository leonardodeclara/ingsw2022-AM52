package it.polimi.ingsw.GUI.GUIControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

/**
 * Class MainMenuController implements all the logic behind the Main Menu FXML Scene
 * including button event handling and button styling
 */
public class MainMenuController extends GUIController {

    @FXML
    private Button quitButton;
    @FXML
    private Button sendButton;

    /**
     * Method initialize sets the style for the buttons and sets up the mouse hover event handlers
     */
    @FXML
    public void initialize() {
        sendButton.setEffect(null);
        sendButton.setText("PLAY");
        quitButton.setEffect(null);
        quitButton.setText("QUIT");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 12);
        sendButton.setFont(font);
        quitButton.setFont(font);
        sendButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> sendButton.setEffect(new Bloom()));
        sendButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> sendButton.setEffect(null));

        quitButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> quitButton.setEffect(new Bloom()));
        quitButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> quitButton.setEffect(null));
    }


    /**
     * Method openConnectionMenu calls GUI instance method to switch scene from Main Menu to Connection Menu
     */
    @FXML
    void openConnectionMenu() {
        gui.openConnectMenu();
    }

    /**
     * Method quit terminates the currently running JVM
     */
    @FXML
    void quit() {
        System.exit(0);
    }


}