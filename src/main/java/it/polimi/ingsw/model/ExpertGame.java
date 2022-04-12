package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ExpertGame extends Game {
    private ArrayList<Personality> personalities;
    private static final int NUM_PLAYABLE_PERSONALITY_CARDS = 3;
    private static final int NUM_EXISTING_PERSONALITY_CARDS = 12;
    private Personality activePersonality; //c'è un solo personaggio attivo per round
    private int coins;
    private int bans;

    public ExpertGame(){
        super();
        personalities = new ArrayList<>();
        coins=20;
        bans=4;
    }

    @Override
    public void instantiateGameElements() {
        super.instantiateGameElements();
        //l'estrazione potrebbe essere resa indipendente da instantiateGameElements
        //extractPersonalityCards();
    }

    public boolean moveStudentFromLobbyForCard2(int playerId,int studentIndex,int islandId){
        Player player = players.get(playerId);
        if(!isMoveStudentFromLobbyLegal(player,studentIndex,islandId))
            return false;
        Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
        player.getBoard().removeFromLobby(studentIndex);
        if(islandId == ISLAND_ID_NOT_RECEIVED)
            player.getBoard().addToTable(studentToMove);
        else {
            Island islandDest = islands.get(islandId);
            islandDest.addStudent(studentToMove);
        }
        //aggiorna l'ownership dei teacher
        updateTeachersOwnershipForCard2(player);
        return true;
    }

    public void updateTeachersOwnershipForCard2(Player player){
        for(Color c : Color.values()) {
            Player owner = teachersOwners.get(c);
            if(owner!=null){
                if (player.getBoard().getTableNumberOfStudents(c) >= owner.getBoard().getTableNumberOfStudents(c)) {
                    owner.getBoard().removeTeacher(c);
                    player.getBoard().addTeacher(c);
                    teachersOwners.put(c, player);
                }
            }
            else {
                if (player.getBoard().getTableNumberOfStudents(c)>0){
                    player.getBoard().addTeacher(c);
                    teachers.remove(c);
                    teachersOwners.put(c, player);
                }
            }
        }
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
        int max_infl = 0, infl;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            infl = 0;
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

        if (owner==null ){
            returnMap.put("ID Player", null);
        }
        else if (!owner.equals(island.getOwner()) && isDraw==0) {
            island.setOwner(owner);
            returnMap.put("ID Player", owner.getPlayerId());
        }
        else
            returnMap.put("ID Player", island.getOwner().getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;

    }

    public HashMap<String,Number> calculateInfluenceForCard8(Island island) {
        int max_infl = 0, infl;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            infl=0;
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
        }

        if (owner==null ){
            returnMap.put("ID Player", null);
        }
        else if (!owner.equals(island.getOwner()) && isDraw==0) {
            island.setOwner(owner);
            returnMap.put("ID Player", owner.getPlayerId());
        }
        else
            returnMap.put("ID Player", island.getOwner().getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;

    }

    public HashMap<String,Number> calculateInfluenceForCard9(Island island,Color bannedColor) {
        int max_infl = 0, infl;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            infl = 0;
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

        }

        if (owner==null ){
            returnMap.put("ID Player", null);
        }
        else if (!owner.equals(island.getOwner()) && isDraw==0) {
            island.setOwner(owner);
            returnMap.put("ID Player", owner.getPlayerId());
        }
        else
            returnMap.put("ID Player", island.getOwner().getPlayerId());
        returnMap.put("Is Draw", isDraw);
        return returnMap;
    }

    public void extractPersonalityCards() {
        ArrayList<Integer> extractedIndexes = new ArrayList<>();
        int randomIndex=0;
        for (int i = 0; i < NUM_PLAYABLE_PERSONALITY_CARDS; i++) {
            Random random = new Random();
            do{
                randomIndex = random.nextInt(NUM_EXISTING_PERSONALITY_CARDS)+1;
            } while(extractedIndexes.contains(randomIndex));
            extractedIndexes.add(randomIndex);
            if (randomIndex == 1 || randomIndex == 7 || randomIndex == 11) {
                LobbyPersonality extractedCard = new LobbyPersonality(randomIndex);
                int lobbyDimension = randomIndex==7 ? 6 : 4;
                for (int j = 0; j < lobbyDimension; j++)
                    extractedCard.addStudent(basket.pickStudent());
                personalities.add(extractedCard);
            } else if (randomIndex == 5) {
                BanPersonality extractedCard = new BanPersonality(randomIndex);
                personalities.add(extractedCard);
            } else {
                Personality extractedCard = new Personality(randomIndex);
                personalities.add(extractedCard);
            }
        }
    }

    public ArrayList<Personality> getPersonalities() {
        return personalities;
    }

    public Personality getActivePersonality() {
        return activePersonality;
    }

    public int getCoins() {
        return coins;
    }

    public int getBans() {
        return bans;
    }
}
