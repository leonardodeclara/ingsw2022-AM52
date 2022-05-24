package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class NicknameController extends GUIController{
    @FXML
    private TextField nickname;

    @FXML
    public void authenticate(){
        String stringNickname = nickname.getText();
        if(stringNickname.length() > 0 && !stringNickname.equals("") && !stringNickname.equals(" ")){
            ArrayList<Object> data = new ArrayList<>();
            data.add(stringNickname);
            Message message = client.buildMessageFromPlayerInput(data, ClientState.CONNECT_STATE);
            gui.passToSocket(message);
        }
    }
}
