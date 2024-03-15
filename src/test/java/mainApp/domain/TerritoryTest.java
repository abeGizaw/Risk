package mainApp.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class TerritoryTest {

    private Territory[] initializeTerritories(int numberOfTerritories) {
        Territory[] territoriesToReturn = new Territory[numberOfTerritories];
        for (int i = 0; i < numberOfTerritories; i++) {
            territoriesToReturn[i] = new Territory("Name", 0, 0, 0, 0);
        }
        return territoriesToReturn;
    }

    @Test
    public void testGetCurrentNumberOfTroops_withTerritoryContainingZeroTroops() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        int expected = 0;
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testAddAdditionalTroops_withOne_expectingOneTroop() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        int expected = 1;
        territoryUnderTest.addAdditionalTroops(1);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testAddAdditionalTroops_withOneToTerritoryContainingOne_expectingTwoTroops() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(1);
        assertEquals(1, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = 2;

        territoryUnderTest.addAdditionalTroops(1);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testAddAdditionalTroops_withMaxIntegerToTerritoryContainingZero_expectingMaxIntegerTroops() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        int expected = Integer.MAX_VALUE;

        territoryUnderTest.addAdditionalTroops(Integer.MAX_VALUE);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testAddAdditionalTroops_withNegativeValToTerritoryContainingZero_expectIllegalArgumentException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        String expectedErrorMessage = "Can not add a negative number of troops to a territory.";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            territoryUnderTest.addAdditionalTroops(-1);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testAddAdditionalTroops_withZeroToTerritoryContainingZero_expectingZero() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        int expected = 0;

        territoryUnderTest.addAdditionalTroops(0);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAdditionalTroops_withOneToTerritoryContainingMaxInteger_expectingArithmeticException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, territoryUnderTest.getCurrentNumberOfTroops());
        String expectedErrorMessage = "Adding troops will cause overflow.";

        Exception exception = assertThrows(ArithmeticException.class, () -> {
            territoryUnderTest.addAdditionalTroops(1);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
        assertEquals(0, expectedErrorMessage.compareTo(actualMessage));
    }

    @Test
    public void testRemoveCurrentTroops_withOneFromTerritoryContainingOne_expectingZero() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(1);
        assertEquals(1, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = 0;

        territoryUnderTest.removeFromCurrentTroops(1);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCurrentTroops_withOneFromTerritoryContainingMaxInteger_expectingMaxIntegerMinusOne() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = Integer.MAX_VALUE - 1;

        territoryUnderTest.removeFromCurrentTroops(1);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCurrentTroops_withMaxIntegerFromTerritoryContainingMaxInteger_expectingZero() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = 0;

        territoryUnderTest.removeFromCurrentTroops(Integer.MAX_VALUE);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCurrentTroops_withTwoFromTerritoryContainingThree_expectingOne() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(3);
        assertEquals(3, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = 1;

        territoryUnderTest.removeFromCurrentTroops(2);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCurrentTroops_withZeroFromTerritoryContainingZero_expectingIllegalArgumentException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        assertEquals(0, territoryUnderTest.getCurrentNumberOfTroops());
        int expected = 0;

        territoryUnderTest.removeFromCurrentTroops(0);
        int actual = territoryUnderTest.getCurrentNumberOfTroops();

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCurrentTroops_withEightFromTerritoryContainingSeven_expectingArithmeticException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(7);
        assertEquals(7, territoryUnderTest.getCurrentNumberOfTroops());
        String expectedErrorMessage = "Removing troops will result in a negative number of troops.";

        Exception exception = assertThrows(ArithmeticException.class, () -> {
            territoryUnderTest.removeFromCurrentTroops(8);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
        assertEquals(0, expectedErrorMessage.compareTo(actualMessage));
    }

    @Test
    public void testRemoveCurrentTroops_withNeg1RemovableTroops_expectingArithmeticException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        territoryUnderTest.addAdditionalTroops(7);
        assertEquals(7, territoryUnderTest.getCurrentNumberOfTroops());
        String expectedErrorMessage = "Can not remove a non positive number of troops from a territory.";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            territoryUnderTest.removeFromCurrentTroops(-1);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
        assertEquals(0, expectedErrorMessage.compareTo(actualMessage));
    }

    @Test
    public void testIsAdjacentTerritory_withTerritoryBeingCalledFrom_expectingFalse() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        boolean expected = false;

        boolean actual = territoryUnderTest.isAdjacentTerritory(territoryUnderTest);

        assertEquals(expected, actual);
    }

    @Test
    public void testIsAdjacentTerritory_withNullTerritory_expectingNullPointerException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        String expectedErrorMessage = "Territory can not be null for isAdjacentTerritory.";

        Exception exception = assertThrows(NullPointerException.class, () -> {
            territoryUnderTest.isAdjacentTerritory(null);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
        assertEquals(0, expectedErrorMessage.compareTo(actualMessage));
    }

    @Test
    public void TestSetAdjacentTerritories_withListContainingTwoTerritories() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory adjacentTerritoryOne = new Territory("Name", 0, 0, 0, 0);
        Territory adjacentTerritoryTwo = new Territory("Name", 0, 0, 0, 0);
        Territory nonAdjacentTerritory = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = new Territory[2];
        adjacentTerritories[0] = adjacentTerritoryOne;
        adjacentTerritories[1] = adjacentTerritoryTwo;

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritoryOne));
        assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritoryTwo));
        assertFalse(territoryUnderTest.isAdjacentTerritory(nonAdjacentTerritory));
    }

    @Test
    public void testSetAdjacentTerritories_withListOfSixTerritories() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = initializeTerritories(6);
        Territory[] nonAdjacentTerritories = initializeTerritories(3);

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        for (Territory adjacentTerritory : adjacentTerritories) {
            assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritory));
        }
        for (Territory nonAdjacentTerritory : nonAdjacentTerritories) {
            assertFalse(territoryUnderTest.isAdjacentTerritory(nonAdjacentTerritory));
        }
    }

    @Test
    public void testSetAdjacentTerritories_withListOfFiveTerritories() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = initializeTerritories(5);
        Territory[] nonAdjacentTerritories = initializeTerritories(1);

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        for (Territory adjacentTerritory : adjacentTerritories) {
            assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritory));
        }
        for (Territory nonAdjacentTerritory : nonAdjacentTerritories) {
            assertFalse(territoryUnderTest.isAdjacentTerritory(nonAdjacentTerritory));
        }
    }

    @Test
    public void testSetAdjacentTerritories_withListOfThreeTerritories() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = initializeTerritories(3);
        Territory[] nonAdjacentTerritories = initializeTerritories(3);

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        for (Territory adjacentTerritory : adjacentTerritories) {
            assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritory));
        }
        for (Territory nonAdjacentTerritory : nonAdjacentTerritories) {
            assertFalse(territoryUnderTest.isAdjacentTerritory(nonAdjacentTerritory));
        }
    }

    @Test
    public void testIsAdjacentTerritory_withTerritoryInAdjacencyList_expectingTrue() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = initializeTerritories(4);

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        assertTrue(territoryUnderTest.isAdjacentTerritory(adjacentTerritories[2]));
    }

    @Test
    public void testIsAdjacentTerritory_withTerritoryNonInAdjacencyList_expectingFalse() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Territory[] adjacentTerritories = initializeTerritories(4);
        Territory nonAdjacentTerritory = new Territory("Name", 0, 0, 0, 0);

        territoryUnderTest.setAdjacentTerritories(adjacentTerritories);

        assertFalse(territoryUnderTest.isAdjacentTerritory(nonAdjacentTerritory));
    }

    @Test
    public void testClickedOnTerritory_withPointNegativeOneZero_ExpectingIllegalArgumentException() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, 0, 0);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) -1);
        EasyMock.expect(pointClicked.getY()).andReturn((double) 0);
        EasyMock.replay(pointClicked);
        String expectedErrorMessage = "Coordinates of the point must be non negative.";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            territoryUnderTest.clickedOnTerritory(pointClicked);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void testClickedOnTerritory_withPointZeroZeroInHitbox_expectingTrue() {
        Territory territoryUnderTest = new Territory("Name", 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) 0);
        EasyMock.expect(pointClicked.getY()).andReturn((double) 0);
        EasyMock.replay(pointClicked);

        boolean actual = territoryUnderTest.clickedOnTerritory(pointClicked);

        assertTrue(actual);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void testClickedOnTerritory_withPointMaxIntMaxIntInHitbox_expectingTrue() {
        Territory territoryUnderTest = new Territory("Name", Integer.MAX_VALUE - 20, Integer.MAX_VALUE - 10, 20, 10);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) Integer.MAX_VALUE);
        EasyMock.expect(pointClicked.getY()).andReturn((double) Integer.MAX_VALUE);
        EasyMock.replay(pointClicked);

        boolean actual = territoryUnderTest.clickedOnTerritory(pointClicked);

        assertTrue(actual);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void testClickedOnTerritory_withPointFiftyOneThousandOutsideHitbox_expectingFalse() {
        Territory territoryUnderTest = new Territory("Name", 40, 60, 20, 40);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) 50);
        EasyMock.expect(pointClicked.getY()).andReturn((double) 1000);
        EasyMock.replay(pointClicked);

        boolean actual = territoryUnderTest.clickedOnTerritory(pointClicked);

        assertFalse(actual);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void testClickedOnTerritory_withPointOneHundredOneHundredOutsideHitbox_expectingFalse() {
        Territory territoryUnderTest = new Territory("Name", 49, 50, 50, 100);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) 100);
        EasyMock.expect(pointClicked.getY()).andReturn((double) 100);
        EasyMock.replay(pointClicked);

        boolean actual = territoryUnderTest.clickedOnTerritory(pointClicked);

        assertFalse(actual);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void testClickedOnTerritory_withPointMaxIntMaxIntInHitboxWithNoWidthAndHeight_expectingTrue() {
        Territory territoryUnderTest = new Territory("Name", Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        Point pointClicked = EasyMock.createMock(Point.class);
        EasyMock.expect(pointClicked.getX()).andReturn((double) Integer.MAX_VALUE);
        EasyMock.expect(pointClicked.getY()).andReturn((double) Integer.MAX_VALUE);
        EasyMock.replay(pointClicked);

        boolean actual = territoryUnderTest.clickedOnTerritory(pointClicked);

        assertTrue(actual);
        EasyMock.verify(pointClicked);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHas0Troops_expectException(){
        Territory testTerritory = new Territory("Name", 10, 10, 10, 10);
        String expectedMessage = "Territory does not have any troops";

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                ()->testTerritory.maxDeployableAttackTroops()
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHasOneTroop_expectZero(){
        maxDeployableTroopsAttackHelper(0, 1);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHasTwoTroops_expectOne(){
        maxDeployableTroopsAttackHelper(1, 2);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHasThreeTroops_expectTwo(){
        maxDeployableTroopsAttackHelper(2, 3);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHasFourTroops_expectThree(){
        maxDeployableTroopsAttackHelper(3, 4);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryAlmostHasMAXINTTroops_expectThree(){
        maxDeployableTroopsAttackHelper(3, Integer.MAX_VALUE - 1);
    }

    @Test
    public void maxDeployableTroopsAttack_territoryHasMAXINTTroops_expectThree(){
        maxDeployableTroopsAttackHelper(3, Integer.MAX_VALUE);
    }

    @Test
    public void maxDeployableAttackTroops_territoryHasFiveTroops_expectThree(){
        maxDeployableTroopsAttackHelper(3, 5);
    }



    public void maxDeployableTroopsAttackHelper(int expected, int numOfTroops){
        Territory testTerritory = new Territory("Name", 10, 10, 10, 10);
        testTerritory.addAdditionalTroops(numOfTroops);
        int actual = testTerritory.maxDeployableAttackTroops();
        assertEquals(expected, actual);
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasZeroTroops_expectException(){
        Territory testTerritory = new Territory("Name", 10, 10, 10, 10);
        String expectedMessage = "Territory does not have any troops";

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                testTerritory::maxDeployableDefendTroops
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasOneTroop_expectOne(){
        maxDeployableTroopsDefendHelper(1, 1);
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasTwoTroops_expectTwo(){
        maxDeployableTroopsDefendHelper(2, 2);
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasThreeTroops_expectTwo(){
        maxDeployableTroopsDefendHelper(2, 3);
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasMAXINTminusOne_expectTwo(){
        maxDeployableTroopsDefendHelper(2, Integer.MAX_VALUE - 1);
    }

    @Test
    public void maxDeployableDefendTroops_territoryHasMAXINTTroops_expectTwo(){
        maxDeployableTroopsDefendHelper(2, Integer.MAX_VALUE);
    }

    private void maxDeployableTroopsDefendHelper(int expected, int troopCount) {
        Territory testTerritory = new Territory("Name", 10, 10, 10, 10);
        testTerritory.addAdditionalTroops(troopCount);
        int actual = testTerritory.maxDeployableDefendTroops();
        assertEquals(expected, actual);
    }


}
