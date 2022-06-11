package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;

import java.util.*;

import static it.polimi.ingsw.Constants.*;

//TODO aggiungere effetti di selezione
//TODO sistemare bene sistema di selezione e pulire action parser dei vecchi parseTower, parseWizard ecc... (ora sono inutili)

//TODO gridpanel per la table così da non dover mettere un controllo (altrimenti si potranno mettere 150 studenti nella table)
//TODO drag n drop per gli studenti (semplifica la costruzione del messaggio)
//TODO centrare bene gli studenti sulle isole (ruotando il cerchio di una costante)
//TODO centrare bene gli studenti sulle carte lobbyPersonality
//TODO popolare banPersonality (basta simbolo ban con numero di ban che esce quando ti fermi sopra)
//TODO popolazione torri

//per gli spostamenti si avranno dei click/drag n drop che modificano lato client la GUI. Quando poi si fa CONFIRM le modifiche avvengono su server
//le modifiche lato client non sono tutte lecite lato server, perchè lato client avvengono solo controlli strutturali, non di logica di gioco.
//Questo significa che se si vanno a posizionare degli studenti su un'isola dove non si possono posizionare e si preme CONFIRM, apparirà un messaggio di errore
//e la board tornerà allo stato precedente all'azione errata dal player (Dato che c'è l'update e lato server non è avvenuta modifica)
public class GameTableController extends GUIController implements UpdatableController{
    @FXML public ImageView player2Icon;
    @FXML public ImageView player3Icon;
    @FXML public ImageView player1Icon;
    @FXML private ImageView deckButton;
    private int renderedDashboard;
    @FXML private Button sendButton;
    @FXML private TilePane blueTable;
    private int selectedIslandID = -1;
    private int selectedCloud = -1;
    private int selectedStudent = -1; //relative to island
    private int selectedAssistant = -1; //priority
    private ArrayList<ImageView> islandsImages;
    private ArrayList<ImageView> cloudsImages;
    private ArrayList<ImageView> deckImages;
    private ArrayList<ImageView> personalitiesImages;
    private HashMap<ImageView,ArrayList<ImageView>> islandToStudentsImages;
    private HashMap<ImageView,ArrayList<ImageView>> cloudToStudentsImages;
    private HashMap<ImageView, ArrayList<ImageView>> personalityToStudentsImages;
    private ArrayList<ImageView> tablesStudents;
    private ArrayList<ImageView> lobbyStudents;
    private ArrayList<ImageView> boardTeachers;
    private HashMap<Integer,String> localIDToPlayer; //1 sempre giocatore,2 giocatore in alto,3 giocatore in mezzo (associa all'icona ingame, il nickname)
    private double centerX = 0;
    private double centerY = 0;
    private boolean waitTurn = false;
    private boolean initialized = false;
    private ArrayList<Integer> parameters;

    public void start(){ //metodo di inizializzazione chiamato da GUI. In alcune situazioni viene chiamato due volte ma noi dobbiamo inizializzare una volta sola
        if(!initialized){ //sarebbe meglio spostare questo controllo sulla GUI e generalizzarlo
            centerX = gui.getScreenX()/2;
            centerY = gui.getScreenY()/2 - 15;
            deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
            parameters = new ArrayList<>();
            deckImages = new ArrayList<>();
            tablesStudents = new ArrayList<>();
            lobbyStudents = new ArrayList<>();
            boardTeachers = new ArrayList<>();
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

            renderedDashboard = 1; //Di default renderizziamo la nostra board
        }

    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    public void send(){ //funziona ma è brutto, lo sistemo quando ho più tempo
        sendButton.setEffect(new DropShadow());
        if(selectedAssistant!=-1)
            parameters.add(selectedAssistant);
        if(selectedStudent!=-1)
            parameters.add(selectedStudent);
        if(selectedIslandID!=-1)
            parameters.add(selectedIslandID);
        if(selectedCloud!=-1)
            parameters.add(selectedCloud);

        if(parameters.size() > 0){ //se qualcosa è stato selezionato
            Message message = client.buildMessageFromPlayerInput(actionParser.parse(gui.getCurrentState(),parameters), gui.getCurrentState());
            gui.passToSocket(message);
            //dopo send deseleziona tutto
            setSelectedAssistant(-1);
            setSelectedCloud(-1);
            setSelectedIslandID(-1);
            setSelectedStudent(-1,-1);
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
        sendButton.setEffect(null); //modo temporaneo per resettare l'effetto generato dal click
        sendButton.toFront();

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();

    }

    private void renderIslands(){

        islandsImages = new ArrayList<>(); //andrà cambiato (vogliamo avere sempre a portata le isole da togliere dalla GUI)

        ArrayList<ClientIsland> islands = gui.getIslands();

        int numIslands = islands.size();

        for (ClientIsland island : islands) {
            int i = island.getIslandIndex(); //potrebbe dare problemi quando si avranno isole con id sparsi
            double angle = 2 * i * Math.PI / numIslands ;
            double xOffset = ISLAND_CIRCLE_RADIUS * Math.cos(angle-Math.PI/2);
            double yOffset = ISLAND_CIRCLE_RADIUS * Math.sin(angle-Math.PI/2);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            ImageView islandImage = new ImageView("/graphics/island"+((i%3)+1)+".png");
            islandImage.setX(x-ISLAND_IMAGE_WIDTH/2);
            islandImage.setY(y-ISLAND_IMAGE_HEIGHT/2);
            islandImage.setFitHeight(ISLAND_IMAGE_HEIGHT);
            islandImage.setFitWidth(ISLAND_IMAGE_WIDTH);
            islandImage.setOnMouseClicked((MouseEvent e) -> {
                handleClickEvent(i,Clickable.ISLAND);
                //setSelectedIslandID(i);
                //System.out.println("Hai cliccato sull'isola "+selectedIslandID);
            });
            islandsImages.add(islandImage);
            gui.addElementToScene(islandImage);
        }
        populateIslands(islands);

    }

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
            cloudsImages.add(cloudImage);
            gui.addElementToScene(cloudImage);
        }
        populateClouds(clouds);
    }

