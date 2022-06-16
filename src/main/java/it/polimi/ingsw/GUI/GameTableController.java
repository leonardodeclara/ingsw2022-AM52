package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.messages.Message;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Constants.*;

//TODO sistemare bene sistema di selezione e pulire action parser dei vecchi parseTower, parseWizard ecc... (ora sono inutili)
//TODO drag n drop per gli studenti (semplifica la costruzione del messaggio)
//TODO centrare bene gli studenti sulle carte lobbyPersonality
//TODO sistemare visualizzazione sulle isole quando si hanno 2 colori (angle va cambiato)
//TODO creare un sistema che sappia quanti elementi puoi toccare e ti impedisca di toccarne di più (move from lobby 3 studenti fisso, alcune carte min 1 max 3)
//TODO visualizzazione messaggio last round


public class GameTableController extends GUIController implements UpdatableController{
    @FXML private ImageView player1Icon;
    @FXML private ImageView player2Icon;
    @FXML private ImageView player3Icon;
    @FXML private ImageView tableBounds;
    @FXML private ImageView deckButton;
    @FXML private Button sendButton;
    @FXML private Label contextMessage;
    private int renderedDashboard;
    private int selectedCloud = -1;
    private int selectedMNmove = -1;
    private ArrayList<Integer> selectedLobbyStudents;
    private ArrayList<Integer> selectedStudentsDestinations;
    private int selectedAssistant = -1; //priority
    private int selectedPersonality = -1;
    private ArrayList<ImageView> deckImages;
    private HashMap<ImageView,Text> currentTurnCardsImages;
    private ArrayList<GUIIsland> islands;
    private ArrayList<GUICloud> clouds;
    private ArrayList<GUIPersonality> personalities;
    private GUIBoard currentBoard;
    private HashMap<Integer,String> localIDToPlayer; //1 sempre giocatore,2 giocatore in alto,3 giocatore in mezzo (associa all'icona ingame, il nickname)
    private double centerX = 0;
    private double centerY = 0;
    private boolean waitTurn = false;
    private boolean initialized = false;
    private boolean showDeck;
    private ArrayList<Object> parameters;

