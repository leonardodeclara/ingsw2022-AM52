package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * Class MatchMakingController implements all the logic behind the MatchMaking Menu FXML Scene
 * It parses parameters from ChoiceBox instances, sets up buttons' styling, handles mouse events,
 * and passes built message back to the GUI instance
 */
public class MatchMakingController extends GUIController{
    @FXML
    private ChoiceBox<String> numOfPlayers;
    @FXML
    private ChoiceBox<String> gameType;
    @FXML
    public Button confirmButton;

    private boolean alreadyPressed = false;

    /**
     * Method initialize sets up effects for button hover events
     * and initializes choice boxes' fields
     */
    @FXML
    public void initialize(){
        ObservableList<String> playerAvailableChoices = FXCollections.observableArrayList();
        ObservableList<String> gameAvailableChoices = FXCollections.observableArrayList();
        playerAvailableChoices.addAll("2", "3");
        gameAvailableChoices.addAll("Basic Rules","Expert Rules");
        numOfPlayers.setItems(playerAvailableChoices);
        gameType.setItems(gameAvailableChoices);


        confirmButton.setEffect(null);
        confirmButton.setText("SEARCH GAME");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 10);
        confirmButton.setFont(font);
        confirmButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> confirmButton.setEffect(new Bloom()));
        confirmButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> confirmButton.setEffect(null));
    }

    /**
     * Method searchGame parses numOfPlayers and gameType parameters from ChoiceBoxes
     * and sends them to ClientMessageBuilder to build a Message instance
     * which is then passed to the GUI instance with passToSocket method
     */
    public void searchGame(){
        if(!alreadyPressed){
            int numOfPlayersValue = Integer.parseInt(numOfPlayers.getSelectionModel().getSelectedItem());
            actionParser.setPlayersNumber(numOfPlayersValue);
            String gameTypeString = gameType.getSelectionModel().getSelectedItem();
            boolean isExpert = (!gameTypeString.equals("Basic Rules"));
            //per ora non serve currentState perchè questo controller non si occupa di altro se non questo
            ArrayList<Object> selectedMode = actionParser.parseNewGameParameters(numOfPlayersValue,isExpert);

            Message builtMessage = clientMessageBuilder.buildMessageFromPlayerInput(selectedMode, ClientState.INSERT_NEW_GAME_PARAMETERS);
            gui.passToSocket(builtMessage);
            gui.prepareView(selectedMode);
            alreadyPressed = true;
        }
    }



}
