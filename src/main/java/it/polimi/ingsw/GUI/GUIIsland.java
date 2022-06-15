package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.model.Color;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
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
    private ImageView islandImage;
    private ArrayList<ImageView> students;
    private int index;
    private double centerX;
    private double centerY;
    private GameTableController controller;
    private GUI gui;
    private HashMap<Color,Long> numOfStudents;
    private HashMap<Color,Tooltip> tooltips;

//TODO sistemare range cerchio quando si aggiungono studenti lato client

    public GUIIsland(int index,double x,double y,double width,double height,GameTableController controller,GUI gui){
        islandImage = new ImageView("/graphics/island"+((index%3)+1)+".png");
        this.index = index;
        setPos(x,y);
        setSize(width,height);
        setCenter();
        this.controller = controller;
        this.gui = gui;
        students = new ArrayList<>();
        tooltips = new HashMap<>();
        numOfStudents = new HashMap<>();
        initializeNumOfStudents();
    }

    public void render(){
        gui.addElementToScene(islandImage);
        populate();
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

    public void setEvents(){
        islandImage.setOnMouseClicked((MouseEvent e) -> {
            controller.handleClickEvent(index,Clickable.ISLAND);
            //setSelectedIslandID(i);
            //System.out.println("Hai cliccato sull'isola "+selectedIslandID);
        });
        islandImage.setOnMouseEntered((MouseEvent e) -> {
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
                    ClientBoard clientBoard = gui.getOwningPlayerBoard();
                    int studentID = Integer.parseInt(db.getString());
                    Color student  = clientBoard.getLobby().get(studentID); //prendo il colore
                    addStudentToIsland(student);
                    success = true;
                    System.out.println("Drop sull'isola "+index);
                    controller.addSelectedStudent(studentID,index);
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
            setTooltip(student,studentImage);
        }else{
            populate();
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
    public void populate(){
        clearStudents();
        for(Color student : numOfStudents.keySet()){
            System.out.println("[PopulateIsland]Printiamo gli studenti "+student);
            double angle = 2 * student.getIndex() * Math.PI / Color.values().length;
            double xOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS * numOfStudents.keySet().size() * 2) * Math.cos(angle);
            double yOffset = (STUDENTS_ISLAND_CIRCLE_RADIUS * numOfStudents.keySet().size() * 2) * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
            studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
            studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
            studentImage.setPreserveRatio(true);
            studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
            studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
            //studentImage.setOnMouseClicked((MouseEvent e) -> {
              //  controller.handleClickEvent(clientIsland.getStudents().indexOf(student),Clickable.ISLAND_STUDENT); //come id passa il primo studente di quel colore che trova nell'isola
           // });
            students.add(studentImage);
            setTooltip(student,studentImage);
            System.out.println("[ISOLA] renderizzo uno studente");
            gui.addElementToScene(studentImage);
        }
    }

    private void clearStudents(){
        for(ImageView student : students){
            gui.removeElementFromScene(student);
        }
    }

    private void setTooltip(Color color,ImageView studentImage){
        Tooltip tooltip = tooltips.get(color);
        long num = numOfStudents.get(color);
        if(tooltip!=null){
            tooltip.setText(Long.toString(num));
            Tooltip.install(studentImage,tooltip);
        }else{
            Tooltip numOfStudents = new Tooltip(Long.toString(num));
            numOfStudents.setShowDelay(Duration.seconds(0.1));
            tooltips.put(color,numOfStudents);
            Tooltip.install(studentImage, numOfStudents);
        }
    }

}
