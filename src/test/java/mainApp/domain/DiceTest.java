package mainApp.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiceTest {

    @Test
    public void rollUnique_WithNullRandomObject_ExpectException(){
        Dice dice = new Dice(null);
        String expectedMessage = "Passed in a Null random object";
        ArrayList<Integer> previousRolls = new ArrayList<>();
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    dice.rollUnique(previousRolls);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void rollUnique_WithFullyArrayPlus1_ExpectException(){
        Random random = EasyMock.mock(Random.class);
        Dice dice = new Dice(random);

        String expectedMessage = "Passed an array with too many players.";
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        for (int i=0; i<6; i++){
            previousRolls.add(i);
        }

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    dice.rollUnique(previousRolls);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }


    @Test
    public void rollUnique_WithEmptyArray_Expect1(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();

        EasyMock.expect(random.nextInt(6)).andStubReturn(0);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(1, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With1elementAndGenerate1Duplicate_Expect2(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);

        EasyMock.expect(random.nextInt(6)).andReturn(0);
        EasyMock.expect(random.nextInt(6)).andReturn(1);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(2, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With1elementAndNoDuplicate_Expect2(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);

        EasyMock.expect(random.nextInt(6)).andReturn(1);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(2, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With2elementAndNoDuplicate_Expect4(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);
        previousRolls.add(3);

        EasyMock.expect(random.nextInt(6)).andReturn(3);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(4, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With2elementAndGenerate2Duplicate_Expect5(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);
        previousRolls.add(3);

        EasyMock.expect(random.nextInt(6)).andReturn(2);
        EasyMock.expect(random.nextInt(6)).andReturn(0);
        EasyMock.expect(random.nextInt(6)).andReturn(4);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(5, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With4elementAndGenerate3Duplicate_Expect2(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);
        previousRolls.add(3);
        previousRolls.add(6);
        previousRolls.add(0);
        previousRolls.add(4);

        EasyMock.expect(random.nextInt(6)).andReturn(3);
        EasyMock.expect(random.nextInt(6)).andReturn(0);
        EasyMock.expect(random.nextInt(6)).andReturn(2);
        EasyMock.expect(random.nextInt(6)).andReturn(1);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(2, diceValue);
        EasyMock.verify(random);

    }

    @Test
    public void rollUnique_With4elementAndGenerate4Duplicate_Expect2(){
        Random random = EasyMock.createMock(Random.class);
        ArrayList<Integer> previousRolls = new ArrayList<Integer>();
        previousRolls.add(1);
        previousRolls.add(3);
        previousRolls.add(6);
        previousRolls.add(0);
        previousRolls.add(4);

        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(0);
        EasyMock.expect(random.nextInt(6)).andReturn(2);
        EasyMock.expect(random.nextInt(6)).andReturn(3);
        EasyMock.expect(random.nextInt(6)).andReturn(1);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        int diceValue = dice.rollUnique(previousRolls);

        assertEquals(2, diceValue);
        EasyMock.verify(random);

    }


    @Test
    public void rollDice_WithNullRandomObject_ExpectException(){
        Dice dice = new Dice(null);
        String expectedMessage = "Passed in a Null random object";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    dice.rollDice(3);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void rollDice_WithOneDice_ExpectArraySize1(){
        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(random.nextInt(6)).andStubReturn(1);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        ArrayList<Integer> diceValues = dice.rollDice(1);

        assertEquals(1, diceValues.size());
        EasyMock.verify(random);

    }

    @Test
    public void rollDice_WithTwoDice_ExpectArraySize2(){
        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(random.nextInt(6)).andStubReturn(0);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        ArrayList<Integer> diceValues = dice.rollDice(2);

        assertEquals(2, diceValues.size());
        EasyMock.verify(random);

    }

    @Test
    public void rollDice_WithThreeDice_ExpectArraySize3(){
        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(random.nextInt(6)).andStubReturn(0);

        EasyMock.replay(random);

        Dice dice = new Dice(random);
        ArrayList<Integer> diceValues = dice.rollDice(3);

        assertEquals(3, diceValues.size());
        EasyMock.verify(random);
    }

    @Test
    public void rollDice_WithFourDice_ExpectException(){
        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(random.nextInt(6)).andStubReturn(0);

        EasyMock.replay(random);
        Dice dice = new Dice(random);

        String expectedMessage = "Rolling too much dice";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    dice.rollDice(4);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void rollDice_WithZeroDice_ExpectException(){
        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(random.nextInt(6)).andStubReturn(0);

        EasyMock.replay(random);
        Dice dice = new Dice(random);

        String expectedMessage = "Not rolling enough dice";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    dice.rollDice(0);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }
}
