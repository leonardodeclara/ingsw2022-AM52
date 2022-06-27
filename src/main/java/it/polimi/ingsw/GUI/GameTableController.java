package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.*;
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

//TODO visualizzazione messaggio last round
//TODO: sistema di unclick per gli spostamenti di studenti


//TODO schermata endgame

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
    private HashMap<ClientBoard,GUIBoard> GUIBoards;
    private GUIBoard currentBoard;
    private HashMap<Integer,String> localIDToPlayer; //1 sempre giocatore,2 giocatore in alto,3 giocatore in mezzo (associa all'icona ingame, il nickname)
    private double centerX = 0;
    private double centerY = 0;
    private boolean waitTurn = false;
    private boolean initialized = false;
    private boolean showDeck;
    private boolean isGameFinished;
    private ArrayList<Node> selectedImages;

    public void start(){ //metodo di inizializzazione chiamato da GUI. In alcune situazioni viene chiamato due volte ma noi dobbiamo inizializzare una volta sola
        if(!initialized){ //sarebbe meglio spostare questo controllo sulla GUI e generalizzarlo
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
            System.out.println("Bottone 1 è "+localIDToPlayer.get(1));
            int i = 2;
            for(String otherPlayer : gui.getOtherPlayers()){
                localIDToPlayer.put(i,otherPlayer);
                i++;
            }
            System.out.println("Bottone 2 è "+localIDToPlayer.get(2));
            System.out.println("Bottone 3 è "+localIDToPlayer.get(3));
            renderPlayerButtonName(player1Icon, localIDToPlayer.get(1));
            renderPlayerButtonName(player2Icon,localIDToPlayer.get(2));
            renderPlayerButtonName(player3Icon, localIDToPlayer.get(3));

            for(ClientBoard clientBoard : gui.getClientBoards()){
                GUIBoards.put(clientBoard,new GUIBoard(clientBoard,gui,this,tableBounds)); //Crea tutte le GUIBoards
            }
            renderedDashboard = 1; //Di default renderizziamo la nostra board
            ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard)); //retrieviamo la clientboard da renderizzare
            currentBoard = GUIBoards.get(clientBoard); //e settiamo la currentBoard per la prima volta
        }

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
                        visualizeContextMessage();//può essere eseguito solo sul thread di GUI
                        renderIslands();
                        populateDashboard(false);
                    }
                });
            }
        };
        timer.schedule(task, 2000L);
    }

    public void send(){
        //qui si hanno tanti selezionabili, ma abbiamo la garanzia che solo quelli clickable per il currentstate saranno !=null
        //dobbiamo prendere e mettere quelli non null (Senza controllare quali sono) in un arraylist di object da passare a buildMessage
        sendButton.setEffect(new DropShadow());

        //da sistemare
        if(gui.getCurrentState().equals(ClientState.END_TURN) && actionParser.getParameters().size() == 0) //se siamo a fine turno e non vogliamo mandare le carte, scriviamo "end" in parameters
            actionParser.getParameters().add(0,"end");


        if(actionParser.getParameters().size() > 0){

            Message message = clientMessageBuilder.buildMessageFromPlayerInput(actionParser.getParameters(), gui.getCurrentState());
            if(message != null){
                System.out.println("Il messaggio è stato costruito ed è valido");
                gui.passToSocket(message);

                //pulisco l'array di parametri dopo aver inviato il messaggio
                actionParser.clearSelectedParameters();

                for(Node n : selectedImages) //resetta tutti gli effetti di selezione
                    n.setEffect(null);
            }else{
                handleErrorMessage(false);
                System.out.println("Il giocatore non ha selezionato qualcosa di valido per lo stato corrente");
            }
        }else{
            System.out.println("Il giocatore ha premuto send ma non ha selezionato niente");
            handleErrorMessage(false);
        }

    }

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

    private void renderSendButton(){
        sendButton.setEffect(null); //modo temporaneo per resettare l'effetto generato dal click
        sendButton.toFront();
        sendButton.setText(gui.getCurrentState().equals(ClientState.END_TURN) ? "END TURN" : "CONFIRM");
    }

    private void renderIslands(){
        for(GUIIsland guiIsland : islands){
            guiIsland.clearIsland(); //cancella tutte le isole e relativo contenuto
        }

        islands.clear(); //rimuovi le reference dall'array islands

        //procedi a renderizzare le isole da capo
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

    private void populateDashboard(boolean fromClick){ //from click ci dice se è arrivato un update o se il metodo è stato chiamato dalla chiusura di una board
        if(!fromClick){ //se è arrivato un update, resettiamo i drag n drop clientside
            for(GUIBoard guiBoard : GUIBoards.values()) //le cicliamo tutte, non sappiamo quali sono state modificate client side
                guiBoard.clearBoard(true);
        }
        else{ //se il giocatore vuole vedere un altra board, dobbiamo preservare i drag n drop clientside
            if(currentBoard != null){ //se c'è già qualcosa renderizzato lo puliamo senza resettarlo
                currentBoard.clearBoard(false);
            }
            ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard)); //retrieviamo la clientboard da renderizzare
            currentBoard = GUIBoards.get(clientBoard); //e cambiamo la currentBoard
        }

        currentBoard.populate(); //in ogni caso si ripopola la currentBoard
    }

    private void renderPersonalityCards(){
        for (GUIPersonality personality : personalities)
            personality.clearPersonality();
        personalities.clear();

        ArrayList<ClientPersonality> cards = gui.getPersonalityCards();
        double cardY,cardX;
        int i = 0;
        for (ClientPersonality card: cards){
            int cardId = card.getCardID();
            //System.out.println("GameTableController: renderizzo la carta personaggio "+cardId);
            cardY = centerY - PERSONALITY_OFFSET_Y + ISLAND_IMAGE_HEIGHT*1.1; //scelta a caso
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

    private void renderDeck(){
        HashMap<Integer, Integer> deck = gui.getDeck(); //k= priority, v = numMoves
        if((deckImages.size() == 0 || deck.size() != deckImages.size())){
            clearDeck();
            int deckCounter = 0;
            int startY = ASSISTANT_Y_START + (10 - deck.size()) * ASSISTANT_Y_OFFSET;
            //System.out.println("Ci sono " + deck.size() + " carte nel mazzo");
            for (Integer priority : deck.keySet()) {
                //System.out.println("Renderizzo la carta " + priority);
                ImageView assistantImage = new ImageView("/graphics/assistant_" + priority + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY + deckCounter * ASSISTANT_Y_OFFSET);
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                assistantImage.setOnMouseClicked((MouseEvent e) -> {
                    //handleClickEvent(priority, Clickable.ASSISTANT);
                    actionParser.handleSelectionEvent(priority,Clickable.ASSISTANT,gui.getCurrentState());
                    handleSelectionEffect(assistantImage,Clickable.ASSISTANT);
                });
                assistantImage.setOnMouseEntered((MouseEvent e) -> {
                    handleHoverEvent(assistantImage, Clickable.ASSISTANT);
                });
                assistantImage.setOnMouseExited((MouseEvent e) -> {
                    //tolgo l'effetto impostato quando muovo il mouse solo se la carta non è quella clickata
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

    private void clearDeck(){
        for(ImageView card : deckImages){
            gui.removeElementFromScene(card);
        }
        deckImages.clear();
    }

    private void renderPlayerButtonName(ImageView playerIcon, String playerName){
        if (playerName==null) return;
        Text name;
        if (playerName.equals(gui.getGB().getNickname()))
            name = new Text("YOUR BOARD");
        else
            name = new Text(playerName.toUpperCase()+"'S"+"\nBOARD");
        name.setLayoutX(playerIcon.getLayoutX());
        //System.out.println("Posizione x del buttone di " + playerName + "è " + playerIcon.getLayoutX());
        name.setLayoutY(playerIcon.getLayoutY()+NAME_TO_BUTTON_VGAP);
        //System.out.println("Posizione y del buttone di " + playerName + "è " + playerIcon.getLayoutY());
        name.setFont(gui.getGameFont());
        name.setFill(Color.WHITE);
        name.setStrokeWidth(.5);
        name.setStroke(Color.BLACK);
        gui.addElementToScene(name);
        Tooltip message = new Tooltip("CLICK ON THE CIRCLE TO SHOW "+playerName.toUpperCase()+"'S BOARD");
        message.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(playerIcon, message);
    }

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


    private void visualizeContextMessage(){
        StringBuilder messageForToolTip= new StringBuilder();
        List<String> texts= gui.getCurrentState().getGUIContextMessage(gui.getGB());
        for (String text: texts){
            messageForToolTip.append(text).append("\n");
        }

        //System.out.println("SCRIVO CONTEXT "+texts.get(0));
        contextMessage.setText(texts.get(0));
        Tooltip fullMessage = new Tooltip(messageForToolTip.toString());
        fullMessage.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(contextMessage, fullMessage);
    }

    public void handleSelectionEffect(Node n, Clickable type){ //gestisce gli effetti di selezione per ogni clickable (vedremo in futuro se avrà senso averlo così generalizzato)
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

    public void handleHoverEvent(Node n, Clickable hoveredElement){ //gestisce l'hover per ogni clickable (vedremo in futuro se avrà senso averlo generalizzato)
        if(actionParser.canClick(gui.getCurrentState(),hoveredElement)){
            //aggiungo il bloom solo se la carta non è stata clickata
            if (n.getEffect()==null){
                Bloom bloom = new Bloom();
                bloom.setThreshold(0.1);
                n.setEffect(bloom);}
        }
    }


    public void onPlayer1Click(){
        if(renderedDashboard!=1 && !currentBoard.isLobbyModified()){
            //clearBoard();
            renderedDashboard = 1;
            populateDashboard(true);
        }
    }


    public void onPlayer2Click(){
        if(renderedDashboard!=2 && !currentBoard.isLobbyModified()){
            //clearBoard();
            renderedDashboard = 2;
            populateDashboard(true);
        }
    }
    public void onPlayer3Click(){
        if(renderedDashboard!=3 && !currentBoard.isLobbyModified()){
            //clearBoard();
            renderedDashboard = 3;
            populateDashboard(true);
        }
    }

    public int extractMNsteps(int islandId){
        return gui.getGB().getMotherNatureDistance(islandId);
    }

    public void changeShowedCards(MouseEvent mouseEvent) {
        //System.out.println("Ho cliccato per cambiare le carte da mostrare");
        if(showDeck) {
            //System.out.println("ora mostro le currentAssistantCards");
            for (ImageView deckCardImage : deckImages)
                deckCardImage.setVisible(false);
            for (Map.Entry<ImageView,Text> turnCardImage: currentTurnCardsImages.entrySet()){
                turnCardImage.getKey().setVisible(true);
                turnCardImage.getValue().setVisible(true);
            }
            showDeck=false;
        }
        else{
            //System.out.println("ora mostro le deckImages");
            for (Map.Entry<ImageView,Text> turnCardImage: currentTurnCardsImages.entrySet()){
                turnCardImage.getKey().setVisible(false);
                turnCardImage.getValue().setVisible(false);
            }
            for (ImageView deckCardImage : deckImages)
                deckCardImage.setVisible(true);
            showDeck=true;
        }
    }

    public void renderCurrentTurnCards(){
        HashMap<String,Integer> playerToCard = gui.getTurnCards();
        HashMap<String,Integer> cardsToRender=
                new HashMap<>(playerToCard.entrySet().
                stream().filter(entry->entry.getValue()!=0).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        //se qualcuno non ha ancora giocato la sua carta assistente viene settato in GameBoard che il valore della sua carta è 0

        if (currentTurnCardsImages.size()==0 || currentTurnCardsImages.size()!=cardsToRender.size()){
            clearCurrentTurnCards();
            int cardCounter = 0;
            double startY = deckButton.getLayoutY()-CURRENT_ASSISTANT_VGAP;
            //System.out.println("Start y per le current assistant: "+startY);

            for (Map.Entry<String,Integer> cardChoice: cardsToRender.entrySet()) {
                //System.out.println("Renderizzo la carta "+cardChoice.getValue()+" di " + cardChoice.getKey());
                ImageView assistantImage = new ImageView("/graphics/assistant_" + cardChoice.getValue() + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY - cardCounter * CURRENT_ASSISTANT_VGAP);
                //System.out.println("Carta di "+cardChoice.getKey()+" in posizione x "+assistantImage.getX());
                //System.out.println("Carta di "+cardChoice.getKey()+" in posizione y "+assistantImage.getY());
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                String text = cardChoice.getKey().equals(gui.getPlayerNickname())?
                        "YOUR CARD": cardChoice.getKey().toUpperCase()+"'S CARD";
                Text cardLabel = new Text(text);
                cardLabel.setX(ASSISTANT_X);
                cardLabel.setY(assistantImage.getY()+ASSISTANT_IMAGE_HEIGHT*0.9);//test
                cardLabel.setFont(gui.getGameFont());
                cardLabel.setFill(Color.WHITE); //si potrebbe mettere nel css
                currentTurnCardsImages.put(assistantImage,cardLabel);
                gui.addElementToScene(assistantImage);
                gui.addElementToScene(cardLabel);
                //per centrare il testo, una variante di questo
                //Bounds textBounds = cardLabel.getBoundsInLocal();
                //double scalex = ASSISTANT_IMAGE_WIDTH/textBounds.getWidth();
                //cardLabel.setScaleX( scalex );

                assistantImage.setVisible(!showDeck);
                cardLabel.setVisible(!showDeck);
                cardLabel.toFront();
                cardCounter++;
            }
        }
        //System.out.println("Numero di immagini di carte assistente del turno: "+currentTurnCardsImages.size());
    }

    private void clearCurrentTurnCards(){
        for(Map.Entry<ImageView,Text> card : currentTurnCardsImages.entrySet()){
            gui.removeElementFromScene(card.getKey());
            gui.removeElementFromScene(card.getValue());
        }
        currentTurnCardsImages.clear();
    }

    private void clearColorChoiceBox(){
        gui.removeElementFromScene(colorChoiceBox);
    }

    public ActionParser getActionParser() {
        return actionParser;
    }
}
