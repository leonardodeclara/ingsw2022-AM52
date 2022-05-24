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
        int numOfPlayersValue = Integer.parseInt(numOfPlayers.getSelectionModel().getSelectedItem());
        String gameTypeString = gameType.getSelectionModel().getSelectedItem();
        boolean isExpert = (!gameTypeString.equals("Basic Rules"));
        ArrayList<Object> data = new ArrayList<>();
        data.add(numOfPlayersValue);
        data.add(isExpert);
        //per ora non serve currentState perchè questo controller non si occupa di altro
        Message builtMessage = client.buildMessageFromPlayerInput(data, ClientState.INSERT_NEW_GAME_PARAMETERS);
        gui.passToSocket(builtMessage);
    }



}