    public void start(){ //metodo di inizializzazione chiamato da GUI. In alcune situazioni viene chiamato due volte ma noi dobbiamo inizializzare una volta sola
        if(!initialized){ //sarebbe meglio spostare questo controllo sulla GUI e generalizzarlo
            centerX = gui.getScreenX()/2;
            centerY = gui.getScreenY()/2 - 15;
            deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
            parameters = new ArrayList<>();
            deckImages = new ArrayList<>();
            currentTurnCardsImages = new HashMap<>();
            selectedLobbyStudents = new ArrayList<>();
            selectedStudentsDestinations = new ArrayList<>();
            initialized = true;
            showDeck=true;

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

            renderedDashboard = 1; //Di default renderizziamo la nostra board

        }

    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    public void send(){
        //qui si hanno tanti selezionabili, ma abbiamo la garanzia che solo quelli clickable per il currentstate saranno !=null
        //dobbiamo prendere e mettere quelli non null (Senza controllare quali sono) in un arraylist di object da passare a buildMessage

        //rivedere questo design considerando i drag n drop
        sendButton.setEffect(new DropShadow());
        if(selectedAssistant!=-1)
            parameters.add(selectedAssistant);
        if(selectedLobbyStudents.size() > 0)
            parameters.add(selectedLobbyStudents);
        if(selectedStudentsDestinations.size() > 0)
            parameters.add(selectedStudentsDestinations);
        if(selectedCloud!=-1)
            parameters.add(selectedCloud);
        if (selectedPersonality!=-1)
            parameters.add(selectedPersonality);
        if (selectedMNmove!=-1)
            parameters.add(selectedMNmove);

        if(parameters.size() > 0){ //se qualcosa è stato selezionato
            //da modificare, non funzione se provo a giocare una carta personaggio ad esempio in moveFromLobby
            //perché da per scontato che io stia muovendo le pedine e non giochi una carta
            Message message = client.buildMessageFromPlayerInput(parameters, gui.getCurrentState());
            gui.passToSocket(message);
            //dopo send deseleziona tutto
            setSelectedAssistant(-1);
            setSelectedCloud(-1);
            setSelectedMNmove(-1);
            selectedLobbyStudents.clear();
            selectedStudentsDestinations.clear();

            parameters.clear();
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
        populateDashboard();
        visualizeContextMessage();
        sendButton.setEffect(null); //modo temporaneo per resettare l'effetto generato dal click
        sendButton.toFront();

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();

    }

    private void renderIslands(){
        ArrayList<ClientIsland> clientIslands = gui.getIslands();
        ArrayList<GUIIsland> newIslands = new ArrayList<>();
        int numIslands = clientIslands.size();

        for (ClientIsland island : clientIslands) {
            int i = island.getIslandIndex(); //potrebbe dare problemi quando si avranno isole con id sparsi
            double angle = 2 * i * Math.PI / numIslands ;
            double xOffset = ISLAND_CIRCLE_RADIUS * Math.cos(angle-Math.PI/2);
            double yOffset = ISLAND_CIRCLE_RADIUS * Math.sin(angle-Math.PI/2);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            GUIIsland guiIsland = new GUIIsland(i,x-ISLAND_IMAGE_WIDTH/2,y-ISLAND_IMAGE_HEIGHT/2,ISLAND_IMAGE_WIDTH,ISLAND_IMAGE_HEIGHT,this,gui);
            guiIsland.setClientIsland(island);
            guiIsland.setEvents();
            guiIsland.render();
            newIslands.add(guiIsland);
        }
        islands=newIslands;
    }
    /*
    private void addStudentToIsland(ImageView islandImage,int islandCounter,ClientIsland island,Color student){
        ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");

        long numberOfStudents = island.getStudents()
                .stream()
                .filter(color -> color.equals(student)).count();
        if(numberOfStudents > 0){
            Tooltip numOfStudents = new Tooltip(Long.toString(numberOfStudents+1));
            numOfStudents.setShowDelay(Duration.seconds(0.1));
            Tooltip.install(studentImage, numOfStudents);
            System.out.println("Aggiungo uno studente"+student+" all'isola "+islandCounter+" e ora ce ne sono "+(numberOfStudents+1));
        }else{
            List<Color> distinctStudents = island.getStudents().stream().distinct().toList();
            distinctStudents.add(student);
            populateIsland(islandImage,gui.getIslands(),islandCounter,distinctStudents);
            System.out.println("Ho aggiunto uno studente"+student+" all'isola "+islandCounter+" e ora ce n'è uno");

        }

    }
    */

    private void renderClouds(){
        ArrayList<GUICloud> newClouds = new ArrayList<>();
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
            GUICloud guiCloud = new GUICloud(i, x-CLOUD_IMAGE_WIDTH/2,y-CLOUD_IMAGE_HEIGHT/2,CLOUD_IMAGE_WIDTH,CLOUD_IMAGE_HEIGHT,this,gui);
            guiCloud.setEvents();
            guiCloud.render();
            newClouds.add(guiCloud);
        }
        clouds=newClouds;
    }

    private void populateDashboard(){
        if(currentBoard != null){ //se c'è già qualcosa renderizzato pulisci prima quello
            currentBoard.clearBoard();
        }
        ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard));
        currentBoard = new GUIBoard(clientBoard,gui,this,tableBounds);
        currentBoard.populate(); //poi renderizzi quello nuovo
        /*
        populateTables(clientBoard);
        populateLobby(clientBoard);
        populateTowers(clientBoard);
        populateTeachers(clientBoard);
        */

    }

    private void renderPersonalityCards(){
        ArrayList<ClientPersonality> cards = gui.getPersonalityCards();
        ArrayList<GUIPersonality> newPersonalities = new ArrayList<>();
        double cardY,cardX;
        int i = 0;
        for (ClientPersonality card: cards){
            int cardId = card.getCardID();
            System.out.println("GameTableController: renderizzo la carta personaggio "+cardId);
            cardY = centerY + ISLAND_IMAGE_HEIGHT*1.1; //scelta a caso
            cardX= centerX - PERSONALITY_IMAGE_WIDTH + i*PERSONALITY_IMAGE_WIDTH;
            GUIPersonality guiPersonality = new GUIPersonality(cardId,cardX-PERSONALITY_IMAGE_WIDTH/2,cardY-PERSONALITY_IMAGE_HEIGHT/2,PERSONALITY_IMAGE_WIDTH,PERSONALITY_IMAGE_HEIGHT,this,gui);
            guiPersonality.render();
            guiPersonality.setEvents();
            newPersonalities.add(guiPersonality);
            i++;
        }
        personalities=newPersonalities;
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
            System.out.println("Ci sono " + deck.size() + " carte nel mazzo");
            for (Integer priority : deck.keySet()) {
                System.out.println("Renderizzo la carta " + priority);
                ImageView assistantImage = new ImageView("/graphics/assistant_" + priority + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY + deckCounter * ASSISTANT_Y_OFFSET);
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                assistantImage.setOnMouseClicked((MouseEvent e) -> {
                    handleClickEvent(priority, Clickable.ASSISTANT);
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
            name = new Text(playerName.toUpperCase()+"'S BOARD");
        name.setLayoutX(playerIcon.getLayoutX());
        System.out.println("Posizione x del buttone di " + playerName + "è " + playerIcon.getLayoutX());
        name.setLayoutY(playerIcon.getLayoutY()+NAME_TO_BUTTON_VGAP);
        System.out.println("Posizione y del buttone di " + playerName + "è " + playerIcon.getLayoutY());
        name.setFont(Font.font(15));
        gui.addElementToScene(name);
        Tooltip message = new Tooltip("CLICK ON THE CIRCLE TO SHOW "+playerName.toUpperCase()+"'S BOARD");
        message.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(playerIcon, message);
    }

    private void visualizeContextMessage(){
        String messageToShow ="";
        String messageForToolTip="";
        List<String> texts= gui.getCurrentState().getGUIContextMessage(gui.getGB());
        for (String text: texts){
            messageToShow+=text+" ";
            messageForToolTip+=text+"\n";
        }
        contextMessage.setText(messageToShow);
        Tooltip fullMessage = new Tooltip(messageForToolTip);
        fullMessage.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(contextMessage, fullMessage);
    }

    public void handleSelectionEffect(Node n, Clickable type){ //gestisce gli effetti di selezione per ogni clickable (vedremo in futuro se avrà senso averlo così generalizzato)
        if(actionParser.canClick(gui.getCurrentState(),type)){
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

    public void handleClickEvent(int id,Clickable clickedElement){
        if(actionParser.canClick(gui.getCurrentState(),clickedElement)){
            switch(clickedElement){
                case ASSISTANT -> {
                    setSelectedAssistant(id);
                    System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                }
                case CLOUD -> {
                    setSelectedCloud(id);
                }
                case PERSONALITY -> {
                    //si potrebbe aggiungere un controllo qui in modo che se sono
                    //in move from lobby, ho spostato due studenti e poi clicco una carta questo click non viene contato
                    //altrimenti si modifica actionParser.canClick
                    setSelectedPersonality(id);
                    System.out.println("Hai cliccato sulla carta personaggio ");
                }
                case LOBBY_STUDENT -> {
                    selectedLobbyStudents.add(id);
                }
                case ISLAND -> {
                    //rivedere, per certe carte personaggio bisogna fare click sull'isola per selezionarla,
                    //quindi bisogna distinguere i casi in cui memorizzare il numero di passi o l'indice
                    setSelectedMNmove(extractMNsteps(id));
                    System.out.println("Hai cliccato su un'isola");
                }

            }
        }
        else
            System.out.println("Non puoi cliccare "+clickedElement+" se sei in "+gui.getCurrentState());
    }

    public void onPlayer1Click(){
        if(renderedDashboard!=1){
            //clearBoard();
            renderedDashboard = 1;
            populateDashboard();
        }
    }


    public void onPlayer2Click(){
        if(renderedDashboard!=2){
            //clearBoard();
            renderedDashboard = 2;
            populateDashboard();
        }
    }
    public void onPlayer3Click(){
        if(renderedDashboard!=3){
            //clearBoard();
            renderedDashboard = 3;
            populateDashboard();
        }
    }

    public void setSelectedCloud(int id){
        selectedCloud = id;
    }

    public void setSelectedAssistant(int priority){
        selectedAssistant = priority;
    }
    private void setSelectedPersonality(int personalityId) {
        selectedPersonality = personalityId;
    }

    public void setSelectedMNmove(int numOfSteps) {
        selectedMNmove = numOfSteps;
    }


    public void addSelectedStudent(int studentID,int destID){
        selectedLobbyStudents.add(studentID);
        selectedStudentsDestinations.add(destID);
    }

    public int extractMNsteps(int islandId){
        return gui.getGB().getMotherNatureDistance(islandId);
    }

    public void changeShowedCards(MouseEvent mouseEvent) {
        System.out.println("Ho cliccato per cambiare le carte da mostrare");
        if(showDeck) {
            System.out.println("ora mostro le currentAssistantCards");
            for (ImageView deckCardImage : deckImages)
                deckCardImage.setVisible(false);
            for (Map.Entry<ImageView,Text> turnCardImage: currentTurnCardsImages.entrySet()){
                turnCardImage.getKey().setVisible(true);
                turnCardImage.getValue().setVisible(true);
            }
            showDeck=false;
        }
        else{
            System.out.println("ora mostro le deckImages");
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
            System.out.println("Start y per le current assistant: "+startY);

            for (Map.Entry<String,Integer> cardChoice: cardsToRender.entrySet()) {
                System.out.println("Renderizzo la carta "+cardChoice.getValue()+" di " + cardChoice.getKey());
                ImageView assistantImage = new ImageView("/graphics/assistant_" + cardChoice.getValue() + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY - cardCounter * CURRENT_ASSISTANT_VGAP);
                System.out.println("Carta di "+cardChoice.getKey()+" in posizione x "+assistantImage.getX());
                System.out.println("Carta di "+cardChoice.getKey()+" in posizione y "+assistantImage.getY());
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
                assistantImage.setPreserveRatio(true);
                String text = cardChoice.getKey().equals(gui.getPlayerNickname())?
                        "YOUR CARD": cardChoice.getKey().toUpperCase()+"'S CARD";
                Text cardLabel = new Text(text);
                cardLabel.setX(ASSISTANT_X);
                cardLabel.setY(assistantImage.getY()+ASSISTANT_IMAGE_HEIGHT*0.9);//test
                cardLabel.setFont(Font.font(13));
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
        System.out.println("Numero di immagini di carte assistente del turno: "+currentTurnCardsImages.size());
    }

    private void clearCurrentTurnCards(){
        for(Map.Entry<ImageView,Text> card : currentTurnCardsImages.entrySet()){
            gui.removeElementFromScene(card.getKey());
            gui.removeElementFromScene(card.getValue());
        }
        currentTurnCardsImages.clear();
    }

}
