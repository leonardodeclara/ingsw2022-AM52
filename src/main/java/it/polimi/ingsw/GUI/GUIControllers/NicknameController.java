package it.polimi.ingsw.GUI.GUIControllers;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Class NicknameController implements all the logic behind the Nickname Menu FXML Scene
 * It parses nickname string from TextField, sets up buttons style,handles mouse events,
 * and passes built message back to the GUI instance
 */
public class NicknameController extends GUIController{

    public Button confirmButton;
    @FXML
    private TextField nickname;
    public Text nicknameMessage;
    private boolean alreadyPressed = false;

    /**
     * Method initialize sets the style for the buttons and sets up the mouse hover event handlers
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
     * Method authenticate gets the nickname in the TextField instance and sends it to ClientMessageBuilder instance to build a message,
     * which is then passed to the GUI instance with passToSocket
     */
    @FXML
    public void authenticate(){
        if(!alreadyPressed){
            String stringNickname = nickname.getText();
            if(stringNickname.length() > 0 && !stringNickname.equals(" ")){
                gui.setPlayerNickname(stringNickname);
                Message message = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.parseNickname(stringNickname), ClientState.CONNECT_STATE);
                gui.passToSocket(message);
                alreadyPressed = true;
            }
        }

    }

    /**
     * Method handleErrorMessage replaces the text asking for a nickname, with an error message.
     * This only gets called if the player sends a not available nickname
     * @param fromServer is a flag eventually used by the controller to decide which error message should be rendered
     */
    @Override
    public void handleErrorMessage(boolean fromServer) {
        alreadyPressed=false;
        nickname.clear();
        nicknameMessage.setText("Entered nickname is not available. Please choose another one");
    }
}
