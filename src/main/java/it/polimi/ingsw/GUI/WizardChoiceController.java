package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class WizardChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView wizard_1,wizard_2,wizard_3,wizard_4;
    boolean alreadyPressed = false;
    int selectedWizard = -1;
    boolean waitTurn = false;

    public void start(){
    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    public void update(){
        ArrayList<Integer> availableWizards = gui.getAvailableWizards();

        if(!availableWizards.contains(0))
            wizard_1.setVisible(false);
        if(!availableWizards.contains(1))
            wizard_2.setVisible(false);
        if(!availableWizards.contains(2))
            wizard_3.setVisible(false);
        if(!availableWizards.contains(3))
            wizard_4.setVisible(false);

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();
    }

    public void setWizard(int wizardID){
        selectedWizard = wizardID;
        System.out.println("Selezionato mago"+selectedWizard);
    }

    public void card1OnClick(){
        setWizard(0);
    }

    public void card2OnClick(){
        setWizard(1);
    }

    public void card3OnClick(){
        setWizard(2);
    }

    public void card4OnClick(){
        setWizard(3);
    }

    public void send(){
        if(!alreadyPressed && selectedWizard!=-1){
            //per ora non serve currentState perchè questo controller non si occupa di altro
            System.out.println("Mando "+selectedWizard);
            Message builtMessage = client.buildMessageFromPlayerInput(actionParser.parseWizardChoice(selectedWizard), ClientState.SET_UP_WIZARD_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }

}
