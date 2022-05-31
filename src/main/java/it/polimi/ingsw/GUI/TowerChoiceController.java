package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;

public class TowerChoiceController extends GUIController implements UpdatableController{
    @FXML
    ChoiceBox<String> towerChoiceBox;

    public void start(){
        ObservableList<String> towers = FXCollections.observableArrayList();
        ArrayList<Tower> availableTowers = gui.getAvailableTowers();
        towers.addAll(convertTowersToString(availableTowers));
        towerChoiceBox.setItems(towers);
    }

    private ArrayList<String> convertTowersToString(ArrayList<Tower> towers){
        ArrayList<String> stringTowers = new ArrayList<>();
        for(Tower tower : towers){
            if(tower.equals(Tower.BLACK))
                stringTowers.add("Black");
            else if(tower.equals(Tower.GREY))
                stringTowers.add("Grey");
            else
                stringTowers.add("White");
        }
        return stringTowers;
    }

    public void update(){
        ObservableList<String> towers = FXCollections.observableArrayList();
        ArrayList<Tower> availableTowers = gui.getAvailableTowers();
        towers.addAll(convertTowersToString(availableTowers));
        towerChoiceBox.setItems(towers);
    }

    public void send(){
        int towerValue = Integer.parseInt(towerChoiceBox.getSelectionModel().getSelectedItem());
        ArrayList<Object> data = new ArrayList<>();
        data.add(towerValue);
        //per ora non serve currentState perchè questo controller non si occupa di altro
        Message builtMessage = client.buildMessageFromPlayerInput(data, ClientState.SET_UP_TOWER_PHASE);
        gui.passToSocket(builtMessage);
    }
}
