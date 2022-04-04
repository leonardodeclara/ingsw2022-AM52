package it.polimi.ingsw;

import java.util.ArrayList;

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
}
