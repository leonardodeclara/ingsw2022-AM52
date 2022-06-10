package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
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


public class GameTableController extends GUIController implements UpdatableController{
    @FXML private ImageView deckButton;
    @FXML private Button sendButton;
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
            initialized = true;

            sendButton.setOnMouseEntered(e -> sendButton.setEffect(new Bloom()));
            sendButton.setOnMouseExited(e -> sendButton.setEffect(null));


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

        sendButton.setEffect(null); //modo temporaneo per resettare l'effetto generato dal click
        sendButton.toFront();

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();

    }

    private void renderIslands(){

        islandsImages = new ArrayList<>();

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


        for(ClientCloud cloud : clouds) {
            int i = cloud.getCloudIndex();
            double angle = 2 * i * Math.PI / numClouds ;
            double xOffset = CLOUD_CIRCLE_RADIUS * Math.cos(angle);
            double yOffset = CLOUD_CIRCLE_RADIUS * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
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

    //se il giocatore decide come stress test di mettere 35 studenti su una sola isola o mettiamo un counter dopo una certa soglia (esteticamente orrendo ma quando si superano i tot studenti è l'unico modo)
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
