package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.*;

public class GameTableController extends GUIController implements UpdatableController{
    int selectedIslandID = -1;
    int selectedCloud = -1;
    ArrayList<ImageView> islandsImages;
    ArrayList<ImageView> cloudsImages;
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
            double xOffset = ISLAND_CIRCLE_RADIUS * Math.cos(angle);
            double yOffset = ISLAND_CIRCLE_RADIUS * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            ImageView islandImage = new ImageView("/graphics/island"+((i%3)+1)+".png");
            islandsImages.add(islandImage);
            islandImage.setX(x-ISLAND_IMAGE_WIDTH/2);
            islandImage.setY(y-ISLAND_IMAGE_HEIGHT/2);
            islandImage.setFitHeight(ISLAND_IMAGE_HEIGHT);
            islandImage.setFitWidth(ISLAND_IMAGE_WIDTH);
            islandImage.setOnMouseClicked((MouseEvent e) -> {
                setSelectedIslandID(i);
                System.out.println("Hai cliccato sull'isola "+selectedIslandID);
            });
            gui.addElementToScene(islandImage);
        }
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
            cloudsImages.add(cloudImage);
            cloudImage.setX(x-CLOUD_IMAGE_WIDTH/2);
            cloudImage.setY(y-CLOUD_IMAGE_HEIGHT/2);
            cloudImage.setPreserveRatio(true);
            cloudImage.setFitHeight(CLOUD_IMAGE_HEIGHT);
            cloudImage.setFitWidth(CLOUD_IMAGE_WIDTH);
            cloudImage.setOnMouseClicked((MouseEvent e) -> {
                setSelectedCloud(i);
                System.out.println("Hai cliccato sulla nuvola "+selectedCloud);
            });
            gui.addElementToScene(cloudImage);
        }
    }

    private void renderDeck(){

    }



    public void setSelectedIslandID(int id){
        selectedIslandID = id;
    }

    public void setSelectedCloud(int id){
        selectedCloud = id;
    }
}
