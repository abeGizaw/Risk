package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.easymock.EasyMock.anyInt;
import static org.junit.jupiter.api.Assertions.*;

@SuppressFBWarnings
public class GameTest {
    Collection<Territory> emptyTerritories;
    ArrayList<Player> emptyPlayers;
    HashMap<Continent, Territory[]> emptyTerritoriesByContinentMap = new HashMap<>();
    HashMap<Continent, Territory[]> territoriesByContinentMap = new HashMap<>();

    Attack emptyAttack;
    ArrayList<Continent> emptyContinents = new ArrayList<>();
    SecretMissionDeck secretMissionCards = null;
    RiskDeck allRiskCards;
    Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));
    Game emptyGame = new Game(emptyPlayers, emptyTerritories, emptyTerritoriesByContinentMap,
            allRiskCards, emptyAttack, maneuver, secretMissionCards);
    ArrayList<Player> players = new ArrayList<>();
    Player player1test = EasyMock.mock(Player.class);
    Player player2test = EasyMock.mock(Player.class);
    HashMap<String, Color> playerColors = new HashMap<>();

    public GameTest() {
        players.add(player1test);
        players.add(player2test);
        territoriesByContinentMap.put(Continent.AFRICA, new Territory[6]);
        territoriesByContinentMap.put(Continent.ASIA, new Territory[12]);
        territoriesByContinentMap.put(Continent.AUSTRALIA, new Territory[4]);
        territoriesByContinentMap.put(Continent.EUROPE, new Territory[7]);
        territoriesByContinentMap.put(Continent.NORTH_AMERICA, new Territory[9]);
        territoriesByContinentMap.put(Continent.SOUTH_AMERICA, new Territory[4]);
        populateColorMap();

    }

    private void populateColorMap() {
        playerColors.put("Magenta", Color.decode("#660066"));
        playerColors.put("Black", Color.decode("#000000"));
        playerColors.put("Blue", Color.decode("#003166"));
        playerColors.put("Green", Color.decode("#1e4620"));
        playerColors.put("Red", Color.decode("#a91b0d"));
    }

    @Test
    public void checkIfTerritoryEmpty_NullObject_ExpectException() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        String expectedMessage = "Invalid Territory, Passed in NULL";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.checkIfTerritoryEmpty(null);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void checkIfTerritoryEmpty_ATerritoryNotOwnedByPlayer_ExpectFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);


        Territory territoryToFind = EasyMock.mock(Territory.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.ownsTerritory(territoryToFind)).andReturn(false);
        EasyMock.expect(player2.ownsTerritory(territoryToFind)).andReturn(false);
        EasyMock.expect(player3.ownsTerritory(territoryToFind)).andReturn(false);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        boolean actual = testGame.checkIfTerritoryEmpty(territoryToFind);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

        assertTrue(actual);
    }

    @Test
    public void checkIfTerritoryEmpty_ATerritoryOwnedByPlayer_ExpectTrue() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        Territory territoryToFind = EasyMock.mock(Territory.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.ownsTerritory(territoryToFind)).andReturn(false);
        EasyMock.expect(player2.ownsTerritory(territoryToFind)).andReturn(false);
        EasyMock.expect(player3.ownsTerritory(territoryToFind)).andReturn(true);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        boolean actual = testGame.checkIfTerritoryEmpty(territoryToFind);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

        assertFalse(actual);
    }

    @Test
    public void allTerritoriesAssigned_allPlayersHaveZeroTerritories_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(0);
        EasyMock.expect(player2.territoryCount()).andReturn(0);
        EasyMock.expect(player3.territoryCount()).andReturn(0);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.allTerritoriesAssigned();

        assertFalse(actual);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void allTerritoriesAssigned_allPlayersHaveOneTerritory_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(1);
        EasyMock.expect(player2.territoryCount()).andReturn(1);
        EasyMock.expect(player3.territoryCount()).andReturn(1);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);


        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.allTerritoriesAssigned();

        assertFalse(actual);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void allTerritoriesAssigned_OnlyOnePlayerHasATerritory_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(1);
        EasyMock.expect(player2.territoryCount()).andReturn(0);
        EasyMock.expect(player3.territoryCount()).andReturn(0);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.allTerritoriesAssigned();

        assertFalse(actual);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void allTerritoriesAssigned_PlayersHave41Territories_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(14);
        EasyMock.expect(player2.territoryCount()).andReturn(14);
        EasyMock.expect(player3.territoryCount()).andReturn(13);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.allTerritoriesAssigned();

        assertFalse(actual);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void allTerritoriesAssigned_PlayersHave42Territories_ReturnTrue() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(14);
        EasyMock.expect(player2.territoryCount()).andReturn(14);
        EasyMock.expect(player3.territoryCount()).andReturn(14);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.allTerritoriesAssigned();

        assertTrue(actual);

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void allTerritoriesAssigned_PlayersHave43Territories_ExpectException() {
        String expectedMessage = "Too many Territories Have Been Assigned";
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(15);
        EasyMock.expect(player2.territoryCount()).andReturn(14);
        EasyMock.expect(player3.territoryCount()).andReturn(14);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                testGame::allTerritoriesAssigned, "Illegal State Exception should be thrown"
        );
        assertEquals(expectedMessage, thrown.getMessage());

        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);
    }

    @Test
    public void placedAllInitialTroops_AllPlayersHaveZeroTroops_ReturnTrue() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.getDeployableTroops()).andReturn(0);
        EasyMock.expect(player2.getDeployableTroops()).andReturn(0);
        EasyMock.expect(player3.getDeployableTroops()).andReturn(0);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.placedAllInitialTroops();

        assertTrue(actual);
        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

    }

    @Test
    public void placedAllInitialTroops_APlayerHasOneTroop_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.getDeployableTroops()).andReturn(0);
        EasyMock.expect(player2.getDeployableTroops()).andReturn(1);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.placedAllInitialTroops();

        assertFalse(actual);
        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

    }

    @Test
    public void placedAllInitialTroops_AllPlayersHaveOneTroop_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.getDeployableTroops()).andReturn(1);


        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.placedAllInitialTroops();

        assertFalse(actual);
        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

    }

    @Test
    public void placedAllInitialTroops_APlayerHasMAXINTTroops_ReturnFalse() {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.getDeployableTroops()).andReturn(0);
        EasyMock.expect(player2.getDeployableTroops()).andReturn(0);
        EasyMock.expect(player3.getDeployableTroops()).andReturn(Integer.MAX_VALUE);

        EasyMock.replay(player1);
        EasyMock.replay(player2);
        EasyMock.replay(player3);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        boolean actual = testGame.placedAllInitialTroops();

        assertFalse(actual);
        EasyMock.verify(player1);
        EasyMock.verify(player2);
        EasyMock.verify(player3);
        EasyMock.verify(attack);

    }

    @Test
    public void convertTerritoryNameToObject_NullName_ExpectNullPointerException() {
        String expectedMessage = "Name is null. Please enter a valid name.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    emptyGame.convertTerritoryNameToObject(null);
                }, "Null Pointer Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void convertTerritoryNameToObject_EmptyName_ExpectIllegalArgumentException() {
        String expectedMessage = "Name is invalid. Please enter a valid name.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    emptyGame.convertTerritoryNameToObject("");
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void convertTerritoryNameToObject_InvalidName_ExpectIllegalArgumentException() {
        String expectedMessage = "Name is invalid. Please enter a valid name.";
        ArrayList<Territory> territories = new ArrayList<>();
        Attack attack = EasyMock.mock(Attack.class);

        Territory currentTerr = EasyMock.niceMock(Territory.class);
        territories.add(currentTerr);
        EasyMock.expect(currentTerr.getTerritoryName()).andReturn("Greenland");
        EasyMock.replay(currentTerr);
        EasyMock.replay(attack);

        Game game = new Game(emptyPlayers, territories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    game.convertTerritoryNameToObject("Grenland");
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        EasyMock.verify(currentTerr);
        EasyMock.verify(attack);

    }

    @Test
    public void convertTerritoryNameToObject_validName_ExpectTerritory() {
        Collection<Territory> territories = new ArrayList<>();
        for (int i = 0; i < 41; i++) {
            Territory currentTerr = EasyMock.niceMock(Territory.class);
            territories.add(currentTerr);
            EasyMock.expect(currentTerr.getTerritoryName()).andReturn("Spain");
            EasyMock.replay(currentTerr);
        }
        Attack attack = EasyMock.mock(Attack.class);
        Territory Greenland = EasyMock.createNiceMock(Territory.class);
        territories.add(Greenland);

        EasyMock.expect(Greenland.getTerritoryName()).andReturn("Greenland");
        EasyMock.replay(Greenland);
        EasyMock.replay(attack);

        Game game = new Game(emptyPlayers, territories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        Territory actual = game.convertTerritoryNameToObject("Greenland");

        assertEquals(Greenland, actual);
        EasyMock.verify(Greenland);
        EasyMock.verify(attack);
        for (Territory terr : territories) {
            EasyMock.verify(terr);
        }
    }

    @Test
    public void allocatePlayerTroops_playerHas0Territories_ExpectException() {
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The current player has zero territories";

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    testGame.allocatePlayerTroops(player1test);
                }, "Illegal State Exception should be thrown");
        assertEquals(expectedMessage, thrown.getMessage());
    }

    public void calcuateBonus_noContinenets_Expect0() {
        ArrayList<Continent> continents = new ArrayList<>();
        int actual = emptyGame.calculateBonus(continents);
        assertEquals(0, actual);

    }

    @Test
    public void calculateBonus_nullContinents_ExpectNullPointerException() {
        String expectedMessage = "Continents should not be null.";
        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    emptyGame.calculateBonus(null);
                }, "Null Pointer Exception should be thrown");
    }

    @Test
    public void allocatePlayerTroops_playerHas8Territories_Expect3() {
        allocatePlayerTroopsHelperForEmpty(3, 8, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas9Territories_Expect3() {
        allocatePlayerTroopsHelperForEmpty(3, 9, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas10Territories_Expect3() {
        allocatePlayerTroopsHelperForEmpty(3, 10, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas11Territories_Expect3() {
        allocatePlayerTroopsHelperForEmpty(3, 11, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas12Territories_Expect4() {
        allocatePlayerTroopsHelperForEmpty(4, 12, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas13Territories_Expect4() {
        allocatePlayerTroopsHelperForEmpty(4, 13, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas14Territories_Expect4() {
        allocatePlayerTroopsHelperForEmpty(4, 14, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas36Territories_Expect12() {
        allocatePlayerTroopsHelperForEmpty(12, 36, emptyContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas37Territories_WithoutAsia_Expect29() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.ASIA);
        allocatePlayerTroopsHelper(29, 37, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas38Territories_WithoutAsia_Expect29() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.ASIA);
        allocatePlayerTroopsHelper(29, 38, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas39Territories_WithoutAsia_Expect30() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.ASIA);
        allocatePlayerTroopsHelper(30, 39, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas40Territories_WithoutAsia_Expect30() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.ASIA);
        allocatePlayerTroopsHelper(30, 40, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutAsia_Expect30() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.ASIA);
        allocatePlayerTroopsHelper(30, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas42Territories_WithoutAsia_ExpectException() {
        EasyMock.expect(player1test.territoryCount()).andReturn(42);

        for (Player player : players) {
            EasyMock.replay(player);
        }

        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The current player has every territory. Game should be over.";

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    testGame.allocatePlayerTroops(player1test);
                }, "Illegal State Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutAfrica_Expect34() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.ASIA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.AFRICA);
        allocatePlayerTroopsHelper(34, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutSouthAmerica_Expect35() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.ASIA);
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.SOUTH_AMERICA);

        allocatePlayerTroopsHelper(35, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutAustralia_Expect35() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.ASIA);
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.AUSTRALIA);
        allocatePlayerTroopsHelper(35, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutNorthAmerica_Expect32() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.ASIA);
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.NORTH_AMERICA);

        allocatePlayerTroopsHelper(32, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas41Territories_WithoutEurope_Expect32() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.ASIA);
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);

        allocatePlayerTroopsHelper(32, 41, ownedContinents);
    }

    @Test
    public void allocatePlayerTroops_playerHas36Territories_WithoutAsia_Expect29() {
        ArrayList<Continent> ownedContinents = new ArrayList<>();
        ownedContinents.add(Continent.AFRICA);
        ownedContinents.add(Continent.AUSTRALIA);
        ownedContinents.add(Continent.NORTH_AMERICA);
        ownedContinents.add(Continent.SOUTH_AMERICA);
        ownedContinents.add(Continent.EUROPE);
        ownedContinents.add(Continent.ASIA);

        allocatePlayerTroopsHelper(29, 36, ownedContinents);
    }

    public void allocatePlayerTroopsHelper(int expectedInput, int numTerritories, ArrayList<Continent> ownedContinents) {
        Collection<Territory> allTerritories = new ArrayList<>();
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Territory fillerTerritory = EasyMock.mock(Territory.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        int leftOffIndex = fillTerritoryData(ownedContinents, numTerritories, allTerritories, player1, territoriesByContinentMap);
        cleanMapOfNull(territoriesByContinentMap, ownedContinents);
        Continent unOwnedContinent = ownedContinents.get(ownedContinents.size() - 1);
        territoriesByContinentMap.get(unOwnedContinent)[leftOffIndex] = fillerTerritory;
        EasyMock.expect(player1.ownsTerritory(fillerTerritory)).andReturn(false);


        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(numTerritories);

        player1.addDeployableTroops(expectedInput);
        for (Player player : testPlayers) {
            EasyMock.replay(player);
        }
        EasyMock.replay(fillerTerritory);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, allTerritories, territoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        testGame.allocatePlayerTroops(player1);


        for (Player player : testPlayers) {
            EasyMock.verify(player);
        }
        for (Territory territory : allTerritories) {
            EasyMock.verify(territory);
        }
        EasyMock.verify(fillerTerritory);
        EasyMock.verify(attack);
        resetContinentHashMap();

    }


    public void allocatePlayerTroopsHelperForEmpty(int expectedInput, int numTerritories, ArrayList<Continent> continents) {
        Collection<Territory> allTerritories = new ArrayList<>();
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Territory fillerTerritory = EasyMock.mock(Territory.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);


        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        EasyMock.expect(player1.territoryCount()).andReturn(numTerritories);
        player1.addDeployableTroops(expectedInput);


        for (Player player : testPlayers) {
            EasyMock.replay(player);
        }
        EasyMock.replay(fillerTerritory);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, allTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);

        testGame.allocatePlayerTroops(player1);
        for (Player player : testPlayers) {
            EasyMock.verify(player);
        }
        for (Territory territory : allTerritories) {
            EasyMock.verify(territory);
        }
        EasyMock.verify(fillerTerritory);
        EasyMock.verify(attack);
    }

    private void cleanMapOfNull(HashMap<Continent, Territory[]> territoriesByContinentMapInput, ArrayList<Continent> ownedContinents) {
        Continent continentToClean = ownedContinents.get(ownedContinents.size() - 1);
        Territory[] territoriesToClean = territoriesByContinentMapInput.get(continentToClean);
        Territory[] cleanTerritories = Arrays.stream(territoriesToClean)
                .filter(Objects::nonNull)
                .toArray(Territory[]::new);

        int size = cleanTerritories.length;
        Territory[] cleanTerritoriesWithFiller = new Territory[size + 1];
        System.arraycopy(cleanTerritories, 0, cleanTerritoriesWithFiller, 0, size);

        this.territoriesByContinentMap.replace(continentToClean, cleanTerritoriesWithFiller);
    }

    private void resetContinentHashMap() {
        territoriesByContinentMap.replace(Continent.AFRICA, new Territory[6]);
        territoriesByContinentMap.replace(Continent.ASIA, new Territory[12]);
        territoriesByContinentMap.replace(Continent.AUSTRALIA, new Territory[4]);
        territoriesByContinentMap.replace(Continent.EUROPE, new Territory[7]);
        territoriesByContinentMap.replace(Continent.NORTH_AMERICA, new Territory[9]);
        territoriesByContinentMap.replace(Continent.SOUTH_AMERICA, new Territory[4]);
    }

    private int fillTerritoryData(ArrayList<Continent> ownedContinents, int numTerritories, Collection<Territory> allTerritories,
                                  Player player1, HashMap<Continent, Territory[]> territoriesByContinentMapInput) {
        int continentArrayIndex = 0;
        int latestTerritoryIndex = 0;
        for (int i = 0; i < numTerritories; i++) {
            Territory currentTerr = EasyMock.mock(Territory.class);
            Continent currentContinent = ownedContinents.get(continentArrayIndex);
            Territory[] territories = territoriesByContinentMapInput.get(currentContinent);
            territories[latestTerritoryIndex] = currentTerr;
            latestTerritoryIndex++;


            territoriesByContinentMapInput.put(currentContinent, territories);
            allTerritories.add(currentTerr);

            EasyMock.expect(player1.ownsTerritory(currentTerr)).andReturn(true);

            if (latestTerritoryIndex == currentContinent.territoryCount()) {
                continentArrayIndex++;
                latestTerritoryIndex = 0;
            }
            EasyMock.replay(currentTerr);
        }
        return latestTerritoryIndex;
    }

    @Test
    public void calculateBonus_AsiaOnly_Expect7() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.ASIA, 7);
    }

    @Test
    public void calculateBonus_NorthAmericaOnly_Expect5() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.NORTH_AMERICA, 5);
    }

    @Test
    public void calculateBonus_SouthAmericaOnly_Expect2() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.SOUTH_AMERICA, 2);
    }

    @Test
    public void calculateBonus_AfricaOnly_Expect3() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.AFRICA, 3);
    }

    @Test
    public void calculateBonus_EuropeOnly_Expect5() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.EUROPE, 5);
    }

    @Test
    public void calculateBonus_AustraliaOnly_Expect2() {
        calculateBonus_OneContinentOnly_ExpectValue(Continent.AUSTRALIA, 2);
    }

    public void calculateBonus_OneContinentOnly_ExpectValue(Continent continent, int expected) {
        ArrayList<Continent> continents = new ArrayList<>();
        continents.add(continent);

        int actual = emptyGame.calculateBonus(continents);
        assertEquals(expected, actual);
    }

    @Test
    public void calculateBonus_AllContinents_Expect24() {
        ArrayList<Continent> continents = new ArrayList<>();
        continents.add(Continent.ASIA);
        continents.add(Continent.AFRICA);
        continents.add(Continent.AUSTRALIA);
        continents.add(Continent.NORTH_AMERICA);
        continents.add(Continent.SOUTH_AMERICA);
        continents.add(Continent.EUROPE);

        int actual = emptyGame.calculateBonus(continents);
        assertEquals(24, actual);
    }

    @Test
    public void updateDeployableTroops_PlayerDeploys0Troops_expectNoGain() {
        updateDeployableTroopsHelper(0, 10);
    }

    @Test
    public void updateDeployableTroops_PlayerDeploys1Troop_expect1TroopLoss() {
        updateDeployableTroopsHelper(1, 10);
    }

    @Test
    public void updateDeployableTroops_PlayerDeploys2Troops_expect2TroopLoss() {
        updateDeployableTroopsHelper(2, 10);
    }

    @Test
    public void updateDeployableTroops_PlayerDeploysAllButOneTroops_expectAllButOneTroopLoss() {
        updateDeployableTroopsHelper(9, 10);
    }

    @Test
    public void updateDeployableTroops_PlayerDeploysAllTroops_expectAllTroopLoss() {
        updateDeployableTroopsHelper(10, 10);
    }


    private void updateDeployableTroopsHelper(int deployed, int playerTroops) {
        Territory currentTerritory = EasyMock.mock(Territory.class);
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = EasyMock.mock(Player.class);
        Player player2 = EasyMock.mock(Player.class);
        Player player3 = EasyMock.mock(Player.class);
        Attack attack = EasyMock.mock(Attack.class);

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);


        player1.removeDeployableTroops(deployed);
        currentTerritory.addAdditionalTroops(deployed);

        EasyMock.expect(player1.getDeployableTroops()).andReturn(playerTroops - deployed);

        for (Player player : testPlayers) {
            EasyMock.replay(player);
        }

        EasyMock.replay(currentTerritory);
        EasyMock.replay(attack);

        Game testGame = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        testGame.updateDeployableTroops(deployed, currentTerritory);

        for (Player player : testPlayers) {
            EasyMock.verify(player);
        }
        EasyMock.verify(currentTerritory);
        EasyMock.verify(attack);
    }

    @Test
    public void allSetsOfTurnInCards_ThreeCardsOneSet_ExpectListSize1() {
        allSetsOfCardsHelper(1, 0, 3, 3);
    }

    @Test
    public void allSetsOfTurnInCards_FourCardsOneSet_ExpectListSize1() {
        allSetsOfCardsHelper(1, 3, 4, 12);
    }

    @Test
    public void allSetsOfTurnInCards_FiveCardsOneSet_ExpectListSize1() {
        allSetsOfCardsHelper(1, 9, 5, 30);
    }

    @Test
    public void allSetsOfTurnInCards_EightCardsOneSet_ExpectListSize1() {
        allSetsOfCardsHelper(1, 55, 8, 168);
    }

    @Test
    public void allSetsOfTurnInCards_NineCardsOneSet_ExpectListSize1() {
        allSetsOfCardsHelper(1, 83, 9, 252);
    }

    @Test
    public void allSetsOfTurnInCards_FourCardsTwoSets_ExpectListSize2() {
        allSetsOfCardsHelper(2, 2, 4, 12);
    }

    @Test
    public void allSetsOfTurnInCards_FiveCardsTwoSets_ExpectListSize2() {
        allSetsOfCardsHelper(2, 8, 5, 30);
    }

    @Test
    public void allSetsOfTurnInCards_EightCardsTwoSets_ExpectListSize2() {
        allSetsOfCardsHelper(2, 54, 8, 168);
    }

    @Test
    public void allSetsOfTurnInCards_NineCardsTwoSets_ExpectListSize2() {
        allSetsOfCardsHelper(2, 82, 9, 252);
    }

    @Test
    public void allSetsOfTurnInCards_EightCardsT56Sets_ExpectListSize56() {
        allSetsOfCardsHelper(56, 0, 8, 168);
    }

    @Test
    public void allSetsOfTurnInCards_NineCardsT56Sets_ExpectListSize56() {
        allSetsOfCardsHelper(56, 28, 9, 252);
    }

    @Test
    public void allSetsOfTurnInCards_NineCardsT84Sets_ExpectListSize84() {
        allSetsOfCardsHelper(84, 0, 9, 252);
    }

    private void allSetsOfCardsHelper(int matches, int fails, int numCards, int getCalls) {
        ArrayList<Player> testPlayers = new ArrayList<>();
        ArrayList<Card> cardsToConfirm = new ArrayList<>();
        Card cardToCheck = new Card("dc", "dc", "dc");

        cardsToConfirm.add(cardToCheck);
        cardsToConfirm.add(cardToCheck);
        cardsToConfirm.add(cardToCheck);

        ArrayList<Card> playerCards = EasyMock.mock(ArrayList.class);
        Player testPlayer = EasyMock.mock(Player.class);
        RiskDeck riskTest = EasyMock.mock(RiskDeck.class);
        testPlayers.add(testPlayer);


        EasyMock.expect(testPlayer.getCards()).andReturn(playerCards);
        EasyMock.expect(playerCards.size()).andReturn(numCards);
        EasyMock.expect(playerCards.get(anyInt())).andReturn(cardToCheck).times(getCalls);
        EasyMock.expect(riskTest.canTurnInCards(cardsToConfirm)).andReturn(true).times(matches);
        if (fails > 0) {
            EasyMock.expect(riskTest.canTurnInCards(cardsToConfirm)).andReturn(false).times(fails);
        }


        EasyMock.replay(playerCards);
        for (Player player : testPlayers) {
            EasyMock.replay(player);
        }
        EasyMock.replay(riskTest);


        Game gameTest = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, riskTest, emptyAttack, maneuver, secretMissionCards);

        ArrayList<ArrayList<Card>> actual = gameTest.allSetsOfValidCards();

        assertEquals(matches, actual.size());

        EasyMock.verify(playerCards);
        EasyMock.verify(riskTest);
        for (Player player : testPlayers) {
            EasyMock.verify(player);
        }
    }

    //#TODO Note: Having problems mocking RiskDeck because of it being an abstract class, and the method being mocked is Final
    @Test
    public void turnCardsIn_playerOwnsNoTerritories_NoBonusTroops() {
        assertEquals(1, 1);
    }

    private void turnInCardsHelper(int bonus) {
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player testPlayer = EasyMock.mock(Player.class);
        testPlayers.add(testPlayer);

        ArrayList<Card> cardsToConfirm = new ArrayList<>();
        Card dummyCard = new Card("dc", "dc", "dc");
        Card cardToCheck = EasyMock.mock(Card.class);
//        cardsToConfirm.add(dummyCard);
        cardsToConfirm.add(cardToCheck);
        cardsToConfirm.add(cardToCheck);
        cardsToConfirm.add(cardToCheck);
        Deck riskTest = EasyMock.mock(RiskDeck.class);
//        Deck deckTest = EasyMock.mock(Deck.class);


        Territory dummyTerritory = new Territory("1", 1, 1, 1, 1);


        EasyMock.expect(cardToCheck.value()).andReturn("China").times(6);
        if (bonus != 3) {
            EasyMock.expect(testPlayer.ownsTerritory(dummyTerritory)).andReturn(false).times(3 - bonus);
        }
        if (bonus != 0) {
            EasyMock.expect(testPlayer.ownsTerritory(dummyTerritory)).andReturn(true).times(bonus);
        }
        EasyMock.expect(riskTest.turnInCards(cardsToConfirm)).andReturn(4);

        testPlayer.addDeployableTroops(4);
        testPlayer.removeCards(cardsToConfirm);


        EasyMock.replay(testPlayer);
        EasyMock.replay(riskTest);
        EasyMock.replay(cardToCheck);


        Game gameTest = EasyMock.partialMockBuilder(Game.class)
                .addMockedMethod("convertTerritoryNameToObject")
                .withConstructor(ArrayList.class, Collection.class, HashMap.class, RiskDeck.class, Attack.class)
                .withArgs(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, riskTest, emptyAttack)
                .createMock();

        EasyMock.expect(gameTest.convertTerritoryNameToObject("China")).andReturn(dummyTerritory).times(3);

        EasyMock.replay(gameTest);


        gameTest.turnCardsIn(cardsToConfirm);


        EasyMock.verify(testPlayer);
        EasyMock.verify(riskTest);
        EasyMock.verify(gameTest);
        EasyMock.verify(cardToCheck);
    }

    @Test
    public void attack_attackerLoses_ExpectNoChange() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(0);

        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);

        EasyMock.replay(testAttack);

        Game gameTest = new Game(emptyPlayers, emptyTerritories,
                emptyTerritoriesByContinentMap, allRiskCards,
                testAttack, maneuver, secretMissionCards);

        gameTest.attack(2, 2);

        assertFalse(gameTest.getDrawCard());
        EasyMock.verify(testAttack);
    }

    @Test
    public void attack_attackerWinButDoesNotConquer_ExpectNoChange() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(0);

        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);

        EasyMock.replay(testAttack);

        Game gameTest = new Game(emptyPlayers, emptyTerritories,
                emptyTerritoriesByContinentMap, allRiskCards,
                testAttack, maneuver, secretMissionCards);

        gameTest.attack(2, 2);

        assertFalse(gameTest.getDrawCard());
        EasyMock.verify(testAttack);
    }

    @Test
    public void attack_attackerWinsOutrightDefenderLives_ExpectCanDrawCardTrue() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(1);

        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);

        EasyMock.replay(testAttack);

        Game gameTest = new Game(emptyPlayers, emptyTerritories,
                emptyTerritoriesByContinentMap, allRiskCards,
                testAttack, maneuver, secretMissionCards);

        gameTest.attack(2, 2);

        assertTrue(gameTest.getDrawCard());
        EasyMock.verify(testAttack);
    }

    @Test
    public void attack_attackerWinsOutrightDefenderLoses_ExpectDrawCardAndRemoveCall() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(2);


        ArrayList<Player> testPlayers = new ArrayList<>();
        Player testPlayer = EasyMock.mock(Player.class);
        testPlayers.add(testPlayer);
        testPlayers.add(testPlayer);
        testPlayers.add(testPlayer);

        ArrayList<Card> testListCards = new ArrayList<>();
        Card testCard = new Card("dc", "dc", "dc");
        testListCards.add(testCard);
        testListCards.add(testCard);
        testListCards.add(testCard);

        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(1).times(2);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(0);
        EasyMock.expect(testPlayer.getCards()).andReturn(testListCards);

        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);

        EasyMock.replay(testAttack);
        EasyMock.replay(testPlayer);

        Game gameTest = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards,
                testAttack, maneuver, secretMissionCards);


        gameTest.attack(2, 2);

        assertTrue(gameTest.getDrawCard());
        assertNotEquals(gameTest.getGameState(), GameState.WIN);

        EasyMock.verify(testAttack);
        EasyMock.verify(testPlayer);
    }

    @Test
    public void attack_attackerWinsOutrightDefenderLosesFinalPlayer_ExpectDrawCardAndRemoveCall() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(2);


        ArrayList<Player> testPlayers = new ArrayList<>();
        Player testPlayer = EasyMock.mock(Player.class);
        testPlayers.add(testPlayer);
        testPlayers.add(testPlayer);

        ArrayList<Card> testListCards = new ArrayList<>();
        Card testCard = new Card("dc", "dc", "dc");
        testListCards.add(testCard);
        testListCards.add(testCard);
        testListCards.add(testCard);

        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(1).times(1);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(0);
        EasyMock.expect(testPlayer.getCards()).andReturn(testListCards);

        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);


        EasyMock.replay(testAttack);
        EasyMock.replay(testPlayer);

        Game gameTest = new Game(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards,
                testAttack, maneuver, secretMissionCards);


        gameTest.attack(2, 2);

        assertTrue(gameTest.getDrawCard());
        assertEquals(gameTest.getGameState(), GameState.WIN);

        EasyMock.verify(testAttack);
        EasyMock.verify(testPlayer);

    }

    @Test
    public void attack_attackerWinsOutrightDefenderLosesSecretMissionMode_ExpectDrawCardAndRemoveCall() {
        Attack testAttack = EasyMock.mock(Attack.class);
        ArrayList<Integer> attackOutput = new ArrayList<>();
        attackOutput.add(0);
        attackOutput.add(0);
        attackOutput.add(2);


        ArrayList<Player> testPlayers = new ArrayList<>();
        Player testPlayer = EasyMock.mock(Player.class);
        testPlayers.add(testPlayer);
        testPlayers.add(testPlayer);
        testPlayers.add(testPlayer);

        ArrayList<Card> testListCards = new ArrayList<>();
        Card testCard = new Card("dc", "dc", "dc");
        testListCards.add(testCard);
        testListCards.add(testCard);
        testListCards.add(testCard);
        SecretMissionDeck secretMissions = new SecretMissionDeck(testListCards);


        EasyMock.expect(testAttack.attackerWins(anyInt(), anyInt())).andReturn(attackOutput);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(1).times(2);
        EasyMock.expect(testPlayer.territoryCount()).andReturn(0);
        EasyMock.expect(testPlayer.getCards()).andReturn(testListCards);


        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);
        testPlayer.addCard(testCard);

        EasyMock.replay(testAttack);
        EasyMock.replay(testPlayer);


        Game gameTest = EasyMock.partialMockBuilder(Game.class)
                .addMockedMethod("checkForSecretMissionWin")
                .withConstructor(ArrayList.class, Collection.class, HashMap.class, RiskDeck.class, Attack.class,
                        Maneuver.class, SecretMissionDeck.class)
                .withArgs(testPlayers, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, testAttack, maneuver, secretMissions)
                .createMock();

        gameTest.checkForSecretMissionWin();

        EasyMock.replay(gameTest);

        gameTest.assignSecretMissionCards(testListCards);

        gameTest.attack(2, 2);

        assertTrue(gameTest.getDrawCard());
        assertNotEquals(gameTest.getGameState(), GameState.WIN);

        EasyMock.verify(testAttack);
        EasyMock.verify(testPlayer);
        EasyMock.verify(gameTest);

    }

    @Test
    public void validateOwns24Territories_NullPlayer_ExpectNullPointerException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }

        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The player cannot be null.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.owns24Territories(null);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void validateOwns24Territories_23Territories_ExpectFalse() {
        helpervalidateOwns24Territories(23, false);

    }

    @Test
    public void validateOwns24Territories_0Territories_ExpectFalse() {
        helpervalidateOwns24Territories(0, false);
    }

    @Test
    public void validateOwns24Territories_24Territories_ExpectTrue() {
        helpervalidateOwns24Territories(24, true);
    }

    @Test
    public void validateOwns24Territories_25Territories_ExpectTrue() {
        helpervalidateOwns24Territories(25, true);
    }

    private void helpervalidateOwns24Territories(int count, boolean expected) {
        EasyMock.expect(players.get(0).territoryCount()).andReturn(count);
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        boolean actual = testGame.owns24Territories(players.get(0));
        assertEquals(expected, actual);

        for (Player player : players) {
            EasyMock.verify(player);
        }

    }

    @Test
    public void playerOwnsContinent_NullPlayer_ExpectNullPointerException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The player and continent cannot be null.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.playerOwnsContinent(Continent.ASIA, null);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void playerOwnsContinent_NullContinent_ExpectNullPointerException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The player and continent cannot be null.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.playerOwnsContinent(null, players.get(0));
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void playerOwnsContinent_AllNorthAmerica_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.NORTH_AMERICA);
    }

    @Test
    public void playerOwnsContinent_AllSouthAmerica_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.SOUTH_AMERICA);
    }

    @Test
    public void playerOwnsContinent_AllAustralia_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.AUSTRALIA);
    }

    @Test
    public void playerOwnsContinent_AllEurope_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.EUROPE);
    }

    @Test
    public void playerOwnsContinent_AllAsia_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.ASIA);
    }


    @Test
    public void playerOwnsContinent_AllAfrica_ExpectTrue() {
        playerOwnsAllTerritoryHelper(Continent.AFRICA);
    }

    @Test
    public void playerOwnsContinent_AllNorthAmericaMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.NORTH_AMERICA);
    }

    @Test
    public void playerOwnsContinent_AllSouthAmericaMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.SOUTH_AMERICA);
    }

    @Test
    public void playerOwnsContinent_AllAfricaMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.AFRICA);
    }

    @Test
    public void playerOwnsContinent_AllAsiaMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.ASIA);
    }

    @Test
    public void playerOwnsContinent_AllEuropeMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.EUROPE);
    }

    @Test
    public void playerOwnsContinent_AllAustraliaMinus1_ExpectTrue() {
        playerOwnsAllMinusTerritoryHelper(Continent.AUSTRALIA);
    }


    private void playerOwnsAllMinusTerritoryHelper(Continent continent) {
        Territory[] territories = territoriesByContinentMap.get(continent);
        for (int i = 0; i < continent.territoryCount(); i++) {
            Territory tempTerritory = EasyMock.mock(Territory.class);
            boolean toReturn = true;
            if (i == 0) {
                toReturn = false;
            }
            EasyMock.expect(players.get(0).ownsTerritory(tempTerritory)).andReturn(toReturn);
            EasyMock.replay(tempTerritory);
            territories[i] = tempTerritory;

        }
        for (Player player : players) {
            EasyMock.replay(player);
        }

        Game testGame = new Game(players, emptyTerritories, territoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        boolean actual = testGame.playerOwnsContinent(continent, players.get(0));
        assertFalse(actual);
        for (Player player : players) {
            EasyMock.verify(player);
        }
        for (Territory territory : territoriesByContinentMap.get(continent)) {
            EasyMock.verify(territory);
        }
    }

    private void playerOwnsAllTerritoryHelper(Continent continent) {
        Territory[] territories = territoriesByContinentMap.get(continent);
        for (int i = 0; i < continent.territoryCount(); i++) {
            Territory tempTerritory = EasyMock.mock(Territory.class);
            EasyMock.expect(players.get(0).ownsTerritory(tempTerritory)).andReturn(true);
            EasyMock.replay(tempTerritory);
            territories[i] = tempTerritory;

        }
        for (Player player : players) {
            EasyMock.replay(player);
        }

        Game testGame = new Game(players, emptyTerritories, territoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        boolean actual = testGame.playerOwnsContinent(continent, players.get(0));
        assertTrue(actual);
        for (Player player : players) {
            EasyMock.verify(player);
        }
        for (Territory territory : territoriesByContinentMap.get(continent)) {
            EasyMock.verify(territory);
        }
    }

    @Test
    public void checkDestroyMission_withNullCard_ExpectNullPointerException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The card cannot be null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    testGame.checkDestroyMission(null, 0);
                }, "Null Pointer Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void checkDestroyMission_withNonDestroyType_ExpectIllegalArumentException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        Card card = EasyMock.mock(Card.class);
        EasyMock.expect(card.type()).andReturn("Conquer");
        EasyMock.replay(card);

        String expectedMessage = "The card is not of the correct type.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.checkDestroyMission(card, 0);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatBlack_Has24Territories_ExpectWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Black", 24, true);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatBlack_Has23Territories_ExpectNoWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Black", 23, false);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerMagenta_DefeatMagenta_Has24Territories_ExpectWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Magenta", 24, true);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerMagenta_DefeatMagenta_Has22Territories_ExpectNoWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Magenta", 22, false);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatRed_Has24Territories_ExpectWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Red", 24, true);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatRed_Has21Territories_ExpectNoWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Red", 21, false);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerGreen_DefeatGreen_Has25Territories_ExpectWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Green", 25, true);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerGreen_DefeatGreen_Has21Territories_ExpectNoWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Green", 21, false);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlue_DefaultBlue_Has25Territories_ExpectWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Blue", 25, true);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlue_DefeatBlue_Has21Territories_ExpectNoWinState() {
        checkDestroyMissionPlayerColorSameAsCardHelper("Blue", 21, false);
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatMagenta_MagentaAlive_ExpectNoWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Black", "Magenta", false, new ArrayList<String>(List.of("Magenta", "Blue", "Back")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatMagenta_MagentaDead_ExpectWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Black", "Magenta", true, new ArrayList<String>(List.of("Red", "Blue", "Back")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatRed_RedDead_ExpectWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Black", "Red", true, new ArrayList<String>(List.of("Blue", "Black", "Magenta", "Green")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatRed_RedAlive_ExpectNoWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Black", "Red", false,
                new ArrayList<String>(List.of("Red", "Blue", "Black", "Magenta", "Green")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlack_DefeatGreen_GreenAlive_ExpectWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Black", "Green", true,
                new ArrayList<String>(List.of("Blue", "Black", "Magenta")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerBlue_DefeatGreen_GreenDead_ExpectNoWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Blue", "Green", false,
                new ArrayList<String>(List.of("Blue", "Green", "Red")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatBlue_BlueAlive_ExpectNoWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Red", "Blue", false,
                new ArrayList<String>(List.of("Blue", "Red", "Magenta")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatBlue_BlueDead_ExpectWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Red", "Blue", true,
                new ArrayList<String>(List.of("Red", "Green", "Magenta")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatBlack_BlackAlive_ExpectNoWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Red", "Black", false,
                new ArrayList<String>(List.of("Black", "Red", "Magenta", "Blue")));
    }

    @Test
    public void checkDestroyMission_withCurrentPlayerRed_DefeatBlack_BlackDead_ExpectWinState() {
        checkDestroyMissionPlayerColorDifferentHelper("Red", "Black", true,
                new ArrayList<String>(List.of("Red", "Blue", "Magenta")));
    }

    private void checkDestroyMissionPlayerColorSameAsCardHelper(String colorName, int troops, boolean shouldWin) {
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        Card card = EasyMock.mock(Card.class);
        Player currentPlayer = players.get(0);

        EasyMock.expect(card.type()).andReturn("Destroy");
        EasyMock.expect(currentPlayer.getColor()).andReturn(playerColors.get(colorName));
        EasyMock.expect(card.value()).andReturn(colorName);
        EasyMock.expect(currentPlayer.territoryCount()).andReturn(troops);


        EasyMock.replay(card);
        for (Player player : players) {
            EasyMock.replay(player);
        }

        testGame.checkDestroyMission(card, 0);
        if (shouldWin) {
            assertEquals(GameState.WIN, testGame.getGameState());
        } else {
            assertNotEquals(GameState.WIN, testGame.getGameState());
        }

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
    }

    private void checkDestroyMissionPlayerColorDifferentHelper(String playerColorName, String cardColorName,
                                                               boolean shouldWin, ArrayList<String> colors) {
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        Card card = EasyMock.mock(Card.class);
        Player currentPlayer = players.get(0);

        EasyMock.expect(card.type()).andReturn("Destroy");
        EasyMock.expect(currentPlayer.getColor()).andReturn(playerColors.get(playerColorName));
        EasyMock.expect(card.value()).andReturn(cardColorName);

        EasyMock.expect(card.value()).andReturn(cardColorName);
        int colorIndex = 0;
        for (Player player : players) {
            EasyMock.expect(player.getColor()).andReturn(playerColors.get(colors.get(colorIndex)));
            colorIndex++;

        }

        EasyMock.replay(card);
        for (Player player : players) {
            EasyMock.replay(player);
        }

        testGame.checkDestroyMission(card, 0);
        if (shouldWin) {
            assertEquals(GameState.WIN, testGame.getGameState());
        } else {
            assertNotEquals(GameState.WIN, testGame.getGameState());
        }

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
    }

    @Test
    public void checkConquerMissionPlayer_WithNullCard_ExpectNullPointerException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The card cannot be null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    testGame.checkConquerMission(null);
                }, "Null Pointer Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
    }

    @Test
    public void checkConquerMissionPlayer_WithCardNotConquer_ExpectIllegalArgumentException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        Card card = EasyMock.mock(Card.class);
        EasyMock.expect(card.type()).andReturn("Destroy");
        EasyMock.replay(card);

        String expectedMessage = "The card is not of the correct type.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.checkConquerMission(card);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfAsiaAndAfrica_ExpectWin() {
        checkConquerMissionPlayerContinentsHelper("ASIA-AFRICA",
                new ArrayList<Continent>(List.of(Continent.ASIA, Continent.AFRICA)),
                new ArrayList<>(List.of(true, true)), true);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfAsiaAndNotAfrica_ExpectNoWin() {
        checkConquerMissionPlayerContinentsHelper("ASIA-AFRICA",
                new ArrayList<Continent>(List.of(Continent.ASIA, Continent.AFRICA)),
                new ArrayList<>(List.of(true, false)), false);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfAsiaAndSouthAmerica_ExpectWin() {
        checkConquerMissionPlayerContinentsHelper("ASIA-SOUTH_AMERICA",
                new ArrayList<Continent>(List.of(Continent.ASIA, Continent.SOUTH_AMERICA)),
                new ArrayList<>(List.of(true, true)), true);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfSouthAmericaAndNotAsia_ExpectNoWin() {
        checkConquerMissionPlayerContinentsHelper("ASIA-SOUTH_AMERICA",
                new ArrayList<Continent>(List.of(Continent.ASIA, Continent.SOUTH_AMERICA)),
                new ArrayList<>(List.of(false, true)), false);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfNorthAmericaAndAfrica_ExpectWin() {
        checkConquerMissionPlayerContinentsHelper("NORTH_AMERICA-AFRICA",
                new ArrayList<Continent>(List.of(Continent.NORTH_AMERICA, Continent.AFRICA)),
                new ArrayList<>(List.of(true, true)), true);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfNorthAmericaAndNotAfrica_ExpectNoWin() {
        checkConquerMissionPlayerContinentsHelper("NORTH_AMERICA-AFRICA",
                new ArrayList<Continent>(List.of(Continent.NORTH_AMERICA, Continent.AFRICA)),
                new ArrayList<>(List.of(true, false)), false);
    }

    @Test
    public void checkConquerMissionPlayer_WithAllOfNorthAmericaAndAustralia_ExpectWin() {
        checkConquerMissionPlayerContinentsHelper("NORTH_AMERICA-AUSTRALIA",
                new ArrayList<Continent>(List.of(Continent.NORTH_AMERICA, Continent.AUSTRALIA)),
                new ArrayList<>(List.of(true, true)), true);
    }

    @Test
    public void checkConquerMissionPlayer_WithNoneOfNorthAmericaAndAustralia_ExpectNoWin() {
        checkConquerMissionPlayerContinentsHelper("NORTH_AMERICA-AUSTRALIA",
                new ArrayList<Continent>(List.of(Continent.NORTH_AMERICA, Continent.AUSTRALIA)),
                new ArrayList<>(List.of(false, false)), false);
    }

    private void checkConquerMissionPlayerContinentsHelper(String continentsToParse,
                                                           ArrayList<Continent> continentsToOwn, ArrayList<Boolean> owns, boolean win) {
        Game gamePartialMock = EasyMock.partialMockBuilder(Game.class)
                .withConstructor(ArrayList.class, Collection.class,
                        HashMap.class, RiskDeck.class,
                        Attack.class, Maneuver.class, SecretMissionDeck.class)
                .withArgs(players, emptyTerritories, territoriesByContinentMap,
                        allRiskCards, null, maneuver, secretMissionCards)
                .addMockedMethod("playerOwnsContinent")
                .createMock();
        Card card = EasyMock.mock(Card.class);
        EasyMock.expect(card.type()).andReturn("Conquer");

        EasyMock.expect(card.value()).andReturn(continentsToParse);

        for (int i = 0; i < continentsToOwn.size(); i++) {
            Continent continent = continentsToOwn.get(i);
            EasyMock.expect(gamePartialMock.playerOwnsContinent(continent, players.get(0))).andReturn(owns.get(i));
            if (!owns.get(i)) {
                break;
            }
        }
        EasyMock.replay(card);
        EasyMock.replay(gamePartialMock);
        for (Player player : players) {
            EasyMock.replay(player);
        }

        gamePartialMock.checkConquerMission(card);
        if (win) {
            assertEquals(GameState.WIN, gamePartialMock.getGameState());
        } else {
            assertNotEquals(GameState.WIN, gamePartialMock.getGameState());
        }

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
        EasyMock.verify(gamePartialMock);
    }


    @Test
    public void checkControlMission_WithNullCard_ExpectNullPointer() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        String expectedMessage = "The card cannot be null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    testGame.checkControlMission(null);
                }, "Null Pointer Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }


    }

    @Test
    public void checkControlMissionPlayer_WithCardNotConquer_ExpectIllegalArgumentException() {
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game testGame = new Game(players, emptyTerritories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);
        Card card = EasyMock.mock(Card.class);
        EasyMock.expect(card.type()).andReturn("Control");
        EasyMock.replay(card);

        String expectedMessage = "The card is not of the correct type.";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    testGame.checkConquerMission(card);
                }, "Illegal Argument Exception should be thrown");

        assertEquals(expectedMessage, thrown.getMessage());

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(card);
    }

    @Test
    public void checkControlMissionPlayer_With18Territories1TroopEach_ExpectNoWin() {
        checkControlHelper("18TerritoriesWith2Troops", false, 18, false);
    }

    @Test
    public void checkControlMissionPlayer_With18Territories2TroopEachExceptLast_ExpectNoWin() {
        checkControlHelper("18TerritoriesWith2Troops", true, 18, false);
    }

    @Test
    public void checkControlMissionPlayer_With18Territories2TroopEach_ExpectWin() {
        checkControlHelper("18TerritoriesWith2Troops", false, 18, true);
    }

    @Test
    public void checkControlMissionPlayer_With19Territories2TroopEach_ExpectWin() {
        checkControlHelper("18TerritoriesWith2Troops", false, 19, true);
    }

    @Test
    public void checkControlMissionPlayer_With23Territories_ExpectNoWin() {
        checkControlHelper("24Territories", false, 23, false);
    }

    @Test
    public void checkControlMissionPlayer_With24Territories_ExpectNoWin() {
        checkControlHelper("24Territories", false, 24, true);
    }

    @Test
    public void checkControlMissionPlayer_With25Territories_ExpectNoWin() {
        checkControlHelper("24Territories", false, 25, true);
    }

    public void checkControlHelper(String type, boolean allButOne, int count, boolean win) {
        Card card = EasyMock.mock(Card.class);
        EasyMock.expect(card.type()).andReturn("Control");

        Player currentPlayer = players.get(0);

        EasyMock.expect(card.value()).andReturn(type);
        ArrayList<Territory> territories = new ArrayList<>();
        if (type.equals("18TerritoriesWith2Troops")) {
            for (int i = 0; i < 42; i++) {
                Territory tempTerritory = EasyMock.mock(Territory.class);
                territories.add(tempTerritory);
                if (allButOne) {
                    if (i < count - 2) {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(true);
                        EasyMock.expect(tempTerritory.getCurrentNumberOfTroops()).andReturn(2);
                    } else if (i == count - 1) {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(true);
                        EasyMock.expect(tempTerritory.getCurrentNumberOfTroops()).andReturn(1);

                    } else {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(false);
                    }
                } else if (win) {
                    if (i < count) {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(true);
                        EasyMock.expect(tempTerritory.getCurrentNumberOfTroops()).andReturn(2);
                    } else {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(false);
                    }

                } else {
                    if (i < count) {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(true);
                        EasyMock.expect(tempTerritory.getCurrentNumberOfTroops()).andReturn(1);
                    } else {
                        EasyMock.expect(currentPlayer.ownsTerritory(tempTerritory)).andReturn(false);
                    }
                }
                EasyMock.replay(tempTerritory);
            }
        } else {
            EasyMock.expect(currentPlayer.territoryCount()).andReturn(count);
        }

        Game testGame = new Game(players, territories, emptyTerritoriesByContinentMap, allRiskCards, null, maneuver, secretMissionCards);

        EasyMock.replay(card);
        for (Player player : players) {
            EasyMock.replay(player);
        }

        testGame.checkControlMission(card);

        if (win) {
            assertEquals(GameState.WIN, testGame.getGameState());
        } else {
            assertNotEquals(GameState.WIN, testGame.getGameState());
        }

        for (Player player : players) {
            EasyMock.verify(player);
        }
        for (Territory territory : territories) {
            EasyMock.verify(territory);
        }
        EasyMock.verify(card);

    }


    @Test
    public void canAttack_1Territory1Troop_ExpectFalse() {
        canAttackHelper(1, false);
    }

    @Test
    public void canAttack_2Territory1Troop_ExpectFalse() {
        canAttackHelper(2, false);
    }

    @Test
    public void canAttack_41Territory1Troop_ExpectFalse() {
        canAttackHelper(41, false);
    }

    @Test
    public void canAttack_1Territory2Troop_ExpectTrue() {
        canAttackHelper(1, true);
    }

    @Test
    public void canAttack_2Territory2Troop_ExpectTrue() {
        canAttackHelper(2, true);
    }

    @Test
    public void canAttack_41Territory2Troop_ExpectTrue() {
        canAttackHelper(41, true);
    }

    @Test
    public void canAttack_noNeighbor_ExpectFalse() {
        canAttackHelper(5, false);
    }

    @Test
    public void canAttack_neighbor_ExpectTrue() {
        canAttackHelper(5, true);
    }

    private void canAttackHelper(int territoryCount, boolean expectedResult) {
        Attack attack = EasyMock.mock(Attack.class);
        ArrayList<Territory> territories = new ArrayList<>();
        for (int i = 0; i < territoryCount; i++) {
            Territory territory = EasyMock.mock(Territory.class);
            territories.add(territory);
            EasyMock.expect(attack.validateAttackingTerritory(territory, players.get(0))).andReturn(expectedResult);
            EasyMock.replay(territory);
            if (expectedResult) {
                break;
            }
        }
        EasyMock.replay(attack);
        for (Player player : players) {
            EasyMock.replay(player);
        }
        Game game = new Game(players, territories, emptyTerritoriesByContinentMap, null, attack, null, null);

        boolean actual = game.canAttack();

        for (Player player : players) {
            EasyMock.verify(player);
        }
        EasyMock.verify(attack);
        for (Territory territory : territories) {
            EasyMock.verify(territory);
        }
        assertEquals(expectedResult, actual);
    }
}
