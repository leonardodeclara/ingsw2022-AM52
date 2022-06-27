package it.polimi.ingsw.GUI.GUIControllers;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.GUI.*;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Constants.*;


/**
 * Class GameTableController implements all the logic behind the Game Table FXML Scene
 * It renders almost all the game elements, handles mouse event handling,
 * sets up various selection effects, deals with player input and sends messages back to the GUI
 */
public class GameTableController extends GUIController implements UpdatableController{
    @FXML private ImageView player1Icon;
    @FXML private ImageView player2Icon;
    @FXML private ImageView player3Icon;
    @FXML private ImageView tableBounds;
    @FXML private ImageView deckButton;
    @FXML private Button sendButton;
    @FXML private Label contextMessage;
    private int renderedDashboard;
    private ArrayList<ImageView> deckImages;
    private HashMap<ImageView,Text> currentTurnCardsImages;
    private ChoiceBox<it.polimi.ingsw.model.Color> colorChoiceBox;
    private ArrayList<GUIIsland> islands;
    private ArrayList<GUICloud> clouds;
    private ArrayList<GUIPersonality> personalities;
    private HashMap<ClientBoard, GUIBoard> GUIBoards;
    private GUIBoard currentBoard;
    private HashMap<Integer,String> localIDToPlayer;
    private double centerX = 0;
    private double centerY = 0;
    private boolean waitTurn = false;
    private boolean initialized = false;
    private boolean showDeck;
    private boolean isGameFinished;
    private ArrayList<Node> selectedImages;

