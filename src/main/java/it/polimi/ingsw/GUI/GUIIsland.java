package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.CLI.ClientPersonality;
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

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static it.polimi.ingsw.Constants.*;

public class GUIIsland{
    private ClientIsland clientIsland;
    private ImageView islandImage;
    private ArrayList<ImageView> students;
    private ImageView motherNature;
    private ImageView tower;
    private int index;
    private double centerX;
    private double centerY;
    private double angle;
    private GameTableController controller;
    private ActionParser actionParser;
    private GUI gui;
    private HashMap<Color,Long> numOfStudents;
    private HashMap<Color,Tooltip> tooltips;

//TODO sistemare range cerchio quando si aggiungono studenti lato client
//TODO sistemare posizioni di madre natura e torri
//TODO aggiungere rendering ban

    public GUIIsland(int index,double x,double y,double width,double height,double angle,GameTableController controller,GUI gui){
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

    public void render(){
        gui.addElementToScene(islandImage);
        numOfStudents.clear();
        initializeNumOfStudents();
        populateStudents();
        populateMotherNature();
        populateTowers();
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

    public void clearIsland(){ //cancella l'isola
        clearStudents();
        clearTowers();
        clearMotherNature();
        gui.removeElementFromScene(islandImage);
    }
    public void setEvents(){
        islandImage.setOnMouseClicked((MouseEvent e) -> {
            //controller.handleClickEvent(index,Clickable.ISLAND);
            if (gui.getCurrentState().equals(ClientState.MOVE_MOTHER_NATURE)){
                System.out.println("Sono in MOVE MN, non devo passare l'indice dell'isola ma il numero di passi");
                actionParser.handleSelectionEvent(controller.extractMNsteps(index),Clickable.ISLAND,gui.getCurrentState());
            }
            else actionParser.handleSelectionEvent(index,Clickable.ISLAND,gui.getCurrentState());
            controller.handleSelectionEffect(islandImage,Clickable.ISLAND);
            //setSelectedIslandID(i);
            //System.out.println("Hai cliccato sull'isola "+selectedIslandID);
        });
        islandImage.setOnMouseEntered((MouseEvent e) -> {
            if (!clientIsland.isMotherNature())
                controller.handleHoverEvent(islandImage, Clickable.ISLAND);
        });
        islandImage.setOnMouseExited((MouseEvent e) -> {
            if (islandImage.getEffect()==null || !(DropShadow.class).equals(islandImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                islandImage.setEffect(null);
        });

        islandImage.setOnDragOver((DragEvent e) -> { //qui si avrà il check dell'action parser (se non è clickable questo evento non deve partire)
            if (e.getGestureSource() != islandImage && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });
        islandImage.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) { //ci sarà da ottimizzare tutto e pulirlo sfruttando data structures apposite
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) { //per generalizzarlo avremo una variabile draggedElement così che qui sappiamo cosa sta venendo rilasciato
                    //per ora creiamo solo il caso specifico di move from lobby
                    int studentID = Integer.parseInt(db.getString());
                    Color student;
                    //rivedere se si può generalizzare rispetto allo stato
                    if (gui.getCurrentState().equals(ClientState.MOVE_FROM_LOBBY)){
                        ClientBoard clientBoard = gui.getOwningPlayerBoard();
                        student = clientBoard.getLobby().get(studentID); //prendo il colore
                    }
                    else{
                        ClientPersonality activeCard = gui.getGB().getActivePersonality();
                        student = activeCard.getStudents().get(studentID);
                    }
                    ArrayList<Object> selection = new ArrayList<>();
                    selection.add(studentID);
                    selection.add(index);
                    //MANCA CONTROLLO CAN CLICK-CAN DRAG
                    actionParser.handleSelectionEvent(selection, gui.getCurrentState());
                    //controller.addSelectedCardStudent(studentID,index);
                    addStudentToIsland(student);
                    success = true;
                    System.out.println("Drop sull'isola "+index);

                }
                event.setDropCompleted(success);

                event.consume();
            }
        });

    }

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

    private void clearStudents(){
        for(ImageView student : students){
            gui.removeElementFromScene(student);
        }
    }

    public void populateMotherNature(){
        double screenCenterX = gui.getScreenX()/2;
        double screenCenterY = gui.getScreenY()/2 - 15;

        clearMotherNature();
        if (clientIsland.isMotherNature()){ //alziamo le carte personaggio di un tot e mettiamo mother nature in una traiettoria circolare in corrispondenza delle isole (radius più piccolo, quindi madre natura è dentro il cerchio)
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

    private void clearMotherNature(){
        gui.removeElementFromScene(motherNature);
        motherNature=null;
    }

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

    private void clearTowers(){
        gui.removeElementFromScene(tower);
        tower=null;
    }

    private void setStudentsTooltip(Color color,ImageView studentImage){
        Tooltip tooltip = tooltips.get(color);
        long num = numOfStudents.get(color);
        if(tooltip!=null){
            tooltip.setText("Ci sono: "+ num +" studenti "+color.translateToItalian().toLowerCase());
            Tooltip.install(studentImage,tooltip);
        }else{
            Tooltip numOfStudents = new Tooltip("Ci sono: "+Long.toString(num)+" studenti "+color.translateToItalian().toLowerCase());
            numOfStudents.setShowDelay(Duration.seconds(0.1));
            tooltips.put(color,numOfStudents);
            Tooltip.install(studentImage, numOfStudents);
        }
    }

    //soluzione temporanea, dipende da come gestiamo la renderizzazione delle torri con i merge
    private void setTowersTooltip(){
        int towerNumber = clientIsland.getTowers().size();
        Tower towerType = clientIsland.getTowers().get(0);
        Tooltip towersMessage = new Tooltip("Ci sono "+towerNumber+" torri "+towerType.getTranslation().toLowerCase());
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