    private void populateDashboard(){
        ClientBoard clientBoard = gui.getPlayerBoard(localIDToPlayer.get(renderedDashboard));
        populateTables(clientBoard);
        populateLobby(clientBoard);
        populateTeachers(clientBoard);


    }
    private void populateTeachers(ClientBoard clientBoard){
        int teacherCounter = 0;
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color teacher : tableColors){
            if(clientBoard.getTeacherTable().contains(teacher)){
                ImageView teachersImage = new ImageView("/graphics/"+teacher.toString().toLowerCase()+"_teacher.png");
                teachersImage.setFitWidth(TEACHER_BOARD_WIDTH);
                teachersImage.setFitHeight(TEACHER_BOARD_HEIGHT);
                teachersImage.setX(TEACHER_BOARD_START_X+teacherCounter*STUDENT_TABLE_HGAP);
                teachersImage.setY(TEACHER_BOARD_START_Y);
                boardTeachers.add(teachersImage);
                gui.addElementToScene(teachersImage);
            }
            teacherCounter++;
        }
    }
    private void populateLobby(ClientBoard clientBoard){
        int studentRowCounter = 0;
        int studentColumnCounter = 0;
        int studentIDCounter = 0; //identificativo studente lobby
        for(Color student : clientBoard.getLobby()){
            ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
            studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
            studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
            studentImage.setX(STUDENT_BOARD_START_X+studentColumnCounter*STUDENT_TABLE_HGAP);
            studentImage.setY(STUDENT_LOBBY_START_Y+studentRowCounter*STUDENT_LOBBY_VGAP);
            int finalStudentIDCounter = studentIDCounter; //event handler accetta solo variabili final (lol)
            studentImage.setOnMouseClicked((MouseEvent e) -> {
                handleClickEvent(finalStudentIDCounter,Clickable.STUDENT);
            });
            lobbyStudents.add(studentImage);
            gui.addElementToScene(studentImage);
            if(studentColumnCounter==4 && studentRowCounter == 0){ //fa abbastanza schifo, miglioro appena ho tempo
                studentColumnCounter=0;
                studentRowCounter++;
            }else{
                studentColumnCounter++;
            }
            studentIDCounter++;
        }
    }

    private void populateTables(ClientBoard clientBoard){
        int tableCounter = 0;
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color color : tableColors){
            int numOfStudents = clientBoard.getStudentsTable().get(color);
            //int numOfStudents = 10; test
            String studentImagePath = "/graphics/"+color.toString().toLowerCase()+"_student.png";
            for(int i = 0; i< numOfStudents;i++){
                ImageView studentImage = new ImageView(studentImagePath);
                studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
                studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
                System.out.println("Printo studente nella table"+color+" su (X,Y): "+(947+tableCounter*STUDENT_TABLE_HGAP)+(148+i*STUDENT_TABLE_VGAP));
                studentImage.setX(STUDENT_BOARD_START_X+tableCounter*STUDENT_TABLE_HGAP);
                studentImage.setY(STUDENT_TABLE_START_Y+i*STUDENT_TABLE_VGAP);
                studentImage.setOnMouseClicked((MouseEvent e) -> {
                    handleClickEvent(color.getIndex(),Clickable.STUDENT); //come id passa l'index del colore della board
                });
                tablesStudents.add(studentImage);
                gui.addElementToScene(studentImage);
            }
            tableCounter++;
        }
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
                System.out.println("aggiungere immagini degli studenti alla carta "+cardId);
                populateLobbyPersonality(card,cardImage);
            }
            else if (card.getBans()!=0)
                System.out.println("aggiungere immagini dei ban alla carta " +cardId);
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
                handleClickEvent(personality.getStudents().indexOf(student),Clickable.STUDENT); //come id passa il primo studente di quel colore che trova nell'isola
            });
            gui.addElementToScene(studentImage);
            studentImage.toFront();
            System.out.println("Aggiunto studente "+student.toString());
            cardStudentsImages.add(studentImage);
        }
        personalityToStudentsImages.put(clientCard,cardStudentsImages);
    }

    private void populateBanPersonality(ClientPersonality personality){
    }

    private void populateIslands(ArrayList<ClientIsland> islands){
        islandToStudentsImages = new HashMap<>();
        double islandCenterX,islandCenterY;
        int islandCounter = 0;
        for(ImageView island : islandsImages){
            islandToStudentsImages.put(island,new ArrayList<>());
            ClientIsland clientIsland = getClientIslandFromImage(islands,islandCounter);
            islandCenterX = island.getX()+ISLAND_IMAGE_WIDTH/2;
            islandCenterY = island.getY()+ISLAND_IMAGE_HEIGHT/2;
            List<Color> distinctStudents = clientIsland.getStudents().stream().distinct().toList();
            for(Color student : distinctStudents){
                long numberOfStudents = clientIsland.getStudents()
                        .stream()
                        .filter(color -> color.equals(student)).count();
                double angle = 2 * distinctStudents.indexOf(student) * Math.PI / Color.values().length;
                double xOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS * distinctStudents.size() * 1.5) * Math.cos(angle);
                double yOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS * distinctStudents.size() * 1.5) * Math.sin(angle);
                double x = islandCenterX + xOffset ;
                double y = islandCenterY + yOffset ;
                ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
                studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
                studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
                studentImage.setPreserveRatio(true);
                studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
                studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
                studentImage.setOnMouseClicked((MouseEvent e) -> {
                    handleClickEvent(clientIsland.getStudents().indexOf(student),Clickable.STUDENT); //come id passa il primo studente di quel colore che trova nell'isola
                });
                Tooltip numOfStudents = new Tooltip(Long.toString(numberOfStudents));
                numOfStudents.setShowDelay(Duration.seconds(0.1));
                Tooltip.install(studentImage, numOfStudents);
                islandToStudentsImages.get(island).add(studentImage);
                gui.addElementToScene(studentImage);
            }
            islandCounter++;
        }
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
                    handleClickEvent(0,Clickable.STUDENT);
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

    private void handleHoverEvent(Node n, Clickable hoveredElement){ //gestisce l'hover per ogni clickable (vedremo in futuro se avrà senso averlo generalizzato)
        if(actionParser.canClick(gui.getCurrentState(),hoveredElement)){
            //aggiungo il bloom solo se la carta non è stata clickata
            if (n.getEffect()==null){
                Bloom bloom = new Bloom();
                bloom.setThreshold(0.1);
                n.setEffect(bloom);}
        }
    }
    private void handleClickEvent(int id,Clickable clickedElement){
        if(actionParser.canClick(gui.getCurrentState(),clickedElement)){
            switch(clickedElement){
                case ASSISTANT -> {
                    setSelectedAssistant(id);
                    System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                }
                case STUDENT -> {}
                case ISLAND -> {}
                case CLOUD -> {}
                case PERSONALITY -> {}

            }
        }
        else
            System.out.println("Non puoi cliccare "+clickedElement+" se sei in "+gui.getCurrentState());
    }


    public void onPlayer1Click(){
        renderedDashboard = 1;
        populateDashboard(); //richiama il render della board
    }


    public void onPlayer2Click(){
        renderedDashboard = 2;
        populateDashboard();
    }
    public void onPlayer3Click(){
        renderedDashboard = 3;
        populateDashboard();
    }

    public void setSelectedIslandID(int id){
        selectedIslandID = id;
    }

    public void setSelectedCloud(int id){
        selectedCloud = id;
    }

    public void setSelectedStudent(int studentID,int islandID){
        selectedStudent = studentID;
        selectedIslandID = islandID;
    }

    public void setSelectedAssistant(int priority){
        selectedAssistant = priority;
    }
}
