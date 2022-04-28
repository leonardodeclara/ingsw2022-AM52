package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBasketException;
import it.polimi.ingsw.model.Basket;
import it.polimi.ingsw.model.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasketTest {


    /**
     * Method pickStudentTest verifies that students are correctly extracted from the basket
     */
    @Test
    void pickStudentTest() {
        int[] content = {2,2,2,2,2};
        Basket basket = new Basket(content);
        int total = 10;
        for (int i = 0; i< 10; i++){
            Color picked = basket.pickStudent();
            assertEquals(total-i-1, basket.getSize());
        }
        int[] finalContent = {0,0,0,0,0};
        assertArrayEquals(finalContent, basket.getStudentsNum());

    }


    /**
     * Method emptyBasketTest verifies that if the basket is empty the result of a pick action must be null
     */
    @Test
    void emptyBasketTest(){
        int[] content = {0,0,0,0,0};
        Basket basket = new Basket(content);
        assertThrows(EmptyBasketException.class, ()->basket.pickStudent());
        assertEquals(0, basket.getSize());
    }

    /**
     * Method nullInputTest manages the creation of a new basket instance with null content.
     */
    @Test
    void nullInputTest(){
        int[] content = null;
        assertThrows(NullPointerException.class, ()->new Basket(content));
    }

    /**
     * Method putStudentTest verifies the correct insertion of a student
     */
    @Test
    void putStudentTest() {
        int[] content = {2,2,2,2,2};
        Basket basket = new Basket(content);
        basket.putStudent(Color.BLUE);
        assertEquals(11, basket.getSize());
    }

}