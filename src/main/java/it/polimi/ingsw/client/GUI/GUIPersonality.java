package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.CLI.ClientPersonality;
import it.polimi.ingsw.client.GUI.GUIControllers.GameTableController;
import it.polimi.ingsw.model.Color;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.*;

/**
 * Class GUIPersonality renders the card image and elements on the screen
 * For each card, a GUIPersonality instance is created
 */
public class GUIPersonality {
    private ClientPersonality personality;
    private ImageView cardImage;
    private int cardId;
    private ArrayList<ImageView> studentsImages;
    private ImageView banImage;
    private ImageView coinImage;
    private Text activeText;
    private double centerX;
    private double centerY;
    private GameTableController controller;
    private GUI gui;
    private ActionParser actionParser;

    public GUIPersonality(int cardId,double x,double y,double width,double height,
                          GameTableController controller,GUI gui,ClientPersonality personality){
        cardImage = new ImageView("/graphics/personality_"+cardId+".jpg");
        this.cardId = cardId;
        setPos(x,y);
        setSize(width,height);
        setCenter();
        this.controller = controller;
        this.actionParser= controller.getActionParser();
        this.gui = gui;
        this.personality=personality;
        studentsImages = new ArrayList<>();
        initializeExtraFeatures();
    }

    /**
     * Method render renders the card image
     * and adds all the various items images on it
     */
    public void render(){
        gui.addElementToScene(cardImage);
        populate();
    }
    public void setPos(double x,double y){
        cardImage.setX(x);
        cardImage.setY(y);
    }

    public void setSize(double width, double height){
        cardImage.setFitHeight(height);
        cardImage.setFitWidth(width);
        cardImage.setPreserveRatio(true);
    }

    private void setCenter(){
        if(cardImage!=null){
            centerX = cardImage.getX()+PERSONALITY_IMAGE_WIDTH/2;
            centerY = cardImage.getY()+PERSONALITY_IMAGE_HEIGHT/2;
        }
    }

    /**
     * Method populate renders the items images on the card
     * This includes students,coins,bans and ACTIVE label
     */
    private void populate(){
        clearFeatures();

        if (personality.isHasBeenUsed()){
            coinImage = new ImageView("graphics/coin.png");
            double coinX = cardImage.getX()+PERSONALITY_IMAGE_WIDTH/2;
            double coinY = cardImage.getY()+PERSONALITY_IMAGE_HEIGHT/2;
            coinImage.setX(coinX-COIN_IMAGE_WIDTH/2);
            coinImage.setY(coinY-COIN_IMAGE_HEIGHT/2);
            coinImage.setFitWidth(COIN_IMAGE_WIDTH);
            coinImage.setFitHeight(COIN_IMAGE_HEIGHT);
            coinImage.setPreserveRatio(true);
            gui.addElementToScene(coinImage);
            coinImage.toFront();
        }
        if (personality.getStudents()!=null && personality.getStudents().size()>0){
            populateLobbyPersonality();
        }
        else if (personality.getBans()>0){
            populateBanPersonality();
        }
        if (personality.isActive()){
            activeText = new Text("ACTIVE");
            activeText.setX(centerX-PERSONALITY_IMAGE_WIDTH/2);
            activeText.setY(centerY+PERSONALITY_IMAGE_HEIGHT/2);
            activeText.setFont(gui.getGameFont());
            activeText.setFill(javafx.scene.paint.Color.BLACK);
            gui.addElementToScene(activeText);
            activeText.toFront();
        }
    }

