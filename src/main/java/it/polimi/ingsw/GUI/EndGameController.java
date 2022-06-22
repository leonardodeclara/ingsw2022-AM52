package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EndGameController extends GUIController implements UpdatableController{

    //l'idea è che se la partita è finita perché ha vinto qualcuno stampo a video il risultato
    @FXML
    public void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void update() {

    }

    @Override
    public void start() {

    }

    @Override
    public void setWaitTurn(boolean value) {

    }
}
