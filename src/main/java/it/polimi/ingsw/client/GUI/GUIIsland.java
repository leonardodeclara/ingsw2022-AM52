package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.ClientPersonality;
import it.polimi.ingsw.client.GUI.GUIControllers.GameTableController;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static it.polimi.ingsw.Constants.*;

/**
 * Class GUIIsland renders the island's elements on the screen
 * For each island, a GUIIsland instance is created
 */
public class GUIIsland{
    private ClientIsland clientIsland;
    private ImageView islandImage;
    private ArrayList<ImageView> students;
    private ImageView motherNature;
    private ImageView tower;
    private ImageView banImage;
    private int index;
    private double centerX;
    private double centerY;
    private double angle;
    private GameTableController controller;
    private ActionParser actionParser;
    private GUI gui;
    private HashMap<Color,Long> numOfStudents;
    private HashMap<Color,Tooltip> tooltips;


    public GUIIsland(int index,double x,double y,double width,double height,double angle,
                     GameTableController controller,GUI gui){
        islandImage = new ImageView("/graphics/island"+((index%3)+1)+".png");
        this.index = index;
        setPos(x,y);
        setSize(width,height);
        setCenter();
        this.controller = controller;
        this.actionParser=controller.getActionParser();
        this.gui = gui;
        this.angle = angle;
        students = new ArrayList<>();
        tooltips = new HashMap<>();
        numOfStudents = new HashMap<>();
        initializeNumOfStudents();
    }

    /**
     * Method render renders the island image
     * and populates it with students,towers and mother nature
     * If the game is in expert mode, personality bans could also be added
     */
    public void render(){
        gui.addElementToScene(islandImage);
        numOfStudents.clear();
        initializeNumOfStudents();
        populateStudents();
        populateMotherNature();
        populateTowers();
        if (clientIsland.getBans()>0)
            populateBans();
    }
    public void setPos(double x,double y){
        islandImage.setX(x);
        islandImage.setY(y);
    }

    public void setSize(double width, double height){
        islandImage.setFitHeight(height);
        islandImage.setFitWidth(width);
        islandImage.setPreserveRatio(true);
    }

    private void setCenter(){
        if(islandImage!=null){
            centerX = islandImage.getX()+ISLAND_IMAGE_WIDTH/2;
            centerY = islandImage.getY()+ISLAND_IMAGE_HEIGHT/2;
        }
    }

    /**
     * Method clearIsland removes all the images
     * from the island
     */
    public void clearIsland(){
        clearStudents();
        clearTowers();
        clearMotherNature();
        gui.removeElementFromScene(islandImage);
    }

