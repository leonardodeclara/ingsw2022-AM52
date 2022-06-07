package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class TowerChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView white,black,grey;
    boolean alreadyPressed = false;
    Tower selectedTower;
    boolean waitTurn = false;

    public void start(){
    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }


    public void update(){
        ArrayList<Tower> availableTowers = gui.getAvailableTowers();
        for(Tower t : availableTowers)
            System.out.println(t);

        if(!availableTowers.contains(Tower.BLACK))
            black.setVisible(false);
        if(!availableTowers.contains(Tower.GREY))
            grey.setVisible(false);
        if(!availableTowers.contains(Tower.WHITE))
            white.setVisible(false);

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();
    }

    public void setTower(Tower tower){
        selectedTower = tower;
    }

    public void blackOnClick(){
        white.setEffect(new BoxBlur());
        grey.setEffect(new BoxBlur());
        black.setEffect(new DropShadow());
        setTower(Tower.BLACK);
    }

    public void whiteOnClick(){
        black.setEffect(new BoxBlur());
        grey.setEffect(new BoxBlur());
        white.setEffect(new DropShadow());
        setTower(Tower.WHITE);
    }

    public void greyOnClick(){
        white.setEffect(new BoxBlur());
        black.setEffect(new BoxBlur());
        grey.setEffect(new DropShadow());
        setTower(Tower.GREY);
    }

    public void send(){
        if(!alreadyPressed && selectedTower!=null){
            gui.setTeam(selectedTower);
            //per ora non serve currentState perchè questo controller non si occupa di altro
            Message builtMessage = client.buildMessageFromPlayerInput(actionParser.parseTowerChoice(selectedTower), ClientState.SET_UP_TOWER_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }
}
