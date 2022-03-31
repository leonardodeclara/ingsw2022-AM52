package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.descriptor.FileSystemSource;

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
            Student picked = basket.pickStudent();
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
        assertNull(basket.pickStudent());
        assertEquals(0, basket.getSize());
    }

    /**
     * Method nullInputTest manages the creation of a new basket istance with null content
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
        basket.putStudent(new Student(Color.BLUE));
        assertEquals(11, basket.getSize());
    }

}