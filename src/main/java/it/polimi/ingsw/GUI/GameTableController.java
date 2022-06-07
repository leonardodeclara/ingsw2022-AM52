package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static it.polimi.ingsw.Constants.*;

//TODO rendere dinamico il radius e la dimensione degli elementi in funzione della lunghezza degli array (fondamentale per gli studenti)
//TODO far funzionare chiusura/apertura deck


public class GameTableController extends GUIController implements UpdatableController{
    @FXML
    public ImageView deckButton;
    public Button sendButton;
    int selectedIslandID = -1;
    int selectedCloud = -1;
    int selectedStudent = -1; //relative to island
    int selectedAssistant = -1; //priority
    ArrayList<ImageView> islandsImages;
    ArrayList<ImageView> cloudsImages;
    ArrayList<ImageView> deckImages;
    HashMap<ImageView,ArrayList<ImageView>> islandToStudentsImages;
    double centerX = 0;
    double centerY = 0;
    double bottomRightX = 0;
    double bottomRightY = 0;
    boolean waitTurn = false;
    boolean shouldRenderDeck = true;
    ArrayList<Integer> parameters;

    public void start(){
        centerX = gui.getScreenX()/2;
        centerY = gui.getScreenY()/2;
        bottomRightX = gui.getScreenX();
        bottomRightY = gui.getScreenY();
        deckButton.setImage(new Image("/graphics/Wizard_"+(gui.getWizard()+1)+".png"));
        parameters = new ArrayList<>();
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

        if(waitTurn)
            gui.disableScene();
        else
            gui.enableScene();

        sendButton.toFront();
    }

    private void renderIslands(){

        islandsImages = new ArrayList<>();

        ArrayList<ClientIsland> islands = gui.getIslands();

        int numIslands = islands.size();

        for (ClientIsland island : islands) {
            int i = island.getIslandIndex();
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
            for(Color student : clientIsland.getStudents()){ //li printiamo a cerchio invece che a matrice così sfruttiamo meglio lo spazio (esteticamente parlando)
                double angle = 2 * clientIsland.getStudents().indexOf(student) * Math.PI / clientIsland.getStudents().size();
                double xOffset = STUDENTS_CIRCLE_RADIUS * Math.cos(angle);
                double yOffset = STUDENTS_CIRCLE_RADIUS * Math.sin(angle);
                double x = islandCenterX + xOffset ;
                double y = islandCenterY + yOffset ;
                ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
                islandToStudentsImages.get(island).add(studentImage);
                studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
                studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
                studentImage.setPreserveRatio(true);
                studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
                studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
                studentImage.setOnMouseClicked((MouseEvent e) -> { //questi dati vanno nell'action parser che li trasforma nell'arraylist di objects
                    handleClickEvent(0,Clickable.STUDENT);
                    //setSelectedStudent(clientIsland.getStudents().indexOf(student),clientIsland.getIslandIndex());
                    //System.out.println("Hai cliccato sullo studente "+selectedStudent+" dell'isola "+selectedIslandID);
                });
                gui.addElementToScene(studentImage);

            }
            islandCounter++;
        }
    }

    private ClientIsland getClientIslandFromImage(ArrayList<ClientIsland> islands,int islandCounter){
        Optional<ClientIsland> optIsland = islands.stream()
                .filter(clientIsland -> clientIsland.getIslandIndex() == islandCounter)
                .findFirst();

        return optIsland.orElse(null);
    }
    private void renderDeck(){
        if(shouldRenderDeck){
            deckImages = new ArrayList<>();

            HashMap<Integer,Integer> deck = gui.getDeck(); //k= priority, v = numMoves
            int deckCounter = 0;
            int startY = 11+(10-deck.size())*46;
            for(Integer priority : deck.keySet()){
                ImageView assistantImage = new ImageView("/graphics/assistant_"+priority+".png");
                deckImages.add(assistantImage);
                assistantImage.setX(26);
                assistantImage.setY(startY+deckCounter*46);
                assistantImage.setFitWidth(85);
                assistantImage.setFitHeight(125);
                assistantImage.setOnMouseClicked((MouseEvent e) -> {
                    handleClickEvent(priority,Clickable.ASSISTANT);
                    //setSelectedAssistant(priority);
                    //System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                });
                gui.addElementToScene(assistantImage);
                deckCounter++;
            }
            deckButton.toFront();

        }else{
            if(deckImages.size() > 0){
                for(ImageView image : deckImages){
                    System.out.println(image);
                    deckImages.remove(image);
                    gui.removeElementFromScene(image);
                }
            }
        }

    }

    private void handleClickEvent(int id,Clickable clickedElement){
        if(actionParser.canClick(gui.getCurrentState(),clickedElement)){
            switch(clickedElement){
                case ASSISTANT -> {
                    setSelectedAssistant(id);
                    System.out.println("Hai cliccato sulla carta "+selectedAssistant);
                }
            }
        }
        else
            System.out.println("Non puoi cliccare "+clickedElement+"se sei in "+gui.getCurrentState());
    }

    public void onWizardClick(){
        shouldRenderDeck =!shouldRenderDeck;
        renderDeck();
        System.out.println(shouldRenderDeck ? "Espando il deck" : "Chiudo il deck");
        System.out.println("Hai cliccato sul mago.");
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
