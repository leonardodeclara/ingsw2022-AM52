package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

import static it.polimi.ingsw.Constants.*;

//TODO sistemare bene sistema di selezione e pulire action parser dei vecchi parseTower, parseWizard ecc... (ora sono inutili)
//TODO aggiungere associazione giocatore-carta assistente
//TODO drag n drop per gli studenti (semplifica la costruzione del messaggio)
//TODO centrare bene gli studenti sulle carte lobbyPersonality
//TODO popolare banPersonality (basta simbolo ban con numero di ban che esce quando ti fermi sopra)
//TODO sistemare visualizzazione sulle isole quando si hanno 2 colori (angle va cambiato)
//TODO creare un sistema che sappia quanti elementi puoi toccare e ti impedisca di toccarne di più (move from lobby 3 studenti fisso, alcune carte min 1 max 3)

public class GameTableController extends GUIController implements UpdatableController{
    @FXML public ImageView player1Icon;
    @FXML public ImageView player2Icon;
    @FXML public ImageView player3Icon;
    @FXML public ImageView tableBounds;
    @FXML private ImageView deckButton;
    private int renderedDashboard;
    @FXML private Button sendButton;
    @FXML private Label contextMessage;
    private int selectedCloud = -1;
    private ArrayList<Integer> selectedLobbyStudents;
    private ArrayList<Integer> selectedStudentsDestinations;
    private int selectedAssistant = -1; //priority
    private int selectedPersonality = -1;
    private ArrayList<ImageView> cloudsImages;
    private ArrayList<ImageView> deckImages;
    private ArrayList<ImageView> personalitiesImages;
    private HashMap<ImageView,ArrayList<ImageView>> cloudToStudentsImages;
    private HashMap<ImageView, ArrayList<ImageView>> personalityToStudentsImages;
    private GUIBoard currentBoard;
    private HashMap<Integer,String> localIDToPlayer; //1 sempre giocatore,2 giocatore in alto,3 giocatore in mezzo (associa all'icona ingame, il nickname)
    private double centerX = 0;
    private double centerY = 0;
    private boolean waitTurn = false;
    private boolean initialized = false;
    private ArrayList<Object> parameters;

