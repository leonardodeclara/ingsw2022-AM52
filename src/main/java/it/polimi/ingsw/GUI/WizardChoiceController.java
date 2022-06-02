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
    boolean alreadyPressed = false;

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
        if(!alreadyPressed){
            int wizardValue = wizardChoiceBox.getSelectionModel().getSelectedItem();
            //per ora non serve currentState perchè questo controller non si occupa di altro
            Message builtMessage = client.buildMessageFromPlayerInput(actionParser.parseWizardChoice(wizardValue), ClientState.SET_UP_WIZARD_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }

}