    /**
     * Method populateLobbyPersonality renders student images on the card
     */
    private void populateLobbyPersonality(){
        ArrayList<Color> cardStudents = personality.getStudents();
        int halfAmountOfStudents=cardStudents.size()/2;
        double offsetX=STUDENT_IMAGE_WIDTH*2;
        double offsetY;
        double startY = centerY +PERSONALITY_IMAGE_HEIGHT*0.2;
        double startX = centerX-STUDENT_IMAGE_WIDTH*1.5;
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
            gui.addElementToScene(studentImage);
            studentImage.toFront();
            studentsImages.add(studentImage);
            setStudentsEvents(studentImage, i);
        }
    }

    /**
     * Method populateBanPersonality renders ban images on the card
     */
    private void populateBanPersonality(){
        double startY = centerY+PERSONALITY_IMAGE_HEIGHT*0.15;
        double startX = centerX;
        int banCount = personality.getBans();
        banImage = new ImageView("/graphics/deny_island_icon.png");
        banImage.setX(startX-BAN_IMAGE_WIDTH/2);
        banImage.setY(startY);
        banImage.setPreserveRatio(true);
        banImage.setFitHeight(BAN_IMAGE_HEIGHT);
        banImage.setFitWidth(BAN_IMAGE_WIDTH);
        Tooltip numOfBanTiles = new Tooltip(("AVAILABLE BANS: "+banCount));
        numOfBanTiles.setShowDelay(Duration.seconds(0.3));
        Tooltip.install(banImage, numOfBanTiles);
        gui.addElementToScene(banImage);
        banImage.toFront();
    }

    /**
     * Method initializeExtraFeatures adds the card description
     * on a tooltip
     */
    private void initializeExtraFeatures(){
        Tooltip descriptionToolTip = new Tooltip(personality.getDescription());
        descriptionToolTip.setShowDelay(Duration.seconds(0.3));
        descriptionToolTip.setHideDelay(Duration.seconds(8));
        descriptionToolTip.setFont(gui.getGameFont());
        Tooltip.install(cardImage, descriptionToolTip);
    }

    /**
     * Method clearFeatures removes all the images on the card
     * from the screen
     */
    private void clearFeatures(){
        if (personality.getStudents()!=null && personality.getStudents().size()>0)
            for(ImageView studentImage : studentsImages){
                gui.removeElementFromScene(studentImage);}
        gui.removeElementFromScene(coinImage);
        gui.removeElementFromScene(banImage);
        gui.removeElementFromScene(activeText);
    }

    /**
     * Method setEvents sets up all the mouse events
     * for the card image
     */
    public void setEvents(){
        cardImage.setOnMouseClicked((MouseEvent e) ->{
            actionParser.handleSelectionEvent(cardId,Clickable.PERSONALITY,gui.getCurrentState());
            controller.handleSelectionEffect(cardImage,Clickable.PERSONALITY);
        });
        cardImage.setOnMouseEntered((MouseEvent e) -> {
            controller.handleHoverEvent(cardImage, Clickable.PERSONALITY);
        });
        cardImage.setOnMouseExited((MouseEvent e) -> {
            if (cardImage.getEffect()==null || !(DropShadow.class).equals(cardImage.getEffect().getClass()))
                cardImage.setEffect(null);
        });
    }

    /**
     * Method setStudentsEvents sets up all the mouse/Drag and drop events
     * for the student images
     */
    public void setStudentsEvents(ImageView studentImage, int studentIndex){
        studentImage.setOnMouseClicked((MouseEvent e) -> {
            if (personality.isActive()){
                actionParser.handleSelectionEvent(studentIndex,Clickable.PERSONALITY_CARD_STUDENT, gui.getCurrentState());
                controller.handleSelectionEffect(studentImage,Clickable.PERSONALITY_CARD_STUDENT);}
        });
        studentImage.setOnDragDetected((MouseEvent e) -> {
            if(controller.getActionParser().canDrag(gui.getCurrentState(),Clickable.PERSONALITY_CARD_STUDENT)){
                Dragboard db = studentImage.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(studentImage.getImage());
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(studentIndex));
                db.setContent(content);
                e.consume();

            }
        });
        studentImage.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                studentsImages.remove(studentImage);
                gui.removeElementFromScene(studentImage);
            }
            event.consume();
        });
    }


    /**
     * Method clearPersonality removes up all the images associated
     * with this card from the screen
     */
    public void clearPersonality(){
        clearFeatures();
        gui.removeElementFromScene(cardImage);
    }

    public void setImageEffect(Effect effect) {
        cardImage.setEffect(effect);
    }
}
