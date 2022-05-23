package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ConnectMenuController extends GUIController{
    @FXML
    private TextField ip;
    @FXML
    private TextField port;

    @FXML
    private void connect(ActionEvent event) throws IOException {
        gui.connect(ip.getText(),port.getText());
    }

//connessione parte da qui
//GUI si connette alla client socket inizializzandola su un thread differente e se ci riesce currentState viene fissato a CONNECT_STATE
//il render e le parti cliccabili dipendono da currentState
//dopo la connessione i controller non chiamano più GUI ma ActionParser che poi a sua volta va a chiamare Client per costruire il messaggio
//e infine qualcuno lo manda a ClientSocket



}