    /**
     * Method setEvents sets up all the mouse/drag and drop events
     * for the island image. If a student is dropped on the island,
     * this will be rendered on it.
     * If a student image of the same color has already been rendered, then
     * no image will be added but the corresponding tooltip will be updated
     */
    public void setEvents(){
        islandImage.setOnMouseClicked((MouseEvent e) -> {
            if (gui.getCurrentState().equals(ClientState.MOVE_MOTHER_NATURE)){
                actionParser.handleSelectionEvent(controller.extractMNsteps(index),Clickable.ISLAND,gui.getCurrentState());
            }
            else actionParser.handleSelectionEvent(index,Clickable.ISLAND,gui.getCurrentState());
            controller.handleSelectionEffect(islandImage,Clickable.ISLAND);

        });
        islandImage.setOnMouseEntered((MouseEvent e) -> {
            if (!clientIsland.isMotherNature() || !(gui.getCurrentState().equals(ClientState.MOVE_MOTHER_NATURE)))
                controller.handleHoverEvent(islandImage, Clickable.ISLAND);
        });
        islandImage.setOnMouseExited((MouseEvent e) -> {
            if (islandImage.getEffect()==null || !(DropShadow.class).equals(islandImage.getEffect().getClass()))
                islandImage.setEffect(null);
        });

        islandImage.setOnDragOver((DragEvent e) -> {
            if (e.getGestureSource() != islandImage && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });
        islandImage.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int selectedStudentID = Integer.parseInt(db.getString());
                    Color student;
                    if (gui.getCurrentState().equals(ClientState.MOVE_FROM_LOBBY)){
                        ClientBoard clientBoard = gui.getOwningPlayerBoard();
                        student = clientBoard.getLobby().get(selectedStudentID);
                    }
                    else{
                        ClientPersonality activeCard = gui.getGB().getActivePersonality();
                        student = activeCard.getStudents().get(selectedStudentID);
                    }
                    ArrayList<Object> selection = new ArrayList<>();
                    selection.add(selectedStudentID);
                    selection.add(index);
                    actionParser.handleSelectionEvent(selection, gui.getCurrentState());
                    addStudentToIsland(student);
                    success = true;

                }
                event.setDropCompleted(success);

                event.consume();
            }
        });

    }

    /**
     * Method addStudentToIsland adds to the hashmap color->number of students
     * a new student, given by the parameter.
     * Finally populateStudents() is called, to refresh student images and tooltips
     * @param student is the color to add to the hashmap
     */
    private void addStudentToIsland(Color student){
        ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
        long numberOfStudents = numOfStudents.containsKey(student) ? numOfStudents.get(student) : 0;
        numOfStudents.put(student,numberOfStudents+1);
        if(numberOfStudents > 1){
            setStudentsTooltip(student,studentImage);
        }else{
            populateStudents();
        }

    }

    /**
     * Method initializeNumOfStudents sets up the hashmap which associates Color->number of students
     * according to the GameBoard current status.
     * This data structure can be modified freely on client side by drag n drop events,
     * because the copy from server is stored in GameBoard instance
     */
    private void initializeNumOfStudents(){
        ClientIsland clientIsland = gui.getGB().getIslandByIndex(index);
        List<Color> distinctStudents = clientIsland.getStudents().stream().distinct().toList();
        for(Color student : distinctStudents){
            long numberOfStudents = clientIsland.getStudents()
                    .stream()
                    .filter(color -> color.equals(student)).count();
            numOfStudents.put(student,numberOfStudents);
        }

    }

    /**
     * Method populateStudents adds all the students images to the island
     * accordingly to the client-side hashmap.
     * If a drag n drop event has added one or more students
     * to the island, they will be
     * rendered even if these are not on the island, server-side.
     * This is done to preview what the island will look like
     * if the player decides to confirm his action
     */
    public void populateStudents(){
        clearStudents();
        int colorCounter = 0;
        for(Color student : Color.values()){
            if(numOfStudents.containsKey(student)){
                double angle = 2 * colorCounter * Math.PI / Color.values().length;
                double xOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS) * Math.cos(angle);
                double yOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS) * Math.sin(angle);
                double x = centerX + xOffset ;
                double y = centerY + yOffset ;
                ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
                studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
                studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
                studentImage.setPreserveRatio(true);
                studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
                studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
                students.add(studentImage);
                setStudentsTooltip(student,studentImage);
                gui.addElementToScene(studentImage);
            }
            colorCounter++;
        }
    }

    /**
     * Method clearLobby removes all the student images
     * from the screen
     */
    private void clearStudents(){
        for(ImageView student : students){
            gui.removeElementFromScene(student);
        }
    }

    /**
     * Method populateMotherNature adds mother nature image to the island
     * This will be displayed if and only if this island has mother nature on it
     */
    public void populateMotherNature(){
        double screenCenterX = gui.getScreenX()/2;
        double screenCenterY = gui.getScreenY()/2 - 15;

        clearMotherNature();
        if (clientIsland.isMotherNature()){
            ImageView motherNatureImage = new ImageView("/graphics/MotherNature_Overlay.png");
            motherNatureImage.setX(islandImage.getX()+MOTHER_NATURE_OFFSET_X);
            motherNatureImage.setY(islandImage.getY()+MOTHER_NATURE_OFFSET_Y);
            motherNatureImage.setRotate(180);
            motherNatureImage.setFitWidth(MOTHER_NATURE_WIDTH);
            motherNatureImage.setFitHeight(MOTHER_NATURE_HEIGHT);
            motherNatureImage.setPreserveRatio(true);
            motherNatureImage.toFront();
            motherNature=motherNatureImage;
            gui.addElementToScene(motherNatureImage);
        }
    }

    /**
     * Method clearMotherNature removes mother nature image
     * from the screen
     */
    private void clearMotherNature(){
        gui.removeElementFromScene(motherNature);
        motherNature=null;
    }

    /**
     * Method populateTowers adds tower images to the island
     */
    public void populateTowers(){
        clearTowers();
        if (clientIsland.getTowers()!=null && clientIsland.getTowers().size()>0){
            String towerColor= clientIsland.getTowers().get(0).toString().toLowerCase();
            ImageView towerImage = new ImageView("/graphics/"+towerColor+"_board_tower.png");
            towerImage.setX(centerX-TOWER_IMAGE_WIDTH/3);
            towerImage.setY(centerY-TOWER_IMAGE_HEIGHT/3);
            towerImage.setFitWidth(TOWER_IMAGE_WIDTH/1.5);
            towerImage.setFitHeight(TOWER_IMAGE_HEIGHT/1.5);
            towerImage.setPreserveRatio(true);
            towerImage.toFront();
            tower=towerImage;
            setTowersTooltip();
            gui.addElementToScene(tower);
        }
    }

    /**
     * Method populateBans adds personality ban images to the island
     */
    public void populateBans(){
        clearBans();
        int banCount = clientIsland.getBans();
        double startX = centerX;
        double startY = centerY+ISLAND_IMAGE_HEIGHT*0.15;
        banImage = new ImageView("/graphics/deny_island_icon.png");
        banImage.setX(startX-BAN_IMAGE_WIDTH/2);
        banImage.setY(startY);
        banImage.setPreserveRatio(true);
        banImage.setFitHeight(BAN_IMAGE_HEIGHT);
        banImage.setFitWidth(BAN_IMAGE_WIDTH);
        Tooltip numOfBanTiles = new Tooltip(("BANS PLACED: "+banCount));
        numOfBanTiles.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(banImage, numOfBanTiles);
        gui.addElementToScene(banImage);
        banImage.toFront();
    }

    /**
     * Method clearBans removes personality ban images from the island
     */
    private void clearBans() {
        gui.removeElementFromScene(banImage);
        banImage =null;
    }
    /**
     * Method clearTowers removes tower images from the island
     */
    private void clearTowers(){
        gui.removeElementFromScene(tower);
        tower=null;
    }

    /**
     * Method setStudentsTooltip sets up/updates tooltip installed
     * on a student image
     * @param color is the color of the students associated with the tooltip to update
     * @param studentImage is the student image reference on which the tooltip is installed
     */
    private void setStudentsTooltip(Color color,ImageView studentImage){
        Tooltip tooltip = tooltips.get(color);
        long num = numOfStudents.get(color);
        if(tooltip!=null){
            tooltip.setText("There are: "+ num +" "+color.toString().toLowerCase()+" students");
            Tooltip.install(studentImage,tooltip);
        }else{
            Tooltip numOfStudents = new Tooltip("There are: "+Long.toString(num)+" "+color.toString().toLowerCase()+" students");
            numOfStudents.setShowDelay(Duration.seconds(0.1));
            tooltips.put(color,numOfStudents);
            Tooltip.install(studentImage, numOfStudents);
        }
    }

    /**
     * Method setTowersTooltip sets up/updates tooltip installed
     * on the tower image
     */
    private void setTowersTooltip(){
        int towerNumber = clientIsland.getTowers().size();
        Tower towerType = clientIsland.getTowers().get(0);
        Tooltip towersMessage = new Tooltip("There are "+towerNumber+" "+towerType.toString().toLowerCase()+" towers");
        towersMessage.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(tower,towersMessage);
    }

    public void setImageEffect(Effect effect){
        islandImage.setEffect(effect);
    }

    public void setClientIsland(ClientIsland clientIsland){
        this.clientIsland=clientIsland;
    }

}
