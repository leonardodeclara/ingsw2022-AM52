package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.model.Color;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import static it.polimi.ingsw.Constants.*;

//TODO rendere dinamico il radius e la dimensione degli elementi in funzione della lunghezza degli array (fondamentale per gli studenti)
public class GameTableController extends GUIController implements UpdatableController{
    int selectedIslandID = -1;
    int selectedCloud = -1;
    int selectedStudent = -1; //relative to island
    ArrayList<ImageView> islandsImages;
    ArrayList<ImageView> cloudsImages;
    HashMap<ImageView,ArrayList<ImageView>> islandToStudentsImages;
    double centerX = 0;
    double centerY = 0;
    boolean waitTurn = false;

    public void start(){
        centerX = gui.getScreenCenterX();
        centerY = gui.getScreenCenterY();
    }

    @Override
    public void setWaitTurn(boolean value) {
        waitTurn = value;
    }


    @Override
    public void update() {
        renderIslands();
        renderClouds();
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
                setSelectedIslandID(i);
                System.out.println("Hai cliccato sull'isola "+selectedIslandID);
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
                setSelectedCloud(i);
                System.out.println("Hai cliccato sulla nuvola "+selectedCloud);
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
                    setSelectedStudent(clientIsland.getStudents().indexOf(student),clientIsland.getIslandIndex());
                    System.out.println("Hai cliccato sullo studente "+selectedStudent+" dell'isola "+selectedIslandID);
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
}
