/*package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class main2 {

    public static void main(String[] args) {
        PrintStream output = new PrintStream(System.out);
        GameBoard GB = new GameBoard(output);
        ArrayList<ClientIsland> islands = new ArrayList<>();

        for(int i = 0; i < Constants.MAX_NUM_ISLANDS; i++){
            islands.add(new ClientIsland(i));
        }

        String nickname1 = new String();
        nickname1 = "Mari";
        String nickname2 = new String();
        nickname2 = "Leo";
        ArrayList<String> playersNickname = new ArrayList<>();
        playersNickname.add(nickname1);
        playersNickname.add(nickname2);

        ArrayList<Color> studentsOnIsland1 = new ArrayList<>();
        studentsOnIsland1.add(Color.RED);
        studentsOnIsland1.add(Color.YELLOW);

        ArrayList<Tower> towersOnIsland1 = new ArrayList<>();
        towersOnIsland1.add(Tower.WHITE);
        towersOnIsland1.add(Tower.WHITE);

        ArrayList<Integer> availableWizards = new ArrayList<>();
        availableWizards.add(1);
        availableWizards.add(2);
        availableWizards.add(3);
        availableWizards.add(4);

        ArrayList<Tower> availableTowers = new ArrayList<>();
        availableTowers.add(Tower.BLACK);
        availableTowers.add(Tower.WHITE);
        availableTowers.add(Tower.GREY);

        ArrayList<ClientCloud> clouds = new ArrayList<>();
        clouds.add(new ClientCloud(1));
        clouds.add(new ClientCloud(2));

        HashMap<String, ClientBoard> clientBoardHashMap = new HashMap<>();
        clientBoardHashMap.put(nickname1, new ClientBoard(2, nickname1, GB));
        clientBoardHashMap.put(nickname2, new ClientBoard(3, nickname2, GB));

        ArrayList<ClientPersonality> clientPersonalities = new ArrayList<>();
        clientPersonalities.add(new ClientPersonality(1, false, 3));



        GB.instantiateGameElements(islands);
        GB.setNickname(nickname1);
        GB.setNickname(nickname2);
        GB.setExpertGame(false);
        GB.setAvailableWizards(availableWizards);
        GB.setIslands(islands);
        GB.setPlayersNickname(playersNickname);
        GB.setClouds(clouds);
        GB.setClientBoards(clientBoardHashMap);
        GB.setPersonalities(clientPersonalities);
        GB.setCoins(5);
        GB.setClientTeam(nickname1, Tower.BLACK);
        GB.setClientTeam(nickname2, Tower.WHITE);
        GB.addClientBoard(nickname1);
        GB.addClientBoard(nickname2);
        GB.setIslandStudents(1, studentsOnIsland1);
        GB.setIslandTowers(1, towersOnIsland1);
        GB.emptyCloud(1);
        GB.emptyCloud(2);

        GB.print();


    }
}*/

