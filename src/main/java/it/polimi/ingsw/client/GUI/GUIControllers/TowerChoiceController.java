package it.polimi.ingsw.client.GUI.GUIControllers;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

/**
 * Class TowerChoiceController implements all the logic behind the Tower Choice Menu FXML Scene
 * It sets up buttons and images styling, handles mouse events,
 * and passes built message with tower enum back to the GUI instance
 * This class implements UpdatableController interface which includes a few methods to implement
 * the typical tabletop turn mechanics and the updatable interface
 */
public class TowerChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView white,black,grey;
    @FXML
    public Button confirmButton;


    boolean alreadyPressed = false;
    Tower selectedTower;
    boolean waitTurn = false;
    boolean isGameFinished = false;

    /**
     * Method initialize sets up effects for button hover events
     */
    @FXML
    public void initialize(){
        confirmButton.setEffect(null);
        confirmButton.setText("CONTINUE");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 10);
        confirmButton.setFont(font);
        confirmButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> confirmButton.setEffect(new Bloom()));
        confirmButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> confirmButton.setEffect(null));
    }

    /**
     * Method setWaitTurn sets waitTurn flag
     * If the flag is set to True, the controller will render the gray overlay to prevent player input
     * @param value is used to set waitTurn
     */
    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    /**
     * Method endGame sets gameFinished flag to True and then renders end game overlay
     */
    @Override
    public void endGame() {
        isGameFinished = true;
        renderEndGame();
    }

    /**
     * Method update gets from GUI instance the available towers
     * and sets to visible only the images corresponding to those towers
     * to prevent the player to select the ones that have been
     * already chosen by other players
     * It also checks if end game or wait turn overlays should be rendered by using waitTurn and isGameFinished flags
     */
    public void update(){
        ArrayList<Tower> availableTowers = gui.getAvailableTowers();

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

        if(isGameFinished)
            renderEndGame();
    }



    /**
     * Method renderEndGame renders a gray overlay screen to prevent player input and
     * renders a message in the center of the screen to let the player know that the game is over
     */
    private void renderEndGame(){
        String gameOverMessage = "GAME OVER\n";
        gameOverMessage+="SOMEONE HAS DISCONNECTED";
        Text gameOverText = new Text(gameOverMessage);
        gameOverText.setFill(Color.WHITE);
        gameOverText.setFont(gui.getGameFont());
        gameOverText.setStyle("-fx-font-size: 20;");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        gameOverText.setX(gui.getScreenY()/2);
        gameOverText.setY(gui.getScreenY()/2);

        gui.addElementToScene(gameOverText);
        gameOverText.toFront();
    }

    /**
     * Method setTower changes the selectedTower value with a new one
     * It gets called whenever a player clicks on a tower image.
     * @param tower represents the selected tower enum
     */
    public void setTower(Tower tower){
        selectedTower = tower;
    }

    /**
     * Method blackOnClick sets up the selection effects for black tower image
     * and sets the currently selected tower to BLACK
     */
    public void blackOnClick(){
        white.setEffect(new BoxBlur());
        grey.setEffect(new BoxBlur());
        black.setEffect(new DropShadow());
        setTower(Tower.BLACK);
    }

    /**
     * Method whiteOnClick sets up the selection effects for white tower image
     * and sets the currently selected tower to WHITE
     */
    public void whiteOnClick(){
        black.setEffect(new BoxBlur());
        grey.setEffect(new BoxBlur());
        white.setEffect(new DropShadow());
        setTower(Tower.WHITE);
    }

    /**
     * Method greyOnClick sets up the selection effects for grey tower image
     * and sets the currently selected tower to GREY
     */
    public void greyOnClick(){
        white.setEffect(new BoxBlur());
        black.setEffect(new BoxBlur());
        grey.setEffect(new DropShadow());
        setTower(Tower.GREY);
    }

    /**
     * Method send passes to ClientMessageBuilder instance the currently selected tower enum and sends back to the GUI instance
     * the built message.
     * This logic is executed if and only if the player has already selected a tower
     */
    public void send(){
        if(!alreadyPressed && selectedTower!=null){
            gui.setTeam(selectedTower);
            Message builtMessage = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.parseTowerChoice(selectedTower), ClientState.SET_UP_TOWER_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }

    /**
     * Method start comes from the implemented interface UpdatableController, but in this specific case it's not used
     */
    public void start() {

    }
}
