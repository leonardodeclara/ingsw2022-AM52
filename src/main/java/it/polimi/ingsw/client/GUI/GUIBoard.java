package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.GUI.GUIControllers.GameTableController;
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

/**
 * Class GUIBoard renders the board's elements on the screen
 * For each player, a GUIBoard instance is assigned
 */
public class GUIBoard {
    private ClientBoard clientBoard;
    private GUI gui;
    private ActionParser actionParser;
    private GameTableController controller;
    private String playerName;
    private ArrayList<ImageView> teachersImages;
    private ArrayList<ImageView> towersImages;
    private ArrayList<ImageView> tableStudentsImages;
    private ArrayList<ImageView> lobbyStudentsImages;
    private ImageView tableBounds;
    private ImageView coinImage;
    private HashMap<Color,Integer> numOfStudentsOnTable;
    private ArrayList<Color> clientSideLobbyStudents;

    public GUIBoard(ClientBoard clientBoard,GUI gui,GameTableController controller,ImageView tableBounds){
        this.clientBoard = clientBoard;
        this.gui = gui;
        this.controller = controller;
        this.actionParser= controller.getActionParser();
        this.playerName = clientBoard.getOwner();
        this.tableBounds = tableBounds;
        numOfStudentsOnTable = new HashMap<>();
        teachersImages = new ArrayList<>();
        towersImages = new ArrayList<>();
        tableStudentsImages = new ArrayList<>();
        lobbyStudentsImages = new ArrayList<>();
        clientSideLobbyStudents = new ArrayList<>();
        initializeBoard();
        setTableEvents();
    }

    /**
     * Method initializeBoard sets up the hashmap which associates Color->number of students
     * and the lobby students list
     * These two data structures can be modified freely on client side by drag n drop events,
     * because the copies from server are stored in GameBoard instance
     */
    private void initializeBoard(){
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color color : tableColors){
            numOfStudentsOnTable.put(color,clientBoard.getStudentsTable().get(color));
        }

