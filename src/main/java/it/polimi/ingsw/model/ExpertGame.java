package it.polimi.ingsw.model;

import java.util.*;

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

    public HashMap<String,Number> calculateInfluenceForCard6(Island island){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players);
        return calculateIslandOwner(island,influences);
    }

    public HashMap<String,Number> calculateInfluenceForCard8(Island island){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players);
        int towersOwnerIndex = getTowersOwnerIndex(island,players);
        influences.add(currentPlayer.getPlayerId(),2);
        if(towersOwnerIndex != -1)
            influences.add(towersOwnerIndex,island.getTowers().size());
        return calculateIslandOwner(island,influences);
    }

    public HashMap<String,Number> calculateInfluenceForCard9(Island island,Color bannedColor){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players,bannedColor);
        int towersOwnerIndex = getTowersOwnerIndex(island,players);
        if(towersOwnerIndex != -1)
            influences.add(towersOwnerIndex,island.getTowers().size());
        return calculateIslandOwner(island,influences);
    }

    protected ArrayList<Integer> calculateStudentsInfluences(Island island,ArrayList<Player> players,Color bannedColor){
        int infl = 0;
        ArrayList<Integer> influences = new ArrayList<>();
        for(Player p: players){
            infl = 0;
            for(Color t:p.getBoard().getTeacherTable()){
                if(t != bannedColor)
                    infl+=island.getStudentsOfColor(t).size();
            }
            influences.add(players.indexOf(p),infl);
        }
        return influences;
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
