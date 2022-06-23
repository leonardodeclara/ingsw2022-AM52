package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;

public class ConnectMenuController extends GUIController{
    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private Button connectButton;

    @FXML
    public void initialize(){
        connectButton.setEffect(null);
        connectButton.setText("CONNECT");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 10);
        connectButton.setFont(font);
        connectButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {

            connectButton.setEffect(new Bloom());

        });
        connectButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            connectButton.setEffect(null);
        });
    }

    @FXML
    private void connect(ActionEvent event) throws IOException {
        gui.connect(ip.getText(),port.getText());
    }


}
