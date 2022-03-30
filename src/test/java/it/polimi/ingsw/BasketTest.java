package it.polimi.ingsw;

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
        Student picked = basket.pickStudent();
        assertEquals(9, basket.getSize());
    }


    /**
     * Method emptyBasketTest verifies that if the basket is empty the result of a pick action must be null
     */
    @Test
    void emptyBasketTest(){
        int[] content = {0,0,0,0,0};
        Basket basket = new Basket(content);
        assertNull(basket.pickStudent());
        assertEquals(0, basket.getSize());
    }

    @Test
    void putStudentTest() {
        int[] content = {2,2,2,2,2};
        Basket basket = new Basket(content);
        basket.putStudent(new Student(Color.BLUE));
        assertEquals(11, basket.getSize());
    }
}