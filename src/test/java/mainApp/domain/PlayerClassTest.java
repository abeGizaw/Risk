package mainApp.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerClassTest {
    Color playerColor = Color.RED;
    ArrayList<Card> emptyCards = new ArrayList<>();

    @Test
    public void addDeployableTroops_GivenTwo_ShouldThrowException() {
        Player player = new Player(3, playerColor, emptyCards);
        String expectedMessage = "Adding too few of troops";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.addDeployableTroops(2);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void addDeployableTroops_GivenThree_InitiallyThree_ShouldPass() {
        int expected = 6;
        Player player = new Player(3, playerColor, emptyCards);

        player.addDeployableTroops(3);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void addDeployableTroops_GivenMultiple_InitiallyThree_ShouldPass() {
        int expected = 12;
        Player player = new Player(3, playerColor, emptyCards);

        player.addDeployableTroops(3);
        player.addDeployableTroops(3);
        player.addDeployableTroops(3);

        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void addDeployableTroops_WithThree_InitiallyZero_ShouldPass() {
        int expected = 3;
        Player player = new Player(3, playerColor, emptyCards);
        player.removeDeployableTroops(3);
        player.addDeployableTroops(3);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void addDeployableTroops_WithMAX_InitiallyZero_ShouldPass() {
        int expected = Integer.MAX_VALUE;
        Player player = new Player(3, playerColor, emptyCards);
        player.removeDeployableTroops(3);
        player.addDeployableTroops(Integer.MAX_VALUE);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void addDeployableTroops_WithThree_InitiallyMAX_ShouldPass() {
        int expected = Integer.MAX_VALUE;
        Player player = new Player(Integer.MAX_VALUE - 3, playerColor, emptyCards);
        player.addDeployableTroops(3);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void removeDeployableTroops_With4_Initially3_ShouldThrowException() {
        Player player = new Player(3, playerColor, emptyCards);
        String expectedMessage = "Trying to remove too many troops";

        Exception thrown = Assertions.assertThrows(
                ArithmeticException.class,
                () -> {
                    player.removeDeployableTroops(4);
                }, "Arithmetic Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void removeDeployableTroops_WithNegativeNumber_ShouldThrowException() {
        Player player = new Player(3, playerColor, emptyCards);
        String expectedMessage = "Trying to remove a negative number of troops";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.removeDeployableTroops(-1);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void removeDeployableTroops_WithOne_InitiallyThree_ShouldPass() {
        int expected = 2;
        Player player = new Player(3, playerColor, emptyCards);
        player.removeDeployableTroops(1);
        assertEquals(player.getDeployableTroops(), expected);
    }
    @Test
    public void removeDeployableTroops_WithZero_InitiallyThree_ShouldPass() {
        int expected = 3;
        Player player = new Player(3, playerColor, emptyCards);
        player.removeDeployableTroops(0);
        assertEquals(player.getDeployableTroops(), expected);
    }
    @Test
    public void removeDeployableTroops_WithMAX_InitiallyMAX_ShouldPass() {
        int expected = 0;
        Player player = new Player(Integer.MAX_VALUE, playerColor, emptyCards);
        player.removeDeployableTroops(Integer.MAX_VALUE);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void removeDeployableTroops_WithMultipleCalls_InitiallyThree_ShouldPass() {
        int expected = 0;
        Player player = new Player(3, playerColor, emptyCards);
        player.removeDeployableTroops(1);
        player.removeDeployableTroops(1);
        player.removeDeployableTroops(1);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void removeAndAddDeployableTroops_WithMultipleCalls_InitiallyTen_ShouldPass() {
        int expected = 9;
        Player player = new Player(14, playerColor, emptyCards);
        player.removeDeployableTroops(3);
        player.removeDeployableTroops(4);
        player.addDeployableTroops(3);
        player.removeDeployableTroops(1);
        assertEquals(player.getDeployableTroops(), expected);
    }

    @Test
    public void checkForNullTerritory_ShouldThrowException() {
        Player player = new Player(3, playerColor, emptyCards);
        String expectedMessage = "Checking for Null Territory";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.ownsTerritory(null);
                }, "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void checkForValidTerritory_PlayerHasEmptySet_ExpectFalse() {
        Player player = new Player(3, playerColor, emptyCards);

        Territory Japan = EasyMock.mock(Territory.class);
        EasyMock.replay(Japan);

        boolean actual = player.ownsTerritory(Japan);
        EasyMock.verify(Japan);

        assertFalse(actual);
    }

    @Test
    public void checkForValidTerritory_PlayerHasOneTerritory_ExpectFalse() {
        Player player = new Player(3, playerColor, emptyCards);

        Territory Japan = EasyMock.mock(Territory.class);
        Territory Madagascar = EasyMock.mock(Territory.class);
        EasyMock.replay(Japan);
        EasyMock.replay(Madagascar);

        player.addTerritory(Madagascar);
        boolean actual = player.ownsTerritory(Japan);
        EasyMock.verify(Japan);
        EasyMock.verify(Madagascar);
        assertFalse(actual);
    }

    @Test
    public void checkForValidTerritory_PlayerHasOneTerritory_ExpectTrue() {
        Player player = new Player(3, playerColor, emptyCards);

        Territory Japan = EasyMock.mock(Territory.class);
        EasyMock.replay(Japan);

        player.addTerritory(Japan);
        boolean actual = player.ownsTerritory(Japan);
        EasyMock.verify(Japan);

        assertTrue(actual);
    }

    @Test
    public void checkForValidTerritory_PlayerHasMultipleTerritories_ExpectFalse() {
        Player player = new Player(3, playerColor, emptyCards);

        Territory Japan = EasyMock.mock(Territory.class);
        Territory Madagascar = EasyMock.mock(Territory.class);
        Territory Egypt = EasyMock.mock(Territory.class);
        EasyMock.replay(Japan);
        EasyMock.replay(Madagascar);
        EasyMock.replay(Egypt);

        player.addTerritory(Madagascar);
        player.addTerritory(Egypt);

        boolean actual = player.ownsTerritory(Japan);
        EasyMock.verify(Japan);
        EasyMock.verify(Madagascar);
        EasyMock.verify(Egypt);
        assertFalse(actual);
    }

    @Test
    public void checkForValidTerritory_PlayerHasMultipleTerritories_ExpectTrue() {
        Player player = new Player(3, playerColor, emptyCards);

        Territory Madagascar = EasyMock.mock(Territory.class);
        Territory Egypt = EasyMock.mock(Territory.class);

        EasyMock.replay(Madagascar);
        EasyMock.replay(Egypt);

        player.addTerritory(Madagascar);
        player.addTerritory(Egypt);

        boolean actual = player.ownsTerritory(Madagascar);
        EasyMock.verify(Madagascar);
        EasyMock.verify(Egypt);
        assertTrue(actual);
    }

    @Test
    public void addTerritory_NullTerritoryPlayerHasNoTerritories_NullPointerException() {
        Player player = new Player(0, playerColor, emptyCards);
        String expectedMessage = "Territory was null. Please enter a valid Territory.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    player.addTerritory(null);
                }, "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());


    }

    @Test
    public void addTerritory_NullTerritoryPlayerHasOneTerritory_NullPointerException() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory territory = EasyMock.createMock(Territory.class);

        EasyMock.replay(territory);
        player.addTerritory(territory);
        String expectedMessage = "Territory was null. Please enter a valid Territory.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    player.addTerritory(null);
                }, " Null Pointer Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
        EasyMock.verify(territory);


    }

    @Test
    public void addTerritory_ValidTerritoryHasNoTerritory_ExpectSizeOne() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory territory = EasyMock.createMock(Territory.class);
        EasyMock.replay(territory);

        player.addTerritory(territory);

        assertEquals(1, player.territoryCount());
        EasyMock.verify(territory);
    }

    @Test
    public void addTerritory_ValidTerritoryHasOneTerritory_ExpectSizeTwo() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory territory = EasyMock.createMock(Territory.class);
        Territory territory2 = EasyMock.createMock(Territory.class);
        player.addTerritory(territory);

        EasyMock.replay(territory);
        EasyMock.replay(territory2);

        player.addTerritory(territory2);

        assertEquals(2, player.territoryCount());
        EasyMock.verify(territory);
        EasyMock.verify(territory2);
    }

    @Test
    public void addTerritory_ValidTerritoryHasFortyOneTerritory_ExpectSizeFortyTwo() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory newTerritory = EasyMock.mock(Territory.class);
        Territory[] territories = new Territory[42];
        for (int i=0; i<41; i++){
            territories[i] = EasyMock.createMock(Territory.class);
            player.addTerritory(territories[i]);
            EasyMock.replay(territories[i]);
        }
        assertEquals(41, player.territoryCount());
        EasyMock.replay(newTerritory);
        player.addTerritory(newTerritory);

        assertEquals(42, player.territoryCount());
        for (int i=0; i<41; i++){
            EasyMock.verify(territories[i]);
        }
        EasyMock.verify(newTerritory);
    }

    @Test
    public void addTerritory_ValidTerritoryHasFortyTwoTerritory_IllegalStateException() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory newTerritory = EasyMock.mock(Territory.class);
        Territory[] territories = new Territory[42];
        for (int i=0; i<42; i++){
            territories[i] = EasyMock.createMock(Territory.class);
            player.addTerritory(territories[i]);
            EasyMock.replay(territories[i]);
        }
        assertEquals(42, player.territoryCount());
        String expectedMessage = "Player already contains 42 territories or more.";

        EasyMock.replay(newTerritory);

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    player.addTerritory(newTerritory);
                }, "Illegal State Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());



        assertEquals(42, player.territoryCount());
        for (int i=0; i<41; i++){
            EasyMock.verify(territories[i]);
        }
        EasyMock.verify(newTerritory);
    }

    @Test
    public void removeTerritory_NullTerritoryPlayerHasOneTerritories_NullPointerException() {
        Player player = new Player(0, playerColor, emptyCards);
        String expectedMessage = "Territory was null. Please enter a valid Territory to remove.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    player.removeTerritory(null);
                }, "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());

    }


    @Test
    public void removeTerritory_TerritoryPlayerHasNoTerritories_RuntimeException() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory territory = EasyMock.mock(Territory.class);
        String expectedMessage = "The player does not control any territories.";
        EasyMock.replay(territory);

        Exception thrown = Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    player.removeTerritory(territory);
                }, "Runtime Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
        EasyMock.verify(territory);
    }

    @Test
    public void removeTerritory_TerritoryNotControlledPlayerHasOneTerritories_IllegalArgumentException() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory Japan = EasyMock.mock(Territory.class);
        Territory Egypt = EasyMock.mock(Territory.class);
        player.addTerritory(Japan);
        String expectedMessage = "The player does not control this territory.";

        EasyMock.replay(Japan);
        EasyMock.replay(Egypt);

        Exception thrown = Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    player.removeTerritory(Egypt);
                }, "Runtime Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
        EasyMock.verify(Japan);
        EasyMock.verify(Egypt);
    }

    @Test
    public void removeTerritory_TerritoryControlledPlayerHasOneTerritories_ExpectSize0() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory Japan = EasyMock.mock(Territory.class);
        player.addTerritory(Japan);
        assertEquals(1, player.territoryCount());

        EasyMock.replay(Japan);

        player.removeTerritory(Japan);

        assertEquals(0, player.territoryCount());
        EasyMock.verify(Japan);
    }

    @Test
    public void removeTerritory_TerritoryControlledPlayerHasFourTerritories_ExpectSize3() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory Japan = EasyMock.mock(Territory.class);
        Territory Egypt = EasyMock.mock(Territory.class);
        Territory Russia = EasyMock.mock(Territory.class);
        Territory Argentina = EasyMock.mock(Territory.class);
        player.addTerritory(Japan);
        player.addTerritory(Egypt);
        player.addTerritory(Russia);
        player.addTerritory(Argentina);
        assertEquals(4, player.territoryCount());

        EasyMock.replay(Japan);
        EasyMock.replay(Egypt);
        EasyMock.replay(Russia);
        EasyMock.replay(Argentina);

        player.removeTerritory(Russia);

        assertEquals(3, player.territoryCount());
        EasyMock.verify(Japan);
        EasyMock.verify(Egypt);
        EasyMock.verify(Russia);
        EasyMock.verify(Argentina);
    }

    @Test
    public void removeTerritory_TerritoryNotControlledPlayerHasFourTerritories_IllegalArgumentException() {
        Player player = new Player(0, playerColor, emptyCards);
        Territory Japan = EasyMock.mock(Territory.class);
        Territory Egypt = EasyMock.mock(Territory.class);
        Territory Russia = EasyMock.mock(Territory.class);
        Territory Argentina = EasyMock.mock(Territory.class);
        Territory Brazil = EasyMock.mock(Territory.class);
        player.addTerritory(Japan);
        player.addTerritory(Egypt);
        player.addTerritory(Russia);
        player.addTerritory(Argentina);
        assertEquals(4, player.territoryCount());
        String expectedMessage = "The player does not control this territory.";

        EasyMock.replay(Japan);
        EasyMock.replay(Egypt);
        EasyMock.replay(Russia);
        EasyMock.replay(Argentina);
        EasyMock.replay(Brazil);

        Exception thrown = Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    player.removeTerritory(Brazil);
                }, "Runtime Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());

        EasyMock.verify(Japan);
        EasyMock.verify(Egypt);
        EasyMock.verify(Russia);
        EasyMock.verify(Argentina);
    }

    @Test
    public void territoryCount_PlayerHas0Territories_Expect0(){
        Player player = new Player(0, playerColor, emptyCards);
        int actual = player.territoryCount();
        assertEquals(0, actual);
    }

    @Test
    public void territoryCount_PlayerHas1Territory_Expect1(){
        Player player = new Player(0, playerColor, emptyCards);
        Territory territory = EasyMock.mock(Territory.class);
        player.addTerritory(territory);

        EasyMock.replay(territory);
        int actual = player.territoryCount();

        assertEquals(1, actual);
        EasyMock.verify(territory);
    }

    @Test
    public void territoryCount_PlayerHas42Territories_Expect42(){
        Player player = new Player(0, playerColor, emptyCards);
        Territory[] territories = new Territory[42];
        for (int i=0; i<42; i++){
            territories[i] = EasyMock.mock(Territory.class);
            EasyMock.replay(territories[i]);
            player.addTerritory(territories[i]);
        }

        int actual = player.territoryCount();

        assertEquals(42, actual);
        for (int i=0; i<42; i++){
            EasyMock.verify(territories[i]);
        }
    }
    @Test
    public void getDeployableTroops_0troops_Expect0(){
        Player player = new Player(0, playerColor, emptyCards);
        int actual = player.getDeployableTroops();
        assertEquals(0, actual);
    }
    @Test
    public void getDeployableTroops_1troopsConstructor0Set_Expect1(){
        Player player = new Player(1, playerColor, emptyCards);
        int actual = player.getDeployableTroops();
        assertEquals(1, actual);
    }

    @Test
    public void getDeployableTroops_0troopsConstructor3Set_Expect3(){
        Player player = new Player(0, playerColor, emptyCards);
        player.addDeployableTroops(3);

        int actual = player.getDeployableTroops();
        assertEquals(3, actual);
    }

    @Test
    public void getDeployableTroops_2troopsConstructorMAXINTminus3Set_ExpectMAXINTminus1(){
        Player player = new Player(2, playerColor, emptyCards);
        player.addDeployableTroops(Integer.MAX_VALUE-3);

        int actual = player.getDeployableTroops();
        assertEquals(Integer.MAX_VALUE-1, actual);
    }

    @Test
    public void getDeployableTroops_0troopsConstructorMAXINTminus1Set_ExpectMAXINTminus1(){
        Player player = new Player(0, playerColor, emptyCards);
        player.addDeployableTroops(Integer.MAX_VALUE-1);

        int actual = player.getDeployableTroops();
        assertEquals(Integer.MAX_VALUE-1, actual);
    }

    @Test
    public void getDeployableTroops_2troopsConstructorMAXINTminus2Set_ExpectMAXINT(){
        Player player = new Player(2, playerColor, emptyCards);
        player.addDeployableTroops(Integer.MAX_VALUE-2);

        int actual = player.getDeployableTroops();
        assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    public void getDeployableTroops_MAXINTtroopsConstructor0Set_ExpectMAXINT(){
        Player player = new Player(Integer.MAX_VALUE, playerColor, emptyCards);

        int actual = player.getDeployableTroops();
        assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    public void addCard_passingInANullCard_ExpectException(){
        Player player = new Player(20, playerColor, emptyCards);
        String expectedErrorMessage = "Passed in a Null Card";

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> {
            player.addCard(null);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void addCard_passingInAValidCard_ExpectAddCall(){
        ArrayList<Card> cards = EasyMock.mock(ArrayList.class);

        Card cardToAdd = EasyMock.mock(Card.class);
        EasyMock.expect(cards.add(cardToAdd)).andReturn(true);

        EasyMock.replay(cardToAdd);
        EasyMock.replay(cards);

        Player player = new Player(30, playerColor, cards);
        player.addCard(cardToAdd);

        EasyMock.verify(cardToAdd);
        EasyMock.verify(cards);
    }

    @Test
    public void removeCards_passInNullArrayList_ExpectException(){
        Player player = new Player(20, playerColor, emptyCards);
        String expectedErrorMessage = "Passed in a Null ArrayList of cards";

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    player.removeCards(null);
                });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void removeCards_passInListSizeOne_ExpectException(){
        ArrayList<Card> playerCards = EasyMock.mock(ArrayList.class);
        ArrayList<Card> cardsToRemove = EasyMock.mock(ArrayList.class);

        EasyMock.expect(cardsToRemove.size()).andReturn(1).times(2);

        EasyMock.replay(playerCards);
        EasyMock.replay(cardsToRemove);

        Player player = new Player(20, playerColor, playerCards);
        String expectedErrorMessage = "Trying to remove 1 card instead of 3";

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    player.removeCards(cardsToRemove);
                });

        assertEquals(exception.getMessage(), expectedErrorMessage);

        EasyMock.verify(playerCards);
        EasyMock.verify(cardsToRemove);
    }

    @Test
    public void removeCards_passInListSizeTwo_ExpectException(){
        ArrayList<Card> playerCards = EasyMock.mock(ArrayList.class);
        ArrayList<Card> cardsToRemove = EasyMock.mock(ArrayList.class);

        EasyMock.expect(cardsToRemove.size()).andReturn(2).times(2);


        EasyMock.replay(playerCards);
        EasyMock.replay(cardsToRemove);

        Player player = new Player(20, playerColor, playerCards);
        String expectedErrorMessage = "Trying to remove 2 card instead of 3";

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    player.removeCards(cardsToRemove);
                });

        assertEquals(exception.getMessage(), expectedErrorMessage);

        EasyMock.verify(playerCards);
        EasyMock.verify(cardsToRemove);
    }

    @Test
    public void removeCards_passInListSizeFour_ExpectException(){
        ArrayList<Card> playerCards = EasyMock.mock(ArrayList.class);
        ArrayList<Card> cardsToRemove = EasyMock.mock(ArrayList.class);

        EasyMock.expect(cardsToRemove.size()).andReturn(4).times(2);
        EasyMock.replay(playerCards);
        EasyMock.replay(cardsToRemove);

        Player player = new Player(20, playerColor, playerCards);
        String expectedErrorMessage = "Trying to remove 4 card instead of 3";

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    player.removeCards(cardsToRemove);
                });

        assertEquals(exception.getMessage(), expectedErrorMessage);

        EasyMock.verify(playerCards);
        EasyMock.verify(cardsToRemove);
    }

    @Test
    public void removeCards_passInListSizeThree_ExpectRemoveCall(){
        ArrayList<Card> playerCards = EasyMock.mock(ArrayList.class);
        ArrayList<Card> cardsToRemove = EasyMock.mock(ArrayList.class);

        EasyMock.expect(cardsToRemove.size()).andReturn(3);
        EasyMock.expect(playerCards.removeAll(cardsToRemove)).andReturn(true);

        EasyMock.replay(playerCards);
        EasyMock.replay(cardsToRemove);

        Player player = new Player(20, playerColor, playerCards);
        player.removeCards(cardsToRemove);


        EasyMock.verify(playerCards);
        EasyMock.verify(cardsToRemove);
    }



}
