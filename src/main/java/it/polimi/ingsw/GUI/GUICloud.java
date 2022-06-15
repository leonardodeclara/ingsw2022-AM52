package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.model.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.*;


public class GUICloud {
    private ImageView cloudImage;
    private ArrayList<ImageView> studentsImages;
    private ArrayList<Color> students;
    private int index;
    private double centerX;
    private double centerY;
    private GameTableController controller;
    private GUI gui;

    public GUICloud(int index,double x,double y,double width,double height,GameTableController controller,GUI gui){
        cloudImage = new ImageView("/graphics/cloud"+((index%3)+1)+".png");
        this.index = index;
        setPos(x,y);
        setSize(width,height);
        setCenter();
        this.controller = controller;
        this.gui = gui;
        initializeStudents();
        studentsImages = new ArrayList<>();
    }

    public void render(){
        gui.addElementToScene(cloudImage);
        populate();
    }

    public void setPos(double x,double y){
        cloudImage.setX(x);
        cloudImage.setY(y);
    }

    public void setSize(double width, double height){
        cloudImage.setFitHeight(height);
        cloudImage.setFitWidth(width);
        cloudImage.setPreserveRatio(true);
    }

    private void setCenter(){
        if(cloudImage!=null){
            centerX = cloudImage.getX()+CLOUD_IMAGE_WIDTH/2;
            centerY = cloudImage.getY()+CLOUD_IMAGE_HEIGHT/2;
        }
    }

    private void populate(){
        clearStudentsImages();

        int studentCounter = 0; //va tenuta traccia manualmente, indexOf trovava la prima occurence
        for(Color student : students){ //li printiamo a cerchio invece che a matrice così sfruttiamo meglio lo spazio (esteticamente parlando)
            double angle = 2 * studentCounter * Math.PI / students.size();
            double xOffset = STUDENTS_CLOUD_CIRCLE_RADIUS * Math.cos(angle);
            double yOffset = STUDENTS_CLOUD_CIRCLE_RADIUS * Math.sin(angle);
            double x = centerX + xOffset ;
            double y = centerY + yOffset ;
            ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
            System.out.println(studentsImages.size());
            studentImage.setX(x-STUDENT_IMAGE_WIDTH/2);
            studentImage.setY(y-STUDENT_IMAGE_HEIGHT/2);
            studentImage.setPreserveRatio(true);
            studentImage.setFitHeight(STUDENT_IMAGE_HEIGHT);
            studentImage.setFitWidth(STUDENT_IMAGE_WIDTH);
            studentsImages.add(studentImage);
            gui.addElementToScene(studentImage);
            System.out.println("CLOUD: renderizzo uno studente");
            studentCounter++;
        }
    }

    public void setEvents(){
        cloudImage.setOnMouseClicked((MouseEvent e) -> {
            controller.handleClickEvent(index,Clickable.CLOUD_STUDENT);
        });
        cloudImage.setOnMouseEntered((MouseEvent e) -> {
            controller.handleHoverEvent(cloudImage, Clickable.CLOUD);
        });
        cloudImage.setOnMouseExited((MouseEvent e) -> {
            if (cloudImage.getEffect()==null || !(DropShadow.class).equals(cloudImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                cloudImage.setEffect(null);
        });

    }

    private void initializeStudents(){
        ClientCloud clientCloud= gui.getGB().getCloudByIndex(index);
        students = clientCloud.getStudents();
    }

    private void clearStudentsImages(){
        for(ImageView student : studentsImages){
            gui.removeElementFromScene(student);
        }
    }


}
