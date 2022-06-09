package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static it.polimi.ingsw.Constants.*;

//TODO aggiungere effetti di selezione
//TODO sistemare bene sistema di selezione e pulire action parser dei vecchi parseTower, parseWizard ecc... (ora sono inutili)

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
    private HashMap<ImageView,ArrayList<ImageView>> islandToStudentsImages;
    private HashMap<ImageView,ArrayList<ImageView>> cloudToStudentsImages;
    private double centerX = 0;
    private double centerY = 0;
    private double bottomRightX = 0;
    private double bottomRightY = 0;
    private boolean waitTurn = false;
    private boolean shouldRenderDeck = true;
    private boolean initialized = false;
    private ArrayList<Integer> parameters;

    public void start(){ //metodo di inizializzazione chiamato da GUI. In alcune situazioni viene chiamato due volte ma noi dobbiamo inizializzare una volta sola
        if(!initialized){ //sarebbe meglio spostare questo controllo sulla GUI e generalizzarlo
            //commentato finché non capisco come centrare la finestra
            //Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            //centerX = (screenBounds.getWidth()-gui.getScreenX())/2;
            //centerY = (screenBounds.getHeight()-gui.getScreenY())/2;
            centerX = gui.getScreenX()/2;
            centerY = gui.getScreenY()/2;
            bottomRightX = gui.getScreenX();
            bottomRightY = gui.getScreenY();
            deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
            parameters = new ArrayList<>();
            deckImages = new ArrayList<>();
            initialized = true;
        }

    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }

    public void send(){ //funziona ma è brutto, lo sistemo quando ho più tempo
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

    private void populateIslands(ArrayList<ClientIsland> islands){
        islandToStudentsImages = new HashMap<>();
        double islandCenterX,islandCenterY;
        int islandCounter = 0;
        for(ImageView island : islandsImages){
            islandToStudentsImages.put(island,new ArrayList<>());
            ClientIsland clientIsland = getClientIslandFromImage(islands,islandCounter);
            islandCenterX = island.getX()+ISLAND_IMAGE_WIDTH/2;
            islandCenterY = island.getY()+ISLAND_IMAGE_HEIGHT/2;
            int studentCounter=0;
            //prova stress test
            ArrayList<Color> testStudents = new ArrayList<>(Arrays.asList(Color.BLUE,Color.BLUE,Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE,Color.RED,Color.RED,Color.PINK,Color.YELLOW,Color.GREEN,Color.RED));
            for(Color student : testStudents){ //li printiamo a cerchio invece che a matrice così sfruttiamo meglio lo spazio (esteticamente parlando)
                double angle = 2 * studentCounter * Math.PI / testStudents.size();
                double xOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS*testStudents.size()) * Math.cos(angle);
                double yOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS*testStudents.size()) * Math.sin(angle);
                double x = islandCenterX + xOffset ;
                double y = islandCenterY + yOffset ;
                ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
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
                islandToStudentsImages.get(island).add(studentImage);
                gui.addElementToScene(studentImage);
                studentCounter++;
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
                    //setSelectedAssistant(priority);
                    //System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                });
                assistantImage.setOnMouseEntered((MouseEvent e) -> {
                    handleHoverEvent(assistantImage, Clickable.ASSISTANT);
                });
                assistantImage.setOnMouseExited((MouseEvent e) -> {
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

    private void handleHoverEvent(Node n, Clickable hoveredElement){
        if(actionParser.canClick(gui.getCurrentState(),hoveredElement)){
            Bloom bloom = new Bloom();
            bloom.setThreshold(0.1);
            n.setEffect(bloom);
        }
    }
    private void handleClickEvent(int id,Clickable clickedElement){
        if(actionParser.canClick(gui.getCurrentState(),clickedElement)){
            switch(clickedElement){
                case ASSISTANT -> {
                    for (ImageView deckImage: deckImages)
                        deckImage.setEffect(null);

                    setSelectedAssistant(id);
                    System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                }
                case STUDENT -> {}
                case ISLAND -> {}
                case CLOUD -> {}

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
        //Volevo aggiungere un effetto DropShadow alla carta clickata ma poi con lo spostamento
        // del mouse si cancella. Piuttosto sarebbe meglio "estrarla dal mazzo", o comunque evidenziare la scelta in qualche modo
        /*
        for (ImageView deckImage: deckImages){
            String[] urlTokens = deckImage.getImage().getUrl().split("/");
            System.out.println(deckImage.getImage().getUrl());
            if (urlTokens[urlTokens.length-1].equals("assistant_" + priority + ".png")){
                System.out.println("Ho aggiunto l'effetto DropShasow alla carta selezionata");
                deckImage.setEffect(new DropShadow());
            }
        }
         */
        selectedAssistant = priority;
    }
}
