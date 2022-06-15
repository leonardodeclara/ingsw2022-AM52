package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.model.Color;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.*;

//TODO: aggiungere effetti on mouse exit ecc
//TODO: aggiungere tooltip per descrizione dell'effetto della carta

public class GUIPersonality {
    private ImageView cardImage;
    private int cardId;
    private ArrayList<ImageView> studentsImages;
    private ImageView banImage;
    private ImageView coinImage;
    private ArrayList<Color> students;
    private double centerX;
    private double centerY;
    private GameTableController controller;
    private GUI gui;

    public GUIPersonality(int cardId,double x,double y,double width,double height,GameTableController controller,GUI gui){
        cardImage = new ImageView("/graphics/personality_"+cardId+".jpg");
        this.cardId = cardId;
        setPos(x,y);
        setSize(width,height);
        setCenter();
        this.controller = controller;
        this.gui = gui;
        studentsImages = new ArrayList<>();
        initializeExtraFeatures();
    }

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

    private void populate(){
        clearExtraFeatures();
        ClientPersonality card = gui.getGB().getPersonalityById(cardId);
        if (card.isHasBeenUsed()){
            coinImage = new ImageView("graphics/coin.png");
            //rivedere dove posizionarla
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
        if (card.getStudents()!=null && card.getStudents().size()>0){
            System.out.println("aggiungo immagini degli studenti alla carta "+cardId);
            populateLobbyPersonality(card);
        }
        else if (card.getBans()!=0){
            System.out.println("aggiungo immagini dei ban alla carta " +cardId);
            populateBanPersonality(card);
        }
    }

    private void populateLobbyPersonality(ClientPersonality personality){
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
            studentImage.setOnMouseClicked((MouseEvent e) -> {
                controller.handleClickEvent(personality.getStudents().indexOf(student),Clickable.PERSONALITY_STUDENT); //come id passa il primo studente di quel colore che trova nell'isola
            });
            gui.addElementToScene(studentImage);
            studentImage.toFront();
            System.out.println("Aggiunto studente "+student+" alla carta lobby "+cardId);
            studentsImages.add(studentImage);
        }
    }

    private void populateBanPersonality(ClientPersonality personality){
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

    private void initializeExtraFeatures(){
        ClientPersonality personality = gui.getGB().getPersonalityById(cardId);
        if (students!=null && students.size()>0)
            students=personality.getStudents();
    }

    private void clearExtraFeatures(){
        if (students!=null && students.size()>0)
            for(ImageView studentImage : studentsImages){
                gui.removeElementFromScene(studentImage);
            }
        if (coinImage!=null)
            gui.removeElementFromScene(coinImage);
        if (banImage!=null)
            gui.removeElementFromScene(banImage);
    }

    public void setEvents(){
        cardImage.setOnMouseClicked((MouseEvent e) ->{
            controller.handleClickEvent(cardId,Clickable.PERSONALITY);
            controller.handleSelectionEffect(cardImage,Clickable.PERSONALITY);
        });
        cardImage.setOnMouseEntered((MouseEvent e) -> {
            controller.handleHoverEvent(cardImage, Clickable.PERSONALITY);
        });
        cardImage.setOnMouseExited((MouseEvent e) -> {
            if (cardImage.getEffect()==null || !(DropShadow.class).equals(cardImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                cardImage.setEffect(null);
        });
    }

    public void setImageEffect(Effect effect) {
        cardImage.setEffect(effect);
    }
}
