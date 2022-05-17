package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConnectMenuController extends GUIController{
    @FXML
    private TextField ip;
    @FXML
    private TextField port;

    @FXML
    private void connect(ActionEvent event){
        System.out.println(ip.getText());
        System.out.println(port.getText());
    }




}
