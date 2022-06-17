package it.polimi.ingsw.GUI;

import it.polimi.ingsw.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class MainMenuController extends GUIController {

    @FXML
    private Button closeButton;

    @FXML
    void openConnectionMenu(ActionEvent event) {
        gui.openConnectMenu();
    }

    @FXML
    void quit(ActionEvent event) {
        System.exit(0);
    }


}