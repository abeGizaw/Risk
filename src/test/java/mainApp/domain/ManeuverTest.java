package mainApp.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ManeuverTest {
    private final ResourceBundle spanishBundle = ResourceBundle.getBundle("message_es_ES");
    private final ResourceBundle englishBundle = ResourceBundle.getBundle("message");

    private Set<Territory> createAdjacentTerritories(int numberOfTerritories, int adjacentIndex, Player player) {
        Set<Territory> adjacentTerritories = new HashSet<>();
        for (int i = 0; i < numberOfTerritories; i++) {
            Territory adjacentTerritory = EasyMock.createStrictMock(Territory.class);
            adjacentTerritories.add(adjacentTerritory);
        }
        int count = 0;
        for (Territory territory: adjacentTerritories) {
            if (count < adjacentIndex) {
                EasyMock.expect(player.ownsTerritory(territory)).andReturn(false);
            } else if (count == adjacentIndex) {
                EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
            }
            count++;
            EasyMock.replay(territory);
        }
        return adjacentTerritories;
    }

    private void createMappingsBetweenTerritories(Territory[] territories, Player player) throws IOException {
        String territoryPointsFilePath = "src/main/java/data/ManeuverTestTerritoryMappings.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryPointsFilePath), Charset.defaultCharset()));
            String territoryMappings;
            while ((territoryMappings = br.readLine()) != null) {
                String[] territoryIndicis = territoryMappings.split(", ");
                int territoryIndex = Integer.parseInt(territoryIndicis[0]) - 1;
                Territory currentTerritory = territories[territoryIndex];
                Set<Territory> adjacentTerritories = new HashSet<>();
                for (int i = 1; i < territoryIndicis.length; i++) {
                    int adjacentTerritoryIndex = Integer.parseInt(territoryIndicis[i]) - 1;
                    Territory adjacentTerritory = territories[adjacentTerritoryIndex];
                    adjacentTerritories.add(adjacentTerritory);
                }
                EasyMock.expect(currentTerritory.getAdjacentTerritories()).andStubReturn(adjacentTerritories);
            }
            br.close();
        } catch (
                IOException e) {
            throw new IOException("Cannot find this file." + territoryPointsFilePath);
        }
    }

    private Territory[] initializeTerritoriesState(Player player) {
        int[] ownedTerritories = {1, 2, 3, 4, 5, 6, 9, 11, 12, 13, 16, 17, 18, -1};
        Territory[] mockedTerritories = new Territory[19];
        int ownedTerritoryIndex = 0;
        for (int i = 0; i < 19; i++) {
            Territory territory = EasyMock.mock(Territory.class);
            if (i == ownedTerritories[ownedTerritoryIndex] - 1) {
                ownedTerritoryIndex++;
                EasyMock.expect(player.ownsTerritory(territory)).andStubReturn(true);
            } else {
                EasyMock.expect(player.ownsTerritory(territory)).andStubReturn(false);
            }
            EasyMock.expect(territory.getCurrentNumberOfTroops()).andStubReturn(2);
            mockedTerritories[i] = territory;
        }
        return mockedTerritories;
    }

    private void validateManeuverTo(int territoryFromIndex, int territoryToIndex, boolean expectedResult) throws IOException {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.mock(Player.class);
        Territory[] territories = initializeTerritoriesState(player);
        createMappingsBetweenTerritories(territories, player);
        Territory territoryFrom = territories[territoryFromIndex];
        Territory territoryTo = territories[territoryToIndex];
        EasyMock.replay(player);
        for (Territory territory: territories) {
            EasyMock.replay(territory);
        }

        maneuverUnderTest.validateManeuverFrom(player, territoryFrom);
        boolean validationTo = maneuverUnderTest.validateManeuverTo(territoryTo);

        assertEquals(expectedResult, validationTo);
    }

    public void testManeuverTroopAmountHelper(int amountToManeuver, int amountOnManeuveringFromTerritory) {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Territory maneuveringFromTerritory = EasyMock.createStrictMock(Territory.class);
        Territory maneuveringToTerritory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(maneuveringFromTerritory.getCurrentNumberOfTroops()).andReturn(amountOnManeuveringFromTerritory);
        maneuveringFromTerritory.removeFromCurrentTroops(amountToManeuver);
        maneuveringToTerritory.addAdditionalTroops(amountToManeuver);
        EasyMock.replay(maneuveringFromTerritory);
        EasyMock.replay(maneuveringToTerritory);
        maneuverUnderTest.setManeuveringFrom(maneuveringFromTerritory);
        maneuverUnderTest.setManeuveringTo(maneuveringToTerritory);
        maneuverUnderTest.maneuverTroopAmount(amountToManeuver);
        EasyMock.verify(maneuveringFromTerritory);
        EasyMock.verify(maneuveringToTerritory);
    }

    @Test
    public void testValidateManeuverFrom_withNullPlayer_expectingNullPointerException() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        String expectedErrorMessage = "Player can not be null.";
        Territory territory = EasyMock.createStrictMock(Territory.class);

        Exception exception = assertThrows(NullPointerException.class, ()-> {
            maneuverUnderTest.validateManeuverFrom(null, territory);
        });
        String actualErrorMessage = exception.getMessage();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateManeuverFrom_withNullTerritory_expectingNullPointerException() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        String expectedErrorMessage = "Territory can not be null.";
        Player player = EasyMock.createStrictMock(Player.class);

        Exception exception = assertThrows(NullPointerException.class, ()-> {
            maneuverUnderTest.validateManeuverFrom(player, null);
        });
        String actualErrorMessage = exception.getMessage();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateManeuverFrom_withTerritoryHavingOneTroopWithTwoAdjacentTerritories_expectingFalse() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(1);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        boolean validationResult = maneuverUnderTest.validateManeuverFrom(player, territory);

        assertFalse(validationResult);
        EasyMock.verify(player);
        EasyMock.verify(territory);
    }

    @Test
    public void testValidateManeuverFrom_withTerritoryHavingTwoTroopsWithThreeAdjacentTerritoriesOneOwnedByPlayer_expectingTrue() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        Set<Territory> adjacentTerritories = createAdjacentTerritories(3, 2, player);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(2);
        EasyMock.expect(territory.getAdjacentTerritories()).andReturn(adjacentTerritories);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        boolean validationResult = maneuverUnderTest.validateManeuverFrom(player, territory);

        assertTrue(validationResult);
        EasyMock.verify(player);
        EasyMock.verify(territory);
        for (Territory adjacentTerritory: adjacentTerritories) {
            EasyMock.verify(adjacentTerritory);
        }
    }

    @Test
    public void testValidateManeuverFrom_withPlayerNotOwningTerritory_expectingIllegalArgumentException() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        String expectedErrorMessage = "When maneuvering, you must select a territory you control.";
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(false);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
           maneuverUnderTest.validateManeuverFrom(player, territory);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        EasyMock.verify(player);
        EasyMock.verify(territory);
    }

    @Test
    public void testValidateManeuverFrom_withTerritoryHavingMaxIntTroopsWithSixAdjacentTerritoriesNoneOwnedByPlayer_expectingFalse() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        Set<Territory> adjacentTerritories = createAdjacentTerritories(6, 6, player);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(Integer.MAX_VALUE);
        EasyMock.expect(territory.getAdjacentTerritories()).andReturn(adjacentTerritories);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        boolean validationResult = maneuverUnderTest.validateManeuverFrom(player, territory);

        assertFalse(validationResult);
        EasyMock.verify(player);
        EasyMock.verify(territory);
        for (Territory adjacentTerritory: adjacentTerritories) {
            EasyMock.verify(adjacentTerritory);
        }
    }

    @Test
    public void testValidateManeuverFrom_withTerritoryHaving13TroopsWithFourAdjacentTerritoriesNoneOwnedByPlayer_expectingFalse() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        Set<Territory> adjacentTerritories = createAdjacentTerritories(4, 4, player);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(13);
        EasyMock.expect(territory.getAdjacentTerritories()).andReturn(adjacentTerritories);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        boolean validationResult = maneuverUnderTest.validateManeuverFrom(player, territory);

        assertFalse(validationResult);
        EasyMock.verify(player);
        EasyMock.verify(territory);
        for (Territory adjacentTerritory: adjacentTerritories) {
            EasyMock.verify(adjacentTerritory);
        }
    }

    @Test
    public void testValidateManeuverFrom_withTerritoryHavingSevenTroopsWithFourAdjacentTerritoriesThreeOwnedByPlayer_expectingTrue() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        Set<Territory> adjacentTerritories = createAdjacentTerritories(4, 0, player);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(13);
        EasyMock.expect(territory.getAdjacentTerritories()).andReturn(adjacentTerritories);
        EasyMock.replay(player);
        EasyMock.replay(territory);

        boolean validationResult = maneuverUnderTest.validateManeuverFrom(player, territory);

        assertTrue(validationResult);
        EasyMock.verify(player);
        EasyMock.verify(territory);
        for (Territory adjacentTerritory: adjacentTerritories) {
            EasyMock.verify(adjacentTerritory);
        }
    }

    @Test
    public void testValidateManeuverTo_withNullTerritory_expectingNullPointerException() {
        Maneuver maneuverUnderTest = new Maneuver(spanishBundle);
        String expectedErrorMessage = "El territorio no puede ser nulo.";

        Exception exception = assertThrows(NullPointerException.class, ()-> {
            maneuverUnderTest.validateManeuverTo(null);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    public void testValidateManeuverTo_withTerritoryNotOwnedByPlayer_expectingIllegalArgumentException() {
        Maneuver maneuverUnderTest = new Maneuver(spanishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territoryFrom = EasyMock.createStrictMock(Territory.class);
        Territory territoryTo = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(player.ownsTerritory(territoryFrom)).andReturn(true);
        Set<Territory> adjacentTerritories = createAdjacentTerritories(3, 2, player);
        EasyMock.expect(territoryFrom.getCurrentNumberOfTroops()).andReturn(2);
        EasyMock.expect(territoryFrom.getAdjacentTerritories()).andReturn(adjacentTerritories);
        EasyMock.expect(player.ownsTerritory(territoryTo)).andReturn(false);
        EasyMock.replay(player);
        EasyMock.replay(territoryFrom);
        EasyMock.replay(territoryTo);
        String expectedErrorMessage = "Al maniobrar, debes seleccionar un territorio que controles.";

        maneuverUnderTest.validateManeuverFrom(player, territoryFrom);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            maneuverUnderTest.validateManeuverTo(territoryTo);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
        EasyMock.verify(player);
        EasyMock.verify(territoryFrom);
        EasyMock.verify(territoryTo);
        for (Territory adjacentTerritory: adjacentTerritories) {
            EasyMock.verify(adjacentTerritory);
        }
    }

    @Test
    public void testValidateManeuverTo_withTerritoryDirectlyAdjacentToManeuveringFromTerritory_expectingTrue() throws IOException {
        validateManeuverTo(12, 15, true);
    }

    @Test
    public void testValidateManeuverTo_withTerritoryOneTerritoryAwayToManeuveringFromTerritory_expectingTrue() throws IOException {
        validateManeuverTo(4, 5, true);
    }

    @Test
    public void testValidateManeuverTo_withTerritoryOneNotConnectedToManeuveringFromTerritory_expectingFalse() throws IOException {
        validateManeuverTo(4, 10, false);
    }

    @Test
    public void testValidateManeuverTo_withTerritoryConnectedWithManeuveringFromTerritoryThroughThreeTerritories_expectingTrue() throws IOException {
        validateManeuverTo(12, 11, true);
        validateManeuverTo(11, 12, true);
    }

    @Test
    public void testValidateManeuverTo_withTerritoryConnectedWithManeuveringFromTerritoryThrough11Territories_expectingTrue() throws IOException {
        validateManeuverTo(12, 5, true);
        validateManeuverTo(5, 12, true);
    }

    @Test
    public void testValidateManeuverTo_withSameTerritoryAsManeuverFrom_expectingIllegalStateArgument() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        Player player = EasyMock.createStrictMock(Player.class);
        Territory territory = EasyMock.createStrictMock(Territory.class);
        Territory adjacentTerritory = EasyMock.createStrictMock(Territory.class);
        Set<Territory> adjacentTerritorySet = new HashSet<>();
        adjacentTerritorySet.add(adjacentTerritory);
        EasyMock.expect(player.ownsTerritory(territory)).andReturn(true);
        EasyMock.expect(territory.getCurrentNumberOfTroops()).andReturn(2);
        EasyMock.expect(territory.getAdjacentTerritories()).andReturn(adjacentTerritorySet);
        EasyMock.expect(player.ownsTerritory(adjacentTerritory)).andReturn(true);
        String expectedErrorMessage = "When maneuvering, can not maneuver from and to the same territory.";

        EasyMock.replay(player);
        EasyMock.replay(territory);
        EasyMock.replay(adjacentTerritory);

        maneuverUnderTest.validateManeuverFrom(player, territory);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            maneuverUnderTest.validateManeuverTo(territory);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
        EasyMock.verify(player);
        EasyMock.verify(territory);
        EasyMock.verify(adjacentTerritory);
    }

    @Test
    public void testManeuverTroopAmount_withZero_expectingIllegalArgumentException() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        String expectedErrorMessage = "When maneuvering, you must maneuver a positive number of troops.";

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            maneuverUnderTest.maneuverTroopAmount(0);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testManeuverTroopAmount_withSameAmountOfTroopsThatTerritoryHas_expectingIllegalArgumentException() {
        Maneuver maneuverUnderTest = new Maneuver(englishBundle);
        String expectedErrorMessage = "When maneuvering, you must have at least one troop remaining on the territory you are maneuvering from.";
        Territory maneuveringFromTerritory = EasyMock.createStrictMock(Territory.class);
        EasyMock.expect(maneuveringFromTerritory.getCurrentNumberOfTroops()).andReturn(2);
        EasyMock.replay(maneuveringFromTerritory);
        maneuverUnderTest.setManeuveringFrom(maneuveringFromTerritory);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            maneuverUnderTest.maneuverTroopAmount(2);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
        EasyMock.verify(maneuveringFromTerritory);
    }

    @Test
    public void testManeuverTroopAmount_with1AndTerritoryWith2Troops() {
        testManeuverTroopAmountHelper(1, 2);
    }

    @Test
    public void testManeuverTroopAmount_with2AndTerritoryWith3Troops() {
        testManeuverTroopAmountHelper(2, 3);
    }

    @Test
    public void testManeuverTroopAmount_withMaxIntMinusOneAndTerritoryWithMaxIntTroops() {
        testManeuverTroopAmountHelper(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
    }

    @Test
    public void testManeuverTroopAmount_with7AndTerritoryWith10Troops() {
        testManeuverTroopAmountHelper(7, 10);
    }

    @Test
    public void testManeuverTroopAmount_with10AndTerritoryWith20Troops() {
        testManeuverTroopAmountHelper(10, 20);
    }
}
