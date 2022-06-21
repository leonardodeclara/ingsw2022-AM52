package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.model.Color;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

import static it.polimi.ingsw.Constants.*;

//TODO: aggiungere label/effetto che mostri che una carta è attiva
//TODO: aggiungere tooltip per descrizione dell'effetto della carta
//TODO. aggiungere controllo (in controller/actionParser/qui che mi renda selezionabile solo gli studenti della carta attiva, non di tutte le carte)

public class GUIPersonality {
    private ClientPersonality personality;
    private ImageView cardImage;
    private int cardId;
    private ArrayList<ImageView> studentsImages;
    private ImageView banImage;
    private ImageView coinImage;
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

        if (personality.isHasBeenUsed()){
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
        if (personality.getStudents()!=null && personality.getStudents().size()>0){
            System.out.println("aggiungo immagini degli studenti alla carta "+cardId);
            populateLobbyPersonality();
        }
        else if (personality.getBans()>0){
            System.out.println("aggiungo immagini dei ban alla carta " +cardId);
            populateBanPersonality();
        }
    }

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
            System.out.println("Aggiunto studente "+student+" alla carta lobby "+cardId);
            studentsImages.add(studentImage);
            setStudentsEvents(studentImage, i);
        }
    }

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

    private void initializeExtraFeatures(){
        Tooltip descriptionToolTip = new Tooltip(personality.getDescription());
        descriptionToolTip.setShowDelay(Duration.seconds(0.3));
        descriptionToolTip.setHideDelay(Duration.seconds(20));
        descriptionToolTip.setFont(gui.getGameFont());
        Tooltip.install(cardImage, descriptionToolTip);
    }

    private void clearExtraFeatures(){
        if (personality.getStudents()!=null && personality.getStudents().size()>0)
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
            actionParser.handleSelectionEvent(cardId,Clickable.PERSONALITY,gui.getCurrentState());
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

    public void setStudentsEvents(ImageView studentImage, int studentIndex){
        studentImage.setOnMouseClicked((MouseEvent e) -> {
            //if (personality.isActive) aggiungere controllo per cui si può clickare su uno studente se la carta è attiva
            //o qui o in controller o actionParser
            actionParser.handleSelectionEvent(studentIndex,Clickable.PERSONALITY_CARD_STUDENT, gui.getCurrentState());
            controller.handleSelectionEffect(studentImage,Clickable.PERSONALITY_CARD_STUDENT);
        });
        studentImage.setOnDragDetected((MouseEvent e) -> {
            if(controller.getActionParser().canDrag(gui.getCurrentState(),Clickable.PERSONALITY_CARD_STUDENT)){
                Dragboard db = studentImage.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(studentImage.getImage());
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(studentIndex));
                db.setContent(content);
                System.out.println("Inizio il drag event per "+studentIndex);
                e.consume();

            }
        });
        studentImage.setOnDragDone(event -> {
            System.out.println("Drag completato, tolgo lo studente dalla carta");
            if (event.getTransferMode() == TransferMode.MOVE) {
                studentsImages.remove(studentImage);
                gui.removeElementFromScene(studentImage);
            }
            event.consume();
        });
    }

    public void setImageEffect(Effect effect) {
        cardImage.setEffect(effect);
    }
}