        clientSideLobbyStudents.addAll(clientBoard.getLobby());
    }

    /**
     * Method setTableEvents sets up drag n drop events for the table
     */
    private void setTableEvents(){
        tableBounds.setOnDragOver((DragEvent e) -> {
            if (e.getGestureSource() != tableBounds && e.getDragboard().hasString()) {
                Dragboard db = e.getDragboard();
                int studentID = Integer.parseInt(db.getString());
                Color student = clientBoard.getLobby().get(studentID);
                if(numOfStudentsOnTable.get(student) < 10)
                    e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });
        tableBounds.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    ClientBoard clientBoard = gui.getOwningPlayerBoard();
                    int studentID = Integer.parseInt(db.getString());
                    Color student = clientBoard.getLobby().get(studentID);
                    addStudentToTable(student);
                    success = true;
                    ArrayList<Object> selection = new ArrayList<>();
                    selection.add(studentID);
                    selection.add(ISLAND_ID_NOT_RECEIVED);
                    actionParser.handleSelectionEvent(selection, gui.getCurrentState());
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    /**
     * Method addStudentToTable adds a student to the hashmap numOfStudentsOnTable
     * which stores the number of students for each color client side
     * It also refreshes table rendered items to show the newly added student
     * @param student color of the student to add to the hashmap
     */
    private void addStudentToTable(Color student) {
        int numOf = numOfStudentsOnTable.get(student);
        numOfStudentsOnTable.put(student,numOf+1);
        populateTables();
    }


    /**
     * Method populate clear all the rendered items on the board
     * and draws them again
     * It also sets again drag n drop events on the table
     */
    public void populate(){
        populateTables();
        populateLobby();
        populateTowers();
        populateTeachers();
        if (gui.getGB().isExpertGame()){
            populateCoins();
        }
        setTableEvents();
    }

    /**
     * Method populateTeachers adds all the teachers images to the board
     * Each image is rendered if and only if the board's owner has that specific teacher
     */
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

    /**
     * Method populateLobby adds all the student images to the lobby
     * If a drag n drop event has removed one or more students
     * from the lobby's students list, they will be
     * rendered even if these are still in lobby, server-side.
     * This is done to preview what the board will look like
     * if the player decides to confirm his action
     */
    private void populateLobby(){
        clearLobby();
        int studentRowCounter = 0;
        int studentColumnCounter = 0;
        int studentIDCounter = 0;
        for(Color student : clientSideLobbyStudents){
            ImageView studentImage = new ImageView("/graphics/"+student.toString().toLowerCase()+"_student.png");
            studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
            studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
            studentImage.setX(STUDENT_BOARD_START_X+studentColumnCounter*STUDENT_TABLE_HGAP);
            studentImage.setY(STUDENT_LOBBY_START_Y+studentRowCounter*STUDENT_LOBBY_VGAP);
            int finalStudentIDCounter = studentIDCounter;
            if(clientBoard.equals(gui.getOwningPlayerBoard())){
                studentImage.setOnDragDetected((MouseEvent e) -> {
                    if(actionParser.canDrag(gui.getCurrentState(),Clickable.LOBBY_STUDENT)){
                        Dragboard db = studentImage.startDragAndDrop(TransferMode.MOVE);
                        db.setDragView(studentImage.getImage());
                        ClipboardContent content = new ClipboardContent();
                        content.putString(Integer.toString(finalStudentIDCounter));
                        db.setContent(content);
                        e.consume();
                    }
                });
                studentImage.setOnDragDone(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        if (event.getTransferMode() == TransferMode.MOVE) {
                            int removedIndex = clientSideLobbyStudents.indexOf(student);
                            clientSideLobbyStudents.remove(student);
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
                    if (studentImage.getEffect()==null || !(DropShadow.class).equals(studentImage.getEffect().getClass()))
                        studentImage.setEffect(null);
                });
                studentImage.setOnMouseClicked((MouseEvent e)->{
                    actionParser.handleSelectionEvent(finalStudentIDCounter,Clickable.LOBBY_STUDENT, gui.getCurrentState());
                    controller.handleSelectionEffect(studentImage,Clickable.LOBBY_STUDENT);
                });
            }

            lobbyStudentsImages.add(studentImage);
            gui.addElementToScene(studentImage);
            if(studentColumnCounter==4 && studentRowCounter == 0){
                studentColumnCounter=0;
                studentRowCounter++;
            }else{
                studentColumnCounter++;
            }
            studentIDCounter++;
        }
    }

    /**
     * Method populateTables adds all the student images to the table
     * If a drag n drop event has added one or more students
     * to the table's students hashmap, they will be
     * rendered even if these are still in lobby, server-side.
     * This is done to preview what the board will look like
     * if the player decides to confirm his action
     */
    private void populateTables(){
        clearTables();
        int tableCounter = 0;
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN));
        for(Color color : tableColors){
            int numOfStudents = numOfStudentsOnTable.get(color);
            String studentImagePath = "/graphics/"+color.toString().toLowerCase()+"_student.png";
            for(int i = 0; i< numOfStudents;i++){
                ImageView studentImage = new ImageView(studentImagePath);
                studentImage.setFitWidth(STUDENT_TABLE_WIDTH);
                studentImage.setFitHeight(STUDENT_TABLE_HEIGHT);
                studentImage.setX(STUDENT_BOARD_START_X+tableCounter*STUDENT_TABLE_HGAP);
                studentImage.setY(STUDENT_TABLE_START_Y+i*STUDENT_TABLE_VGAP);
                studentImage.setOnMouseClicked((MouseEvent e) -> {
                    actionParser.handleSelectionEvent(color,Clickable.TABLE_STUDENT,gui.getCurrentState());
                    controller.handleSelectionEffect(studentImage,Clickable.TABLE_STUDENT);
                });
                tableStudentsImages.add(studentImage);
                gui.addElementToScene(studentImage);
            }
            tableCounter++;
        }

    }

    /**
     * Method populateTowers adds all the towers images to the board
     */
    private void populateTowers(){
        clearTowers();
        int towersCounter = clientBoard.getTowers();
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
        for (int i = 0; i<towersCounter;i++){
            ImageView towerImage = new ImageView("graphics/"+towerColor+"_board_tower.png");
            towerImage.setFitHeight(TOWER_IMAGE_HEIGHT);
            towerImage.setFitWidth(TOWER_IMAGE_WIDTH);
            towerImage.setX(TOWER_IMAGE_START_X +(i%(halfTowersCounter))*offsetX);
            offsetY= i>=halfTowersCounter? TOWER_TABLE_VGAP: 0;
            towerImage.setY(TOWER_IMAGE_START_Y + offsetY);
            towerImage.setPreserveRatio(true);
            towersImages.add(towerImage);
            gui.addElementToScene(towerImage);

        }

    }

    /**
     * Method populateCoins adds a coin image to the board
     * It also adds a tooltip which tells to the player how
     * many coins does he have
     */
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

    /**
     * Method clearLobby removes all the lobby student images
     * from the screen
     */
    private void clearLobby(){
        for(ImageView student : lobbyStudentsImages)
            gui.removeElementFromScene(student);
        lobbyStudentsImages.clear();

    }

    /**
     * Method clearTables removes all the table student images
     * from the screen
     */
    private void clearTables(){
        for(ImageView student : tableStudentsImages){
            gui.removeElementFromScene(student);
        }
        tableStudentsImages.clear();

    }

    /**
     * Method clearTeachers removes all the teachers images
     * from the screen
     */
    private void clearTeachers(){
        for(ImageView teacher : teachersImages)
            gui.removeElementFromScene(teacher);
        teachersImages.clear();

    }

    /**
     * Method clearTowers removes all the towers images
     * from the screen
     */
    private void clearTowers(){
        for(ImageView tower : towersImages)
            gui.removeElementFromScene(tower);
        towersImages.clear();

    }

    public ClientBoard getClientBoard(){
        return clientBoard;
    }

    /**
     * Method clearBoard removes all the board images
     * from the screen
     */
    public void clearBoard(boolean reset){
        clearLobby();
        clearTables();
        clearTeachers();
        clearTowers();
        gui.removeElementFromScene(coinImage);

        if(reset)
            reset();

    }

    public boolean isLobbyModified(){
        return clientSideLobbyStudents.size() != clientBoard.getLobby().size();
    }

    /**
     * Method reset clears lobby students list and table hashmap
     * and calls initialization
     * This is called when it's needed to synchronize board status
     * between GUI client and server after a drag n drop event which modified
     * the board client side
     */
    private void reset(){
        numOfStudentsOnTable.clear();
        clientSideLobbyStudents.clear();
        initializeBoard();
    }
}
