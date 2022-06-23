package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.TIE;

public class WizardChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView wizard_1,wizard_2,wizard_3,wizard_4;
    @FXML
    public Button confirmButton;
    boolean alreadyPressed = false;
    int selectedWizard = -1;
    boolean waitTurn = false;
    boolean isGameFinished = false;

    public void start(){
    }

    @FXML
    public void initialize(){
        confirmButton.setEffect(null);
        confirmButton.setText("CONTINUE");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 10);
        confirmButton.setFont(font);
        confirmButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {

            confirmButton.setEffect(new Bloom());

        });
        confirmButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            confirmButton.setEffect(null);
        });
    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    @Override
    public void endGame() {
        isGameFinished = true;
        renderEndGame();
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

        if(isGameFinished)
            renderEndGame();
    }

    private void renderEndGame(){
        System.out.println("RENDERIZZO SCRITA DI FINE GAME");
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

    public void setWizard(int wizardID){
        selectedWizard = wizardID;
        System.out.println("Selezionato mago"+selectedWizard);
    }

    public void card1OnClick(){
        wizard_1.setEffect(new DropShadow());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new BoxBlur());
        setWizard(0);
    }

    public void card2OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new DropShadow());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new BoxBlur());
        setWizard(1);
    }

    public void card3OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new DropShadow());
        wizard_4.setEffect(new BoxBlur());
        setWizard(2);
    }

    public void card4OnClick(){
        wizard_1.setEffect(new BoxBlur());
        wizard_2.setEffect(new BoxBlur());
        wizard_3.setEffect(new BoxBlur());
        wizard_4.setEffect(new DropShadow());
        setWizard(3);
    }

    public void send(){
        if(!alreadyPressed && selectedWizard!=-1){
            gui.setWizard(selectedWizard);
            //per ora non serve currentState perchè questo controller non si occupa di altro
            System.out.println("Mando "+selectedWizard);
            Message builtMessage = client.buildMessageFromPlayerInput(actionParser.parseWizardChoice(selectedWizard), ClientState.SET_UP_WIZARD_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }

}
