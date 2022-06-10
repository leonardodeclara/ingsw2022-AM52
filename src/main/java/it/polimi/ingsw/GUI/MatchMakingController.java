package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.ArrayList;

public class MatchMakingController extends GUIController{
    @FXML
    private ChoiceBox<String> numOfPlayers;
    @FXML
    private ChoiceBox<String> gameType;

    private boolean alreadyPressed = false;

    @FXML
    public void initialize(){
        ObservableList<String> playerAvailableChoices = FXCollections.observableArrayList();
        ObservableList<String> gameAvailableChoices = FXCollections.observableArrayList();
        playerAvailableChoices.addAll("2", "3");
        gameAvailableChoices.addAll("Basic Rules","Expert Rules");
        numOfPlayers.setItems(playerAvailableChoices);
        gameType.setItems(gameAvailableChoices);
    }

    public void searchGame(){
        if(!alreadyPressed){
            int numOfPlayersValue = Integer.parseInt(numOfPlayers.getSelectionModel().getSelectedItem());
            String gameTypeString = gameType.getSelectionModel().getSelectedItem();
            boolean isExpert = (!gameTypeString.equals("Basic Rules"));
            //per ora non serve currentState perchè questo controller non si occupa di altro se non questo
            ArrayList<Object> selectedMode = actionParser.parseNewGameParameters(numOfPlayersValue,isExpert);

            Message builtMessage = client.buildMessageFromPlayerInput(selectedMode, ClientState.INSERT_NEW_GAME_PARAMETERS);
            gui.passToSocket(builtMessage);
            gui.prepareView(selectedMode);
            alreadyPressed = true;
        }
    }



}
