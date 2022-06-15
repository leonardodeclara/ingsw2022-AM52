package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.model.Color;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static it.polimi.ingsw.Constants.*;

public class GUIBoard {
    private ClientBoard clientBoard;
    private GUI gui;
    private GameTableController controller;
    private String playerName;
    private ArrayList<ImageView> teachersImages;
    private ArrayList<ImageView> towersImages;
    private ArrayList<ImageView> tableStudentsImages;
    private ArrayList<ImageView> lobbyStudentsImages;
    private ImageView tableBounds;
    private ImageView coinImage;
    private HashMap<Color,Integer> numOfStudentsOnTable;
    private ArrayList<Color> studentsInLobby;

    public GUIBoard(ClientBoard clientBoard,GUI gui,GameTableController controller,ImageView tableBounds){
        this.clientBoard = clientBoard;
        this.gui = gui;
        this.controller = controller;
        this.playerName = clientBoard.getOwner();
        this.tableBounds = tableBounds;
        numOfStudentsOnTable = new HashMap<>();
        teachersImages = new ArrayList<>();
        towersImages = new ArrayList<>();
        tableStudentsImages = new ArrayList<>();
        lobbyStudentsImages = new ArrayList<>();
        studentsInLobby = new ArrayList<>();
        initializeBoard();
        setTableEvents();
    }

