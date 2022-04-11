package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpertGameTest {

    @Test
    void instantiateGameElementsTest() {
        ExpertGame game = new ExpertGame();
        game.instantiateGameElements();
        assertEquals(20,game.getCoins());
        assertEquals(4, game.getBans());
    }

    @Test
    void moveStudentFromLobbyForCard2() {
        ExpertGame game = new ExpertGame();


    }

    @Test
    void updateTeachersOwnershipForCard2() {
    }

    @Test
    void moveMotherNatureForCard4() {
    }

    @Test
    void calculateInfluenceForCard6() {
    }

    @Test
    void calculateInfluenceForCard8() {
    }

    @Test
    void calculateInfluenceForCard9() {
    }
}