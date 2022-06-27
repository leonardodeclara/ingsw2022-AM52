package it.polimi.ingsw.GUI;

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

public class TowerChoiceController extends GUIController implements UpdatableController{
    @FXML
    public ImageView white,black,grey;
    @FXML
    public Button confirmButton;


    boolean alreadyPressed = false;
    Tower selectedTower;
    boolean waitTurn = false;
    boolean isGameFinished = false;

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

        if(isGameFinished)
            renderEndGame();
    }

    @Override
    public void start() {

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
            Message builtMessage = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.parseTowerChoice(selectedTower), ClientState.SET_UP_TOWER_PHASE);
            gui.passToSocket(builtMessage);
            alreadyPressed = true;
        }
    }
}
