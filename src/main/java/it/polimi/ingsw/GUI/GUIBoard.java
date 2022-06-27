package it.polimi.ingsw.GUI;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.GUI.GUIControllers.GameTableController;
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

//TODO: sistemare il caso in cui provi a spostare meno di due studenti dalla lobby in MOVE_FROM_LOBBY

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

    private void initializeBoard(){
        ArrayList<Color> tableColors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.PINK,Color.YELLOW,Color.RED,Color.GREEN)); //l'ordine è importante per gli offset del rendering
        for(Color color : tableColors){
            numOfStudentsOnTable.put(color,clientBoard.getStudentsTable().get(color));
        }

        clientSideLobbyStudents.addAll(clientBoard.getLobby());
    }

    private void setTableEvents(){
        tableBounds.setOnDragOver((DragEvent e) -> { //qui si avrà il check dell'action parser (se non è clickable questo evento non deve partire)
            if (e.getGestureSource() != tableBounds && e.getDragboard().hasString()) {
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

    private void addStudentToTable(Color student) {
        int numOf = numOfStudentsOnTable.get(student);
        numOfStudentsOnTable.put(student,numOf+1);
        //System.out.println("Aggiungo in numOfStudentsOnTable di "+getClientBoard().getOwner()+" uno studente "+student);
        for(Color s : numOfStudentsOnTable.keySet())
            //System.out.println("C'è uno studente "+s+" in numOfStudentsOnTable");
        populateTables();
    }


    //quando si riceve update, viene chiamato populateDashBoard con reset= true, che a sua volta per ogni GUIBoard chiama clearBoard(reset=true)
    //reset in guiBoard fa clear di studentsInLobby e numOfStudentsInTable e li rinizializza

    //quando si fa click sulla board di qualcuno, viene chiamata populateDashboard con reset=false, quella attuale viene pulita con clearBoard(reset=false) e disegnata quella nuova, ma non viene fatto il reset

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
        clearLobby();
        int studentRowCounter = 0;
        int studentColumnCounter = 0;
        int studentIDCounter = 0; //identificativo studente lobby
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
                        //System.out.println("Drag completato, tolgo lo studente dalla lobby");
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
                    if (studentImage.getEffect()==null || !(DropShadow.class).equals(studentImage.getEffect().getClass())) //rivedere, qui il comportamento è lo stesso delle carte personaggio
                        studentImage.setEffect(null);
                });
                studentImage.setOnMouseClicked((MouseEvent e)->{
                    actionParser.handleSelectionEvent(finalStudentIDCounter,Clickable.LOBBY_STUDENT, gui.getCurrentState());
                    controller.handleSelectionEffect(studentImage,Clickable.LOBBY_STUDENT);
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
        //bisogna associare a ogni studente della lobby un qualcosa di univoco in modo che negli eventi drag possa essere
        //associato questo qualcosa di univoco a quello specifico studente

        //l'unica cosa univoca è l'indice all'interno dell'array lobby di clientBoard.
        //il problema è che se iteriamo lobby, dobbiamo renderizzare solo gli studenti presenti anche in studentsinlobby ma essendo color un enum
        //è impossibile fare studentsInLobby.contains(student) perchè non terrebbe conto del numero di studenti del tipo student contenuti in studentsInLobby

        //l'unica è ciclare studentsInLobby e associare l'indice nell'array all'evento drag dell'immagine di quello studente usando un hashmap
        //che dall'indice di studentsInLobby (studentCounter) sa a cosa corrisponde in lobby.
        //quando uno studente viene aggiunto alla table/isola è facile farlo, e lo si toglie da studentsInLobby come al solito.
        //a quel punto però si aggiorna l'hashmap che binda gli id di lobby a quelli di studentsInLobby
        //quindi quando si posiziona la prima volta lo studente 3 ad esempio di lobby sulla table, questo viene rimosso da studentsInLobby
        //e si modifica questa hashmap
        //per le isole è la stessa cosa. Nel messaggio si avrà l'id di lobby, si modifica studentsInLobby, si aggiorna l'hashmap in modo che
        //il prossimo drag vada a buon fine



        //per l'hashmap il codice di aggiornamento è


        /*
        l'hashmap è <studentsInLobbyIndex,lobbyID>
         // 1 2 3 4 5 6 studentsInLobby
            R G B B Y G
        // 1 2 3 4 5 6 lobby
           R G B B Y G
        //è stato rimosso il 5 di lobby  (removedIndex)
        // hashMap.clear()   //per sicurezza
        //for(i=0;i<studentsInLobby.size();i++){
            if(i<removedIndex)
                hashMap.put(i,i)
            if(i>=removedIndex)
                hashMap.put(i,i+1)

        // }


        l'aggiornamento dell'hashmap avviene quando si toglie qualcosa da studentsInLobby
        l'hashmap invece si usa quando si vuole draggare uno studente, poichè in clipboard viene messo l'id di lobby, non quello di studentsInLobby
        ossia  hashMap.get(finalStudentIDCounter)
         */
    }

    private void populateTables(){
        clearTables();
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

    private void clearLobby(){
        for(ImageView student : lobbyStudentsImages)
            gui.removeElementFromScene(student);
        lobbyStudentsImages.clear();

    }
    private void clearTables(){
        for(ImageView student : tableStudentsImages){
            System.out.println("Rimuovo dalla board lo studente "+student.getImage().getUrl());
            gui.removeElementFromScene(student);
        }
        tableStudentsImages.clear();

    }
    private void clearTeachers(){
        for(ImageView teacher : teachersImages)
            gui.removeElementFromScene(teacher);
        teachersImages.clear();

    }
    private void clearTowers(){
        for(ImageView tower : towersImages)
            gui.removeElementFromScene(tower);
        towersImages.clear();

    }

    public ClientBoard getClientBoard(){
        return clientBoard;
    }
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

    private void reset(){
        numOfStudentsOnTable.clear();
        clientSideLobbyStudents.clear();
        initializeBoard();
    }
}
