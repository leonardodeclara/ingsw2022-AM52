package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GUIController {

    @FXML
    private Button closeButton;

    @FXML
    void connect(ActionEvent event) {

    }

    @FXML
    void quit(ActionEvent event) {
        System.exit(0);
    }

}