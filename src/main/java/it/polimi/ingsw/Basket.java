package it.polimi.ingsw;

import java.util.Random;

/**
 * This class contains the information and the methods that can be called on the game's bag.
 * The number of students in the bag are collected in an array, divided by color in the following order:
 * PINK, GREEN, BLUE, YELLOW, RED
 */
public class Basket {

    private int[] studentsNum;
    private int size;

    /**
     * Constructor creates a new basket, whose number of students are specified in the array given as input.
     * @param studentsNum: array of integers indicating the students numbers, divided by color.
     */
    public Basket(int[] studentsNum){
        if (studentsNum==null)
            throw new NullPointerException();
        this.studentsNum=studentsNum;
        size=0;
        for(int i = 0; i< this.studentsNum.length;i++){
            size+= this.studentsNum[i];
        }
    }

    /**
     * This methods allows the extraction of a student tile from the basket.
     * The color is randomly chosen according to the current weight of each color.
     * @return a new student instance.
     */
    public Color pickStudent() {

        if (size==0)
            return null;
        //alternativamente potrei lanciare un'eccezione ad hoc, tipo EmptyBasketException
        //in modo che il chiamante possa fare un try catch ed in caso di catch fa game.setLastRound(true)
        //in questo modo posso evitare di restituire null e differenziarlo da altri casi di errore

        int colorIndex = weightedRandomIndex();
        if (colorIndex==-1)
            return null;
        studentsNum[colorIndex]-= 1;
        size-= 1;
        return Color.values()[colorIndex];
    }

    /**
     * This method manages the insertion of a new student.
     * @param color: instance of the color that must be added to the basket.
     */
    public void putStudent(Color color){
        int colorIndex = color.getIndex();
        studentsNum[colorIndex]=studentsNum[colorIndex]+1;
        size+= 1;
    }

    /**
     * This private method generates in weighted-random way a color index from which it will be picked a new student
     * @return the index of the student's color
     */
    private int weightedRandomIndex(){
        int weights = size;
        int currentWeight = 0;
        Random random = new Random();
        int randomWeight = random.nextInt(weights);
        for (int i = 0; i< studentsNum.length;i++){
            currentWeight+=studentsNum[i];
            if(currentWeight>randomWeight)
                return i;
        }
        return -1;
    }

    public int[] getStudentsNum() {
        return studentsNum;
    }

    public int getSize() {
        return size;
    }

}
