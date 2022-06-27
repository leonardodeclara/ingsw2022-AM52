package it.polimi.ingsw.GUI.GUIControllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class ConnectMenuController implements all the logic behind the Connect Menu FXML Scene
 * It parses ip and port strings from TextField, sets up buttons style,handles mouse events,
 * and passes built message back to the GUI instance
 */
public class ConnectMenuController extends GUIController{
    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private Button connectButton;
    @FXML
    private Text connectionFailedText;

    /**
     * Method initialize sets the style for the buttons and sets up the mouse hover event handlers
     */
    @FXML
    public void initialize(){
        connectButton.setEffect(null);
        connectButton.setText("CONNECT");
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Hey Comic.ttf"), 10);
        connectButton.setFont(font);
        connectButton.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {

            connectButton.setEffect(new Bloom());

        });
        connectButton.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            connectButton.setEffect(null);
        });

        connectionFailedText.setVisible(false);
    }

    /**
     * Method connect parses IP and port in the corresponding TextField instances
     * and tries to connect to the server
     * If a TextField is empty, a error message gets rendered on the screen
     */
    @FXML
    private void connect() throws IOException {
        if(ip.getText().length() > 0 && port.getText().length() > 0)
            gui.connect(ip.getText(),port.getText());
        else
            handleErrorMessage(false);
    }

    /**
     * Method handleErrorMessage renders an error message.
     * This only gets called if the player tries to connect without entering valid IP and port
     * @param fromServer is a flag used by the method to know which type of message to render
     */
    @Override
    public void handleErrorMessage(boolean fromServer) {
        ip.clear();
        port.clear();
        connectionFailedText.setText(fromServer ? "Connection failed! Please try again" : "You should enter a server IP address! ");
        connectionFailedText.setVisible(true);

        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        connectionFailedText.setVisible(false);
                    }
                });
            }
        };
        timer.schedule(task, 2000L);
    }

}
