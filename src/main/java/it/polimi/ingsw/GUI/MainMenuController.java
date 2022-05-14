package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController extends GUIController {

    @FXML
    private Button closeButton;

    @FXML
    void openConnectionMenu(ActionEvent event) {
        gui.setScene(1);
    }

    @FXML
    void quit(ActionEvent event) {
        System.exit(0);
    }


    @FXML
    void connect(ActionEvent event){

    }

}