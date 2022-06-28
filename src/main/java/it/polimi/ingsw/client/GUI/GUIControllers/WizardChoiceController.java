package it.polimi.ingsw.client.GUI.GUIControllers;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

/**
 * Class WizardChoiceController implements all the logic behind the Wizard Choice Menu FXML Scene
 * It sets up buttons and images styling, handles mouse events,
 * and passes built message with wizard ID back to the GUI instance
 * This class implements UpdatableController interface which includes a few methods to implement
 * the typical tabletop turn mechanics and the updatable interface
 */
public class WizardChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView wizard_1,wizard_2,wizard_3,wizard_4;
    @FXML
    public Button confirmButton;
    boolean alreadyPressed = false;
    int selectedWizard = -1;
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
     * Method update gets from GUI instance the available wizards
     * and sets to visible only the images corresponding to those wizards
     * to prevent the player to select the ones that have been
     * already chosen by other players
     * It also checks if end game or wait turn overlays should be rendered by using waitTurn and isGameFinished flags
     */
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
     * Method setWizard changes the selectedWizard value with a new one
     * It gets called whenever a player clicks on a wizard image.
     * @param wizardID represents the selected tower enum
     */
    public void setWizard(int wizardID){
        selectedWizard = wizardID;
    }

    /**
     * Method card1OnClick sets up the selection effects for wizard image 1 and sets the currently selected
     * wizard to 0
     */
    public void card1OnClick(){
        wizard_1.setEffect(new DropShadow());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new BoxBlur());
        setWizard(0);
    }

    /**
     * Method card2OnClick sets up the selection effects for wizard image 1 and sets the currently selected
     * wizard to 1
     */
    public void card2OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new DropShadow());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new BoxBlur());
        setWizard(1);
    }

    /**
     * Method card3OnClick sets up the selection effects for wizard image 1 and sets the currently selected
     * wizard to 2
     */
    public void card3OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new DropShadow());
        wizard_4.setEffect(new BoxBlur());
        setWizard(2);
    }

    /**
     * Method card4OnClick sets up the selection effects for wizard image 1 and sets the currently selected
     * wizard to 3
     */
    public void card4OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new DropShadow());
        setWizard(3);
    }

    /**
     * Method send passes to ClientMessageBuilder instance the currently selected wizard ID and sends back to the GUI instance
     * the built message.
     * This logic is executed if and only if the player has already selected something
     */
    public void send(){
        if(!alreadyPressed && selectedWizard!=-1){
            gui.setWizard(selectedWizard);
            Message builtMessage = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.parseWizardChoice(selectedWizard), ClientState.SET_UP_WIZARD_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }




    /**
     * Method start comes from the implemented interface UpdatableController, but in this specific case it's not used
     */
    public void start(){
    }
}