    /**
     * Method start initializes all the data structures containing the various game elements to render.
     */
    public void start(){
        if(!initialized){
            centerX = gui.getScreenX()/2;
            centerY = gui.getScreenY()/2 - 15;
            deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
            deckImages = new ArrayList<>();
            currentTurnCardsImages = new HashMap<>();
            GUIBoards = new HashMap<>();
            selectedImages = new ArrayList<>();
            islands = new ArrayList<>();
            clouds = new ArrayList<>();
            personalities = new ArrayList<>();
            initialized = true;
            showDeck=true;
            isGameFinished = false;
            sendButton.setOnMouseEntered(e -> sendButton.setEffect(new Bloom()));
            sendButton.setOnMouseExited(e -> sendButton.setEffect(null));

            if(gui.getNumOfPlayers()==2)
                player3Icon.setVisible(false);

            localIDToPlayer = new HashMap<>();
            localIDToPlayer.put(1,gui.getPlayerNickname());
            int i = 2;
            for(String otherPlayer : gui.getOtherPlayers()){
                localIDToPlayer.put(i,otherPlayer);
                i++;
            }
            renderPlayerButtonName(player1Icon, localIDToPlayer.get(1));
            renderPlayerButtonName(player2Icon,localIDToPlayer.get(2));
            renderPlayerButtonName(player3Icon, localIDToPlayer.get(3));

            for(ClientBoard clientBoard : gui.getClientBoards()){
                GUIBoards.put(clientBoard,new GUIBoard(clientBoard,gui,this,tableBounds));
            }
            renderedDashboard = 1;
            ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard));
            currentBoard = GUIBoards.get(clientBoard);
        }

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
     * Method handleErrorMessage replaces the current state prompt with an error message for 2 seconds.
     * This only gets called if the player tries to do an invalid action
     * @param fromServer is a flag eventually used by the controller to decide which error message should be rendered
     */
    @Override
    public void handleErrorMessage(boolean fromServer){
        List<String> texts= fromServer ? gui.getCurrentState().getServerErrorMessage() : gui.getCurrentState().getInputErrorMessage();

        contextMessage.setText(texts.get(0));
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        visualizeContextMessage();
                        renderIslands();
                        populateDashboard(false);
                    }
                });
            }
        };
        timer.schedule(task, 2000L);
    }

    /**
     * Method send check if something is currently selected and eventually sends to ClientMessageBuilder those parameters
     * to build a message to send to the server accordingly to the current client state.
     * This method gets called when the player clicks on CONFIRM button in the bottom center of the screen
     * If nothing has been selected, an error prompt gets rendered
     * If the player tries to perform an invalid action, an error prompt gets rendered
     * It also handles various selection effects for both the selected items and the CONFIRM button
     */
    public void send(){

        sendButton.setEffect(new DropShadow());

        if(gui.getCurrentState().equals(ClientState.END_TURN) && actionParser.getParameters().size() == 0)
            actionParser.getParameters().add(0,"end");


        if(actionParser.getParameters().size() > 0){

            Message message = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.getParameters(), gui.getCurrentState());
            if(message != null){
                gui.passToSocket(message);

                actionParser.clearSelectedParameters();

                for(Node n : selectedImages)
                    n.setEffect(null);
            }else{
                handleErrorMessage(false);
            }
        }else{
            handleErrorMessage(false);
        }

    }

    /**
     * Method update gets from GUI instance the current status of the GameBoard instance
     * and renders all the game elements accordingly.
     * The rendered board depends on the current value of renderedDashboard variable
     * If the current client state is WAIT_TURN or if the game is finished, a gray overlay with a message gets rendered
     */
    @Override
    public void update() {
        renderIslands();
        renderClouds();
        renderDeck();
        renderCurrentTurnCards();
        if (gui.getGB().isExpertGame())
            renderPersonalityCards();
        clearColorChoiceBox();
        if (gui.getCurrentState().equals(ClientState.CHOOSE_COLOR_FOR_CARD_9)
                || gui.getCurrentState().equals(ClientState.CHOOSE_COLOR_FOR_CARD_12))
            renderColorChoiceBox();
        populateDashboard(false);
        visualizeContextMessage();
        renderSendButton();

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
    private void renderEndGame() {
        String gameOverMessage = "GAME OVER\n";
        if (gui.getWinner()==null)
            gameOverMessage+="SOMEONE HAS DISCONNECTED";
        else if (gui.getWinner()!=null && gui.getWinner().equals(TIE))
            gameOverMessage+="THE GAME ENDED ON A TIE";
        else
            gameOverMessage+=gui.getWinner().toUpperCase()+" IS THE WINNER!";
        Text gameOverText = new Text(gameOverMessage);
        gameOverText.setFill(Color.WHITE);
        gameOverText.setFont(gui.getGameFont());
        gameOverText.setStyle("-fx-font-size: 20;");
        gameOverText.setTextAlignment(TextAlignment.CENTER);

        gameOverText.setX(gui.getScreenX()/2- gameOverText.getLayoutBounds().getWidth()/1.5);
        gameOverText.setY(gui.getScreenY()/2);

        gui.addElementToScene(gameOverText);
        gameOverText.toFront();
    }

    /**
     * Method renderSendButton renders the button used by the player to perform game actions
     */
    private void renderSendButton(){
        sendButton.setEffect(null);
        sendButton.toFront();
        sendButton.setText(gui.getCurrentState().equals(ClientState.END_TURN) ? "END TURN" : "CONFIRM");
    }

    /**
     * Method renderIslands renders all the islands elements, in a circle pattern, to the screen.
     * Each island corresponds to a GUIIsland instance
     * Also,in the rendering loop, all the GUIIslands' events are set and
     * the image reference gets saved
     */
    private void renderIslands(){
        for(GUIIsland guiIsland : islands){
            guiIsland.clearIsland();
        }

        islands.clear();

        ArrayList<ClientIsland> clientIslands = gui.getIslands();
        int numIslands = clientIslands.size();

        int islandCounter = 0;
        for (ClientIsland island : clientIslands) {
            double angle = 2 * islandCounter * Math.PI / numIslands ;
            double xOffset = ISLAND_CIRCLE_RADIUS * Math.cos(angle-Math.PI/2);
            double yOffset = ISLAND_CIRCLE_RADIUS * Math.sin(angle-Math.PI/2);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            GUIIsland guiIsland = new GUIIsland(island.getIslandIndex(),x-ISLAND_IMAGE_WIDTH/2,y-ISLAND_IMAGE_HEIGHT/2,ISLAND_IMAGE_WIDTH,ISLAND_IMAGE_HEIGHT,angle,this,gui);
            guiIsland.setClientIsland(island);
            guiIsland.setEvents();
            guiIsland.render();
            islands.add(guiIsland);
            islandCounter++;
        }
    }

    /**
     * Method renderClouds renders all the clouds elements, in a circle pattern, to the screen.
     * Each cloud corresponds to a GUICloud instance
     * Also,in the rendering loop, all the GUICloud events are set and
     * the image reference gets saved
     */
    private void renderClouds(){
        for (GUICloud cloud: clouds)
            cloud.clearCloud();

        clouds.clear();
        ArrayList<ClientCloud> clientClouds = gui.getClouds();
        int numClouds = clientClouds.size();
        int personalitiesYOffset = ((gui.getGB().isExpertGame() && gui.getNumOfPlayers()==3) ? CLOUD_PERSONALITY_OFFSET : 0);
        for (ClientCloud cloud : clientClouds) {
            int i = cloud.getCloudIndex();
            double angle = 2 * i * Math.PI / numClouds ;
            double xOffset = CLOUD_CIRCLE_RADIUS * Math.cos(angle);
            double yOffset = CLOUD_CIRCLE_RADIUS * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset - personalitiesYOffset;
            GUICloud guiCloud = new GUICloud(i, x-CLOUD_IMAGE_WIDTH/2,y-CLOUD_IMAGE_HEIGHT/2,
                    CLOUD_IMAGE_WIDTH,CLOUD_IMAGE_HEIGHT,this,gui,cloud);
            guiCloud.setEvents();
            guiCloud.render();
            clouds.add(guiCloud);
        }
    }

    /**
     * Method populateDashboard renders all the board elements to the screen.
     * This includes lobby students, table students, teachers' tables and towers.
     * The board to render is chosen accordingly to the value of renderedDashboard
     */
    private void populateDashboard(boolean fromClick){
        if(!fromClick){
            for(GUIBoard guiBoard : GUIBoards.values())
                guiBoard.clearBoard(true);
        }
        else{
            if(currentBoard != null){
                currentBoard.clearBoard(false);
            }
            ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard));
            currentBoard = GUIBoards.get(clientBoard);
        }

        currentBoard.populate();
    }

    /**
     * Method renderPersonalityCards renders all the personality cards to the screen.
     */
    private void renderPersonalityCards(){
        for (GUIPersonality personality : personalities)
            personality.clearPersonality();
        personalities.clear();

        ArrayList<ClientPersonality> cards = gui.getPersonalityCards();
        double cardY,cardX;
        int i = 0;
        for (ClientPersonality card: cards){
            int cardId = card.getCardID();
            cardY = centerY - PERSONALITY_OFFSET_Y + ISLAND_IMAGE_HEIGHT*1.1;
            cardX= centerX - PERSONALITY_IMAGE_WIDTH + i*PERSONALITY_IMAGE_WIDTH;
            GUIPersonality guiPersonality = new GUIPersonality(cardId,cardX-PERSONALITY_IMAGE_WIDTH/2,
                    cardY-PERSONALITY_IMAGE_HEIGHT/2,PERSONALITY_IMAGE_WIDTH,PERSONALITY_IMAGE_HEIGHT,
                    this,gui, card);
            guiPersonality.render();
            guiPersonality.setEvents();
            personalities.add(guiPersonality);
            i++;
        }
    }


    /**
     * Method renderDeck renders all the assistant cards to the screen.
     */
    private void renderDeck(){
        HashMap<Integer, Integer> deck = gui.getDeck();
        if((deckImages.size() == 0 || deck.size() != deckImages.size())){
            clearDeck();
            int deckCounter = 0;
            int startY = ASSISTANT_Y_START + (10 - deck.size()) * ASSISTANT_Y_OFFSET;
            for (Integer priority : deck.keySet()) {
                ImageView assistantImage = new ImageView("/graphics/assistant_" + priority + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY + deckCounter * ASSISTANT_Y_OFFSET);
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                assistantImage.setOnMouseClicked((MouseEvent e) -> {
                    actionParser.handleSelectionEvent(priority, Clickable.ASSISTANT,gui.getCurrentState());
                    handleSelectionEffect(assistantImage,Clickable.ASSISTANT);
                });
                assistantImage.setOnMouseEntered((MouseEvent e) -> {
                    handleHoverEvent(assistantImage, Clickable.ASSISTANT);
                });
                assistantImage.setOnMouseExited((MouseEvent e) -> {
                    if (assistantImage.getEffect()==null || !(DropShadow.class).equals(assistantImage.getEffect().getClass()))
                        assistantImage.setEffect(null);
                });
                deckImages.add(assistantImage);
                gui.addElementToScene(assistantImage);
                assistantImage.setVisible(showDeck);
                deckCounter++;
            }
            deckButton.toFront();
        }
    }

    /**
     * Method clearDeck removes from the screen all the assistant cards' images
     * It's used to clear the screen before rendering after update
     */
    private void clearDeck(){
        for(ImageView card : deckImages){
            gui.removeElementFromScene(card);
        }
        deckImages.clear();
    }

    /**
     * Method renderPlayerButtonName renders the text to the bottom of the board buttons
     */
    private void renderPlayerButtonName(ImageView playerIcon, String playerName){
        if (playerName==null) return;
        Text name;
        if (playerName.equals(gui.getGB().getNickname()))
            name = new Text("YOUR BOARD");
        else
            name = new Text(playerName.toUpperCase()+"'S"+"\nBOARD");
        name.setLayoutX(playerIcon.getLayoutX());
        name.setLayoutY(playerIcon.getLayoutY()+NAME_TO_BUTTON_VGAP);
        name.setFont(gui.getGameFont());
        name.setFill(Color.WHITE);
        name.setStrokeWidth(.5);
        name.setStroke(Color.BLACK);
        gui.addElementToScene(name);
        Tooltip message = new Tooltip("CLICK ON THE CIRCLE TO SHOW "+playerName.toUpperCase()+"'S BOARD");
        message.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(playerIcon, message);
    }

    /**
     * Method renderColorChoiceBox renders a ChoiceBox used by some cards, to
     * let player select a specific color to activate the card effect
     */
    private void renderColorChoiceBox() {
        if (colorChoiceBox!=null)
            clearColorChoiceBox();
        ObservableList<it.polimi.ingsw.model.Color> colorOptions = FXCollections.observableArrayList();
        colorOptions.addAll(it.polimi.ingsw.model.Color.values());
        colorChoiceBox =  new ChoiceBox<>();
        colorChoiceBox.setItems(colorOptions);
        colorChoiceBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (ov,old_value,new_value)-> {
                actionParser.handleSelectionEvent(new_value,Clickable.COLOR,gui.getCurrentState());
        });
        colorChoiceBox.setPrefWidth(COLOR_CHOICEBOX_WIDTH);
        colorChoiceBox.setLayoutX(centerX-COLOR_CHOICEBOX_WIDTH/2);
        colorChoiceBox.setLayoutY(centerY-COLOR_CHOICEBOX_VGAP);
        Tooltip message = new Tooltip("CHOOSE A COLOR");
        message.setShowDelay(Duration.seconds(0.1));
        Tooltip.install(colorChoiceBox, message);
        gui.addElementToScene(colorChoiceBox);
    }

    /**
     * Method visualizeContextMessage renders, accordingly to the current client state
     * the context message so that the player knows what to do
     */
    private void visualizeContextMessage(){
        StringBuilder messageForToolTip= new StringBuilder();
        List<String> texts= gui.getCurrentState().getGUIContextMessage(gui.getGB());
        for (String text: texts){
            messageForToolTip.append(text).append("\n");
        }

        contextMessage.setText(texts.get(0));
        Tooltip fullMessage = new Tooltip(messageForToolTip.toString());
        fullMessage.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(contextMessage, fullMessage);
    }

    /**
     * Method handleSelectionEffect renders the selection effect for the selected items
     * This is done only if the selected item's type is in the clickable list of the current
     * client state
     */
    public void handleSelectionEffect(Node n, Clickable type){
        if(actionParser.canClick(gui.getCurrentState(),type)){
            selectedImages.add(n);
            switch(type){
                case ASSISTANT -> {
                    for(ImageView card : deckImages){
                        card.setEffect(null);
                    }
                    n.setEffect(new DropShadow());
                }
                case ISLAND -> {
                    for(GUIIsland island : islands){
                        island.setImageEffect(null);
                    }
                    n.setEffect(new DropShadow());
                }
                case CLOUD -> {
                    for(GUICloud cloud : clouds){
                        cloud.setImageEffect(null);
                    }
                    n.setEffect(new DropShadow());
                }
                case PERSONALITY -> {
                    for(GUIPersonality card : personalities){
                        card.setImageEffect(null);
                    }
                    n.setEffect(new DropShadow());
                }
                case PERSONALITY_CARD_STUDENT,LOBBY_STUDENT,TABLE_STUDENT -> {
                    n.setEffect(new DropShadow());
                }
            }
        }
    }

    /**
     * Method handleHoverEvent renders the hover effect for the item pointed
     * by the mouse cursor
     * This is done only if the hovered item's type is in the clickable list of the current
     * client state
     */
    public void handleHoverEvent(Node n, Clickable hoveredElement){
        if(actionParser.canClick(gui.getCurrentState(),hoveredElement)){
            if (n.getEffect()==null){
                Bloom bloom = new Bloom();
                bloom.setThreshold(0.1);
                n.setEffect(bloom);}
        }
    }

    /**
     * Method onPlayer1Click renders the board of player 1, which is always the player
     * who is playing the game from this instance of the game.
     * If the rendered board of the player has already been rendered, nothing changes
     * If the player is moving students with drag n drop, nothing changes
     */
    public void onPlayer1Click(){
        if(renderedDashboard!=1 && !currentBoard.isLobbyModified()){
            renderedDashboard = 1;
            populateDashboard(true);
        }
    }

    /**
     * Method onPlayer2Click renders the board of player 2
     * If the rendered board of the player has already been rendered, nothing changes
     * If the player is moving students with drag n drop, nothing changes
     */
    public void onPlayer2Click(){
        if(renderedDashboard!=2 && !currentBoard.isLobbyModified()){
            renderedDashboard = 2;
            populateDashboard(true);
        }
    }

    /**
     * Method onPlayer3Click renders the board of player 3
     * If the rendered board of the player has already been rendered, nothing changes
     * If the player is moving students with drag n drop, nothing changes
     */
    public void onPlayer3Click(){
        if(renderedDashboard!=3 && !currentBoard.isLobbyModified()){
            renderedDashboard = 3;
            populateDashboard(true);
        }
    }

    /**
     * Method extractMNsteps returns the distance between the island where mother nature is
     * and the one specified by the island ID parameter
     * @param islandId represents the ID of the island to consider in the calculation
     * @return the result of the calculation
     */
    public int extractMNsteps(int islandId){
        return gui.getGB().getMotherNatureDistance(islandId);
    }


    /**
     * Method changeShowedCards show/hides the assistant cards played in the current turn
     * or the player's deck cards accordingly to showDeck flag
     */
    public void changeShowedCards() {
        if(showDeck) {
            for (ImageView deckCardImage : deckImages)
                deckCardImage.setVisible(false);
            for (Map.Entry<ImageView,Text> turnCardImage: currentTurnCardsImages.entrySet()){
                turnCardImage.getKey().setVisible(true);
                turnCardImage.getValue().setVisible(true);
            }
            showDeck=false;
        }
        else{
            for (Map.Entry<ImageView,Text> turnCardImage: currentTurnCardsImages.entrySet()){
                turnCardImage.getKey().setVisible(false);
                turnCardImage.getValue().setVisible(false);
            }
            for (ImageView deckCardImage : deckImages)
                deckCardImage.setVisible(true);
            showDeck=true;
        }
    }

    /**
     * Method renderCurrentTurnCards renders the current turn cards
     */
    public void renderCurrentTurnCards(){
        HashMap<String,Integer> playerToCard = gui.getTurnCards();
        HashMap<String,Integer> cardsToRender=
                new HashMap<>(playerToCard.entrySet().
                stream().filter(entry->entry.getValue()!=0).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        if (currentTurnCardsImages.size()==0 || currentTurnCardsImages.size()!=cardsToRender.size()){
            clearCurrentTurnCards();
            int cardCounter = 0;
            double startY = deckButton.getLayoutY()-CURRENT_ASSISTANT_VGAP;

            for (Map.Entry<String,Integer> cardChoice: cardsToRender.entrySet()) {
                ImageView assistantImage = new ImageView("/graphics/assistant_" + cardChoice.getValue() + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY - cardCounter * CURRENT_ASSISTANT_VGAP);
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                String text = cardChoice.getKey().equals(gui.getPlayerNickname())?
                        "YOUR CARD": cardChoice.getKey().toUpperCase()+"'S CARD";
                Text cardLabel = new Text(text);
                cardLabel.setX(ASSISTANT_X);
                cardLabel.setY(assistantImage.getY()+ASSISTANT_IMAGE_HEIGHT*0.9);
                cardLabel.setFont(gui.getGameFont());
                cardLabel.setFill(Color.WHITE);
                currentTurnCardsImages.put(assistantImage,cardLabel);
                gui.addElementToScene(assistantImage);
                gui.addElementToScene(cardLabel);
                assistantImage.setVisible(!showDeck);
                cardLabel.setVisible(!showDeck);
                cardLabel.toFront();
                cardCounter++;
            }
        }
    }

    /**
     * Method clearCurrentTurnCards removes from the screen all the assistant cards' images played in the current turn
     * It's used to clear the screen before rendering after update
     */
    private void clearCurrentTurnCards(){
        for(Map.Entry<ImageView,Text> card : currentTurnCardsImages.entrySet()){
            gui.removeElementFromScene(card.getKey());
            gui.removeElementFromScene(card.getValue());
        }
        currentTurnCardsImages.clear();
    }

    private ClientCloud getClientCloudFromImage(ArrayList<ClientCloud> clouds, int cloudCounter){
        Optional<ClientCloud> optCloud = clouds.stream()
                .filter(clientCloud -> clientCloud.getCloudIndex() == cloudCounter)
                .findFirst();

        return optCloud.orElse(null);
    }

    private ClientIsland getClientIslandFromImage(ArrayList<ClientIsland> islands,int islandCounter){
        Optional<ClientIsland> optIsland = islands.stream()
                .filter(clientIsland -> clientIsland.getIslandIndex() == islandCounter)
                .findFirst();

        return optIsland.orElse(null);
    }

    private void clearColorChoiceBox(){
        gui.removeElementFromScene(colorChoiceBox);
    }

    public ActionParser getActionParser() {
        return actionParser;
    }
}
