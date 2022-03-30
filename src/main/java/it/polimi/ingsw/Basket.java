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
        this.studentsNum=studentsNum;
        size=0;
        for(int i = 0; i< this.studentsNum.length;i++){
            size+= this.studentsNum[i];
        }
    }

    private int[] getStudentsNum() {
        return studentsNum;
    }

    public int getSize() {
        return size;
    }

    /**
     * This methods allows the extraction of a student tile from the basket.
     * The color is randomly choosen.
     * @return a new student instance.
     */
    public Student pickStudent() {
        int colorIndex;
        Random random = new Random();

        //dovrò gestire una eccezione da qualche parte
        //magari un try-catch NullPointerException dove faccio la chiamata
        //metodo di scelta randomica del colore va sistemato
        // in modo da pescare con più probabilità un colore se di quello ci sono più pedine (colori hanno pesi)
        if (size==0)
            return null;
        do {
            colorIndex = random.nextInt(Color.values().length);
        } while (studentsNum[colorIndex] <= 0);
        studentsNum[colorIndex]=studentsNum[colorIndex]-1;
        size-= 1;
        return new Student(Color.values()[colorIndex]);
    }

    /**
     * This methods manages the insertion of a new student
     * @param student: instance of the student that must be added to the basket
     */
    public void putStudent(Student student){
        int colorIndex = student.getColor().getIndex();
        studentsNum[colorIndex]=studentsNum[colorIndex]+1;
        size+= 1;
    }
}