    public void start(){ //metodo di inizializzazione chiamato da GUI. In alcune situazioni viene chiamato due volte ma noi dobbiamo inizializzare una volta sola
        if(!initialized){ //sarebbe meglio spostare questo controllo sulla GUI e generalizzarlo
            centerX = gui.getScreenX()/2;
            centerY = gui.getScreenY()/2 - 15;
            deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
            parameters = new ArrayList<>();
            deckImages = new ArrayList<>();
            selectedLobbyStudents = new ArrayList<>();
            selectedStudentsDestinations = new ArrayList<>();
            initialized = true;

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

        if(parameters.size() > 0){ //se qualcosa è stato selezionato
            Message message = client.buildMessageFromPlayerInput(parameters, gui.getCurrentState());
            gui.passToSocket(message);
            //dopo send deseleziona tutto
            setSelectedAssistant(-1);
            setSelectedCloud(-1);
            //setSelectedIslandID(-1);
            //setSelectedStudent(-1,-1);
            parameters.clear();
        }

    }

    @Override
    public void update() {
        renderIslands();
        renderClouds();
        renderDeck();
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
        ArrayList<ClientIsland> islands = gui.getIslands();

        int numIslands = islands.size();

        for (ClientIsland island : islands) {
            int i = island.getIslandIndex(); //potrebbe dare problemi quando si avranno isole con id sparsi
            double angle = 2 * i * Math.PI / numIslands ;
            double xOffset = ISLAND_CIRCLE_RADIUS * Math.cos(angle-Math.PI/2);
            double yOffset = ISLAND_CIRCLE_RADIUS * Math.sin(angle-Math.PI/2);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            GUIIsland guiIsland = new GUIIsland(i,x-ISLAND_IMAGE_WIDTH/2,y-ISLAND_IMAGE_HEIGHT/2,ISLAND_IMAGE_WIDTH,ISLAND_IMAGE_HEIGHT,this,gui);
            guiIsland.setEvents();
            guiIsland.render();

        }
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
        cloudsImages = new ArrayList<>();
        ArrayList<ClientCloud> clouds = gui.getClouds();
        int numClouds = clouds.size();

        int personalitiesYOffset = ((gui.getGB().isExpertGame() && gui.getNumOfPlayers()==3) ? CLOUD_PERSONALITY_OFFSET : 0);
        for(ClientCloud cloud : clouds) {
            int i = cloud.getCloudIndex();
            double angle = 2 * i * Math.PI / numClouds ;
            double xOffset = CLOUD_CIRCLE_RADIUS * Math.cos(angle);
            double yOffset = CLOUD_CIRCLE_RADIUS * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset - personalitiesYOffset;
            ImageView cloudImage = new ImageView("/graphics/cloud"+((i%3)+1)+".png");
            cloudImage.setX(x-CLOUD_IMAGE_WIDTH/2);
            cloudImage.setY(y-CLOUD_IMAGE_HEIGHT/2);
            cloudImage.setPreserveRatio(true);
            cloudImage.setFitHeight(CLOUD_IMAGE_HEIGHT);
            cloudImage.setFitWidth(CLOUD_IMAGE_WIDTH);
            cloudImage.setOnMouseClicked((MouseEvent e) -> {
                handleClickEvent(i,Clickable.CLOUD);
                //setSelectedCloud(i);
                //System.out.println("Hai cliccato sulla nuvola "+selectedCloud);
            });
            cloudImage.setOnMouseEntered((MouseEvent e) -> {
                handleHoverEvent(cloudImage, Clickable.CLOUD);
            });
            cloudImage.setOnMouseExited((MouseEvent e) -> {
                if (cloudImage.getEffect()==null || !(DropShadow.class).equals(cloudImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                    cloudImage.setEffect(null);
            });
            cloudsImages.add(cloudImage);
            gui.addElementToScene(cloudImage);
        }
        populateClouds(clouds);
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
        personalitiesImages = new ArrayList<>();
        double cardY,cardX;
        int i = 0;
        for (ClientPersonality card: cards){
            int cardId = card.getCardID();
            System.out.println("GameTableController: renderizzo la carta personaggio "+cardId);
            cardY = centerY + ISLAND_IMAGE_HEIGHT*1.1; //scelta a caso
            cardX= centerX -PERSONALITY_IMAGE_WIDTH + i*PERSONALITY_IMAGE_WIDTH;
            ImageView cardImage = new ImageView("/graphics/personality_"+cardId+".jpg");
            cardImage.setX(cardX-PERSONALITY_IMAGE_WIDTH/2);
            cardImage.setY(cardY-PERSONALITY_IMAGE_HEIGHT/2);
            cardImage.setFitHeight(PERSONALITY_IMAGE_HEIGHT);
            cardImage.setFitWidth(PERSONALITY_IMAGE_WIDTH);
            cardImage.setPreserveRatio(true);
            cardImage.setOnMouseClicked((MouseEvent e)-> handleClickEvent(cardId,Clickable.PERSONALITY));
            personalitiesImages.add(cardImage);
            gui.addElementToScene(cardImage);
            if (card.isHasBeenUsed()){
                ImageView coinImage = new ImageView("graphics/coin.png");
                double coinX = cardImage.getX()+PERSONALITY_IMAGE_WIDTH/2;
                double coinY = cardImage.getY()+PERSONALITY_IMAGE_HEIGHT/2;
                coinImage.setX(coinX-COIN_IMAGE_WIDTH/2);
                coinImage.setY(coinY-COIN_IMAGE_HEIGHT/2);
                coinImage.setFitWidth(COIN_IMAGE_WIDTH);
                coinImage.setFitHeight(COIN_IMAGE_HEIGHT);
                coinImage.setPreserveRatio(true);
                gui.addElementToScene(coinImage);
            }
            if (card.getStudents()!=null && card.getStudents().size()>0){
                System.out.println("aggiungo immagini degli studenti alla carta "+cardId);
                populateLobbyPersonality(card,cardImage);
            }
            else if (card.getBans()!=0){
                System.out.println("aggiungo immagini dei ban alla carta " +cardId);
                populateBanPersonality(card,cardImage);
            }
            i++;

        }

    }

    private void populateLobbyPersonality(ClientPersonality personality, ImageView clientCard){
        if (personalityToStudentsImages==null)
            personalityToStudentsImages= new HashMap<>();
        ArrayList<Color> cardStudents = personality.getStudents();
        ArrayList<ImageView> cardStudentsImages = new ArrayList<>();
        int halfAmountOfStudents=cardStudents.size()/2;
        double offsetX=STUDENT_IMAGE_WIDTH*2;
        double offsetY;
        double startY = clientCard.getY()+PERSONALITY_IMAGE_HEIGHT*0.7;
        double startX = clientCard.getX()+PERSONALITY_IMAGE_WIDTH/2-STUDENT_IMAGE_WIDTH*1.5;
        if (halfAmountOfStudents==3){
            startX=startX-STUDENT_IMAGE_WIDTH;
            offsetX=STUDENT_IMAGE_WIDTH*1.5;
        }

        for (int i = 0; i<cardStudents.size();i++){
            Color student = cardStudents.get(i);
            ImageView studentImage = new ImageView("/graphics/" + student.toString().toLowerCase() + "_student.png");
            studentImage.setX(startX + (i%halfAmountOfStudents)*offsetX);
            offsetY= i>=halfAmountOfStudents? STUDENT_IMAGE_HEIGHT*1.5: 0;
            studentImage.setY(startY-STUDENT_IMAGE_HEIGHT/2+offsetY);
            studentImage.setPreserveRatio(true);
            studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
            studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
            studentImage.setOnMouseClicked((MouseEvent e) -> {
                handleClickEvent(personality.getStudents().indexOf(student),Clickable.PERSONALITY_STUDENT); //come id passa il primo studente di quel colore che trova nell'isola
            });
            gui.addElementToScene(studentImage);
            studentImage.toFront();
            System.out.println("Aggiunto studente "+student);
            cardStudentsImages.add(studentImage);
        }
        personalityToStudentsImages.put(clientCard,cardStudentsImages);

    }

    private void populateBanPersonality(ClientPersonality personality, ImageView clientCard){
        double startY = clientCard.getY()+PERSONALITY_IMAGE_HEIGHT*0.65;
        double startX = clientCard.getX()+PERSONALITY_IMAGE_WIDTH/2;
        int banCount = personality.getBans();
        ImageView banImage = new ImageView("/graphics/deny_island_icon.png");
        banImage.setX(startX-BAN_IMAGE_WIDTH/2);
        banImage.setY(startY);
        banImage.setPreserveRatio(true);
        banImage.setFitHeight(BAN_IMAGE_HEIGHT);
        banImage.setFitWidth(BAN_IMAGE_WIDTH);
        Tooltip numOfBanTiles = new Tooltip(("AVAILABLE BANS: "+banCount));
        numOfBanTiles.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(banImage, numOfBanTiles);
        gui.addElementToScene(banImage);
    }



    private void populateClouds(ArrayList<ClientCloud> clouds){
        cloudToStudentsImages = new HashMap<>();
        double cloudCenterX,cloudCenterY;
        int cloudCounter = 0;
        for(ImageView cloud : cloudsImages){
            cloudToStudentsImages.put(cloud,new ArrayList<>());
            ClientCloud clientCloud = getClientCloudFromImage(clouds,cloudCounter);
            cloudCenterX = cloud.getX()+CLOUD_IMAGE_WIDTH/2;
            cloudCenterY = cloud.getY()+CLOUD_IMAGE_HEIGHT/2;
            int studentCounter = 0; //va tenuta traccia manualmente, indexOf trovava la prima occurence
            for(Color student : clientCloud.getStudents()){ //li printiamo a cerchio invece che a matrice così sfruttiamo meglio lo spazio (esteticamente parlando)
                double angle = 2 * studentCounter * Math.PI / clientCloud.getStudents().size();
                double xOffset = STUDENTS_CLOUD_CIRCLE_RADIUS * Math.cos(angle);
                double yOffset = STUDENTS_CLOUD_CIRCLE_RADIUS * Math.sin(angle);
                double x = cloudCenterX + xOffset ;
                double y = cloudCenterY + yOffset ;
                System.out.println("Metto su una nuvola uno studente "+student);
                ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
                System.out.println(cloudToStudentsImages.get(cloud).size());
                studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
                studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
                studentImage.setPreserveRatio(true);
                studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
                studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
                studentImage.setOnMouseClicked((MouseEvent e) -> {
                    handleClickEvent(0,Clickable.CLOUD_STUDENT);
                    //setSelectedStudent(clientIsland.getStudents().indexOf(student),clientIsland.getIslandIndex());
                    //System.out.println("Hai cliccato sullo studente "+selectedStudent+" dell'isola "+selectedIslandID);
                });
                cloudToStudentsImages.get(cloud).add(studentImage);
                gui.addElementToScene(studentImage);
                studentCounter++;
            }
            cloudCounter++;
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
            System.out.println("Ci sono " + deck.size() + " carte nel mazzo");
            for (Integer priority : deck.keySet()) {
                System.out.println("Renderizzo la carta " + priority);
                ImageView assistantImage = new ImageView("/graphics/assistant_" + priority + ".png");
                assistantImage.setX(ASSISTANT_X);
                assistantImage.setY(startY + deckCounter * ASSISTANT_Y_OFFSET);
                assistantImage.setFitWidth(ASSISTANT_IMAGE_WIDTH);
                assistantImage.setFitHeight(ASSISTANT_IMAGE_HEIGHT);
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
        List<String> texts= gui.getCurrentState().getContextMessage(gui.getGB());
        for (String text: texts){
            messageToShow+=text+" ";
            messageForToolTip+=text+"\n";
        }
        contextMessage.setText(messageToShow);
        Tooltip fullMessage = new Tooltip(messageForToolTip);
        fullMessage.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(contextMessage, fullMessage);
    }

    private void handleSelectionEffect(Node n, Clickable type){ //gestisce gli effetti di selezione per ogni clickable (vedremo in futuro se avrà senso averlo così generalizzato)
        switch(type){
            case ASSISTANT -> {
                for(ImageView card : deckImages){
                    card.setEffect(null);
                }
                n.setEffect(new DropShadow());
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
                    setSelectedPersonality(id);
                    System.out.println("Hai cliccato sulla carta personaggio ");
                }
                case LOBBY_STUDENT -> {
                    selectedLobbyStudents.add(id);
                }
                case ISLAND -> {

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

    public void addSelectedStudent(int studentID,int destID){
        selectedLobbyStudents.add(studentID);
        selectedStudentsDestinations.add(destID);
    }
}
