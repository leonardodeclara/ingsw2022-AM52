package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;

public class WizardChoiceController extends GUIController implements UpdatableController{
    @FXML
    ChoiceBox<Integer> wizardChoiceBox;


    public void start(){
        ObservableList<Integer> wizards = FXCollections.observableArrayList();
        wizards.addAll(gui.getAvailableWizards());
        wizardChoiceBox.setItems(wizards);
    }

    public void update(){
        ObservableList<Integer> wizards = FXCollections.observableArrayList();
        wizards.addAll(gui.getAvailableWizards());
        wizardChoiceBox.setItems(wizards);
    }

    public void send(){
        int wizardValue = wizardChoiceBox.getSelectionModel().getSelectedItem();
        ArrayList<Object> data = new ArrayList<>();
        data.add(wizardValue);
        //per ora non serve currentState perchè questo controller non si occupa di altro
        Message builtMessage = client.buildMessageFromPlayerInput(data, ClientState.SET_UP_WIZARD_PHASE);
        gui.passToSocket(builtMessage);
    }

}
