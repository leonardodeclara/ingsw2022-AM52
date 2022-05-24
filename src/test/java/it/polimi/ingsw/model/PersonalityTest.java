package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonalityTest {

    @Test
    void pastUsageTest(){
        Personality card = new Personality(2);

        assertFalse(card.isHasBeenUsed());
        assertEquals(2, card.getCost());
        card.setHasBeenUsed(true);
        assertEquals(2,card.getCost());
    }

    @Test
    void lobbyPersonalityTest(){
        LobbyPersonality card = new LobbyPersonality(1);
        assertEquals(4, card.getLobbySize());
        assertEquals(1, card.getCharacterId());
        for (int i = 0; i<10; i++){
            card.addStudent(Color.YELLOW);
        }
        assertEquals(4, card.getStudents().size());
        card = new LobbyPersonality(7);
        for (int i = 0; i<10; i++){
            card.addStudent(Color.BLUE);
        }
        assertEquals(6, card.getStudents().size());
    }

    @Test
    void costChangeTest(){
        Personality card = new Personality(2);
        card.updateCost();
        assertEquals(3,card.getCost());
        assertTrue(card.isHasBeenUsed());
    }

    @Test
    void banPersonalityTest(){
        BanPersonality card = new BanPersonality(5);
        assertEquals(4, card.getBans());
        card.addBan();
        assertEquals(4, card.getBans());
        card.removeBan();
        assertEquals(3, card.getBans());
        card.addBan();
        assertEquals(4, card.getBans());
        for(int i = 0; i<4; i++){
            card.removeBan();
        }
        assertEquals(0, card.getBans());
        card.removeBan();
        assertEquals(0, card.getBans());
    }
}