    private void initializeBoard(){
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN)); //l'ordine è importante per gli offset del rendering
        for(Color color : tableColors){
            numOfStudentsOnTable.put(color,clientBoard.getStudentsTable().get(color));
        }

        studentsInLobby.addAll(clientBoard.getLobby());
    }
    private void setTableEvents(){
        tableBounds.setOnDragOver((DragEvent e) -> { //qui si avrà il check dell'action parser (se non è clickable questo evento non deve partire)
            if (e.getGestureSource() != tableBounds && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });
        tableBounds.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) { //ci sarà da ottimizzare tutto e pulirlo sfruttando data structures apposite
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) { //per generalizzarlo avremo una variabile draggedElement così che qui sappiamo cosa sta venendo rilasciato
                    //per ora creiamo solo il caso specifico di move from lobby
                    ClientBoard clientBoard = gui.getOwningPlayerBoard();
                    int studentID = Integer.parseInt(db.getString());
                    Color student  = clientBoard.getLobby().get(studentID); //prendo il colore
                    addStudentToTable(student);
                    success = true;
                    controller.addSelectedStudent(studentID, ISLAND_ID_NOT_RECEIVED);
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    private void addStudentToTable(Color student) {
        int numOf = numOfStudentsOnTable.get(student);
        numOfStudentsOnTable.put(student,numOf+1);
        populateTables();
    }


    public void populate(){
        clearBoard();
        populateTables();
        populateLobby();
        populateTowers();
        populateTeachers();
        if (gui.getGB().isExpertGame()){
            populateCoins();
        }
    }

    private void populateTeachers(){
        int teacherCounter = 0;
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color teacher : tableColors){
            if(clientBoard.getTeacherTable().contains(teacher)){
                ImageView teachersImage = new ImageView("/graphics/"+teacher.toString().toLowerCase()+"_teacher.png");
                teachersImage.setFitWidth(TEACHER_BOARD_WIDTH);
                teachersImage.setFitHeight(TEACHER_BOARD_HEIGHT);
                teachersImage.setX(TEACHER_BOARD_START_X+teacherCounter*STUDENT_TABLE_HGAP);
                teachersImage.setY(TEACHER_BOARD_START_Y);
                teachersImages.add(teachersImage);
                gui.addElementToScene(teachersImage);
            }
            teacherCounter++;
        }
    }
    private void populateLobby(){
        int studentRowCounter = 0;
        int studentColumnCounter = 0;
        int studentIDCounter = 0; //identificativo studente lobby
        for(Color student : studentsInLobby){
            ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
            studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
            studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
            studentImage.setX(STUDENT_BOARD_START_X+studentColumnCounter*STUDENT_TABLE_HGAP);
            studentImage.setY(STUDENT_LOBBY_START_Y+studentRowCounter*STUDENT_LOBBY_VGAP);
            int finalStudentIDCounter = studentIDCounter; //event handler accetta solo variabili final
            //se si mantiene la stessa scelta di effetti per tutti gli oggetti clickable questi tre metodi possono finire in un unico metodo
            /*
            studentImage.setOnMouseClicked((MouseEvent e) -> {
                handleClickEvent(finalStudentIDCounter,Clickable.LOBBY_STUDENT);
            });
            */
            if(clientBoard.equals(gui.getOwningPlayerBoard())){
                studentImage.setOnDragDetected((MouseEvent e) -> {
                    if(controller.actionParser.canClick(gui.getCurrentState(),Clickable.LOBBY_STUDENT)){
                        Dragboard db = studentImage.startDragAndDrop(TransferMode.MOVE);

                        ClipboardContent content = new ClipboardContent();
                        content.putString(Integer.toString(finalStudentIDCounter));
                        db.setContent(content);
                        System.out.println("Inizio il drag event per "+finalStudentIDCounter);
                        e.consume();
                    }
                });
                studentImage.setOnDragDone(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        System.out.println("Drag completato, tolgo lo studente dalla lobby");
                        if (event.getTransferMode() == TransferMode.MOVE) {
                            studentsInLobby.remove(student);
                            lobbyStudentsImages.remove(studentImage);
                            gui.removeElementFromScene(studentImage);
                        }
                        event.consume();
                    }
                });
                studentImage.setOnMouseEntered((MouseEvent e) -> {
                    controller.handleHoverEvent(studentImage, Clickable.LOBBY_STUDENT);
                });
                studentImage.setOnMouseExited((MouseEvent e) -> {
                    if (studentImage.getEffect()==null || !(DropShadow.class).equals(studentImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                        studentImage.setEffect(null);
                });
            }

            lobbyStudentsImages.add(studentImage);
            gui.addElementToScene(studentImage);
            if(studentColumnCounter==4 && studentRowCounter == 0){ //fa abbastanza schifo, miglioro appena ho tempo
                studentColumnCounter=0;
                studentRowCounter++;
            }else{
                studentColumnCounter++;
            }
            studentIDCounter++;
        }
    }

    private void populateTables(){
        int tableCounter = 0;
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color color : tableColors){
            int numOfStudents = numOfStudentsOnTable.get(color);
            //int numOfStudents = 10; test
            String studentImagePath = "/graphics/"+color.toString().toLowerCase()+"_student.png";
            for(int i = 0; i< numOfStudents;i++){
                ImageView studentImage = new ImageView(studentImagePath);
                studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
                studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
                System.out.println("Printo studente nella table"+color+" su (X,Y): "+(947+tableCounter*STUDENT_TABLE_HGAP)+(148+i*STUDENT_TABLE_VGAP));
                studentImage.setX(STUDENT_BOARD_START_X+tableCounter*STUDENT_TABLE_HGAP);
                studentImage.setY(STUDENT_TABLE_START_Y+i*STUDENT_TABLE_VGAP);
                studentImage.setOnMouseClicked((MouseEvent e) -> {
                    controller.handleClickEvent(color.getIndex(),Clickable.TABLE_STUDENT); //come id passa l'index del colore della board
                });
                tableStudentsImages.add(studentImage);
                gui.addElementToScene(studentImage);
            }
            tableCounter++;
        }

    }

    private void populateTowers(){
        int towersCounter = clientBoard.getTowers();
        System.out.println("CI SONO " + towersCounter + " DEL COLORE "+clientBoard.getTeam());
        int halfTowersCounter;
        double offsetY,offsetX;
        if (gui.getNumOfPlayers()==3){
            offsetX=TOWER_TABLE_HGAP;
            halfTowersCounter=3;
        }
        else{
            offsetX=TOWER_TABLE_HGAP*0.75;
            halfTowersCounter=4;
        }
        String towerColor = clientBoard.getTeam().toString().toLowerCase();
        System.out.println("Il colore della torre è "+ towerColor);
        for (int i = 0; i<towersCounter;i++){
            ImageView towerImage = new ImageView("graphics/"+towerColor+"_board_tower.png");
            towerImage.setFitHeight(TOWER_IMAGE_HEIGHT);
            towerImage.setFitWidth(TOWER_IMAGE_WIDTH);
            towerImage.setX(TOWER_IMAGE_START_X +(i%(halfTowersCounter))*offsetX);
            offsetY= i>=halfTowersCounter? TOWER_TABLE_VGAP: 0;
            towerImage.setY(TOWER_IMAGE_START_Y + offsetY);
            towerImage.setPreserveRatio(true);
            towersImages.add(towerImage);
            gui.addElementToScene(towerImage); //in teoria le torri non sono clickable perché è tutto automatico

        }

    }

    private void populateCoins(){
        int coinCount = clientBoard.getCoins();
        if (coinCount>0){
            coinImage = new ImageView("/graphics/coin.png");
            coinImage.setX(COIN_BOARD_START_X);
            coinImage.setY(COIN_BOARD_START_Y);
            coinImage.setFitWidth(COIN_IMAGE_WIDTH);
            coinImage.setFitHeight(COIN_IMAGE_HEIGHT);
            coinImage.setPreserveRatio(true);
            Tooltip numOfCoins = new Tooltip(("AVAILABLE COINS: "+coinCount));
            numOfCoins.setShowDelay(Duration.seconds(0.3));
            Tooltip.install(coinImage, numOfCoins);
            gui.addElementToScene(coinImage);
            coinImage.toFront();
        }
    }

    public void clearBoard(){
        for(ImageView student : lobbyStudentsImages)
            gui.removeElementFromScene(student);
        for(ImageView student : tableStudentsImages)
            gui.removeElementFromScene(student);
        for(ImageView teacher : teachersImages)
            gui.removeElementFromScene(teacher);
        for(ImageView tower : towersImages)
            gui.removeElementFromScene(tower);
        gui.removeElementFromScene(coinImage);

        lobbyStudentsImages.clear();
        tableStudentsImages.clear();
        teachersImages.clear();
        towersImages.clear();

    }
}
