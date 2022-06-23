package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class NicknameController extends GUIController{

    public Button confirmButton;
    @FXML
    private TextField nickname;
    public Text nicknameMessage;
    private boolean alreadyPressed = false;

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
    @FXML
    public void authenticate(){
        if(!alreadyPressed){
            String stringNickname = nickname.getText();
            if(stringNickname.length() > 0 && !stringNickname.equals(" ")){
                gui.setPlayerNickname(stringNickname);
                Message message = client.buildMessageFromPlayerInput(actionParser.parseNickname(stringNickname), ClientState.CONNECT_STATE);
                gui.passToSocket(message);
                alreadyPressed = true;
            }
        }

    }

    @Override
    public void handleErrorMessage(boolean fromServer) {
        alreadyPressed=false;
        nickname.clear();
        nicknameMessage.setText("Riprova");
    }
}
