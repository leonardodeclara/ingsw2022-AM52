package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpertGame extends Game {
    private ArrayList<Personality> personalities;
    private Personality activePersonality; //c'è un solo personaggio attivo per round
    private int coins;
    private int bans;

    public ExpertGame(){
        super();

    }
    @Override
    public void instantiateGameElements() {
        super.instantiateGameElements();
    }

    public boolean moveStudentFromLobbyForCard2(int playerId,int studentIndex,int islandId){
        Player player = players.get(playerId);
        if(!isMoveStudentFromLobbyLegal(player,studentIndex,islandId))
            return false;
        if(islandId == -1){
            player.getBoard().removeFromLobby(studentIndex);
            Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
            player.getBoard().addToTable(studentToMove);
        }
        else
        {
            player.getBoard().removeFromLobby(studentIndex);
            Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
            Island islandDest = islands.get(islandId);
            islandDest.addStudent(studentToMove);
        }
        //aggiorna l'ownership dei teacher
        for(Color c : Color.values()) {
            Player owner = teachersOwners.get(c);
            if (player.getBoard().getTableNumberOfStudents(c) >= owner.getBoard().getTableNumberOfStudents(c)) {
                owner.getBoard().removeTeacher(c);
                player.getBoard().addTeacher(c);
                teachersOwners.put(c, player);
            }
        }
        return true;
    }


    public boolean moveMotherNatureForCard4(int playerId,int numSteps){
        if(!isMoveMNLegal(playerId,numSteps+2))
            return false;

        Island from = islands.get(currentMotherNatureIsland.getIslandIndex());
        Island dest = islands.get(from.getIslandIndex() + numSteps+2 % islands.size());

        //manca il ricalcolo dell'influenza

        from.setMotherNature(false);
        dest.setMotherNature(true);
        return true;
    }

    public HashMap<String,Number> calculateInfluenceForCard6(Island island) {
        int max_infl = 0, infl = 0;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            for (Color t : p.getBoard().getTeacherTable()) {
                infl += island.getStudentsOfColor(t).size();
            }
            if (infl > max_infl) {
                max_infl = infl;
                owner = p;
                isDraw = 0;
            } else if (infl == max_infl)
                isDraw = 1;

        }

        if (!owner.equals(island.getOwner()))
            island.setOwner(owner);

        returnMap.put("ID Player", owner.getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;

    }

    public HashMap<String,Number> calculateInfluenceForCard8(Island island) {
        int max_infl = 0, infl = 0;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            if(p.equals(currentPlayer))
                infl+=2;
            for (Color t : p.getBoard().getTeacherTable()) {
                infl += island.getStudentsOfColor(t).size();
                if (island.getTowers().size() > 0)
                    if (island.getOwnerTeam().equals(p.getTeam()))
                        infl += island.getTowers().size();
            }
            if (infl > max_infl) {
                max_infl = infl;
                owner = p;
                isDraw = 0;
            } else if (infl == max_infl)
                isDraw = 1;
            infl = 0;
        }

        if (!owner.equals(island.getOwner()))
            island.setOwner(owner);

        returnMap.put("ID Player", owner.getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;

    }

    public HashMap<String,Number> calculateInfluenceForCard9(Island island,Color bannedColor) {
        int max_infl = 0, infl = 0;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            if(p.equals(currentPlayer))
                infl+=2;
            for (Color t : p.getBoard().getTeacherTable()) {
                if(!t.equals(bannedColor))
                    infl += island.getStudentsOfColor(t).size();
                if (island.getTowers().size() > 0)
                    if (island.getOwnerTeam().equals(p.getTeam()))
                        infl += island.getTowers().size();
            }
            if (infl > max_infl) {
                max_infl = infl;
                owner = p;
                isDraw = 0;
            } else if (infl == max_infl)
                isDraw = 1;
            infl = 0;
        }

        if (!owner.equals(island.getOwner()))
            island.setOwner(owner);

        returnMap.put("ID Player", owner.getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;

    }
}
