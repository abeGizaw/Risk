package mainApp.domain.IntegrationTests;

import mainApp.domain.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class F12ManeuverTroopsTest {
    private Game game;
    private Initializer initializer;
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<String, Point> pointsByTerritoryName;
    private HashMap<String, Territory> territoriesByTerritoryName;


    private void initializeGameObjects() throws IOException {
        Dimension standardScreenSize = new Dimension(1536, 864);
        initializer = new Initializer(standardScreenSize);
        String[] colors = {"Magenta", "Red", "Blue"};
        ArrayList<String> playerColors = new ArrayList<>(List.of(colors));
        players = initializer.makePlayers(playerColors.size(), playerColors);
        initializer.createAllEntities();
        game = initializer.makeGame(players, false, ResourceBundle.getBundle("message"));
        initializePointsByName();
        initializeTerritoriesByName();
    }

    private void initializePointsByName() throws IOException {
        String territoryPointsFilePath = "src/main/java/data/territoryPoints.txt";
        pointsByTerritoryName = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryPointsFilePath), Charset.defaultCharset()));
            String territory;
            while ((territory = br.readLine()) != null) {
                String[] territoryData = territory.split(", ");
                String territoryName = territoryData[0];
                int territoryXPos = Integer.parseInt(territoryData[1]);
                int territoryYPos = Integer.parseInt(territoryData[2]);
                pointsByTerritoryName.put(territoryName, new Point(territoryXPos, territoryYPos));
            }
            br.close();
        } catch (IOException e) {
            throw new IOException("Cannot find this file." + territoryPointsFilePath);
        }
    }

    private void initializeTerritoriesByName() {
        territoriesByTerritoryName = new HashMap<>();
        Collection<Territory> territories = initializer.getTerritories();
        for (Territory territory: territories) {
            String territoryName = territory.getTerritoryName();
            territoriesByTerritoryName.put(territoryName, territory);
        }
    }

    private void setupBoardState() throws IOException {
        String territoriesByPlayerFilePath = "src/main/java/data/F11TerritoriesByPlayer.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoriesByPlayerFilePath), Charset.defaultCharset()));
            String line;
            int playerIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] territoryNamesByPlayer = line.split(",");
                Player currentPlayer = players.get(playerIndex);
                for (String territoryName : territoryNamesByPlayer) {
                    Territory territory = territoriesByTerritoryName.get(territoryName);
                    territory.addAdditionalTroops(1);
                    currentPlayer.addTerritory(territory);
                }
                playerIndex++;
            }
            br.close();
        } catch (IOException e) {
            throw new IOException("Cannot find this file." + territoriesByPlayerFilePath);
        }
    }

    private void skipTurns(int turnsSkipped) {
        for (int i = 0; i < turnsSkipped; i++) {
            game.setGameState(GameState.MANEUVER);
            game.transitionFromManeuverToDeploy();
        }
    }

    private void setTroopsAtTerritory(String territoryName, int troops) {
        Territory territory = territoriesByTerritoryName.get(territoryName);
        territory.addAdditionalTroops(troops - 1);
    }

    private void resetManeuverableTroops() {
        for (Player player: players) {
            player.removeDeployableTroops(player.getDeployableTroops());
        }
    }

    private void validateValidManeuver(int playerIndex, String territoryFromName, int troopsOnTerritoryFrom,
                                       String territoryToName, int troopsOnTerritoryTo, int troopsManeuvered) throws IOException {
        initializeGameObjects();
        setupBoardState();
        skipTurns(playerIndex);
        game.setGameState(GameState.MANEUVER);
        setTroopsAtTerritory(territoryFromName, troopsOnTerritoryFrom);
        setTroopsAtTerritory(territoryToName, troopsOnTerritoryTo);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point territoryToPoint = pointsByTerritoryName.get(territoryToName);
        int expectedTroopsOnTerritoryFrom = troopsOnTerritoryFrom - troopsManeuvered;
        int expectedTroopsOnTerritoryTo = troopsOnTerritoryTo + troopsManeuvered;

        game.clickedOnPoint(territoryFromPoint);
        game.clickedOnPoint(territoryToPoint);
        game.maneuverTroops(new AtomicInteger(troopsManeuvered));

        assertEquals(GameState.DEPLOY, game.getGameState());
        int actualTroopsOnTerritoryFrom = territoriesByTerritoryName.get(territoryFromName).getCurrentNumberOfTroops();
        int actualTroopsOnTerritoryTo = territoriesByTerritoryName.get(territoryToName).getCurrentNumberOfTroops();
        assertEquals(expectedTroopsOnTerritoryFrom, actualTroopsOnTerritoryFrom);
        assertEquals(expectedTroopsOnTerritoryTo, actualTroopsOnTerritoryTo);
    }

    @Test
    public void testManeuverTroops_withTerritoryFromNotOwnedByPlayer_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        game.setGameState(GameState.MANEUVER);
        String expectedErrorMessage = "When maneuvering, you must select a territory you control.";
        Point pointForTerritoryNotOwnedByPlayer = pointsByTerritoryName.get("Alberta");

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(pointForTerritoryNotOwnedByPlayer);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_withTerritoryFromHavingOneTroop_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        game.setGameState(GameState.MANEUVER);
        String expectedErrorMessage = "When Maneuvering, you must maneuver from a territory with at least 2 troops "
                + "that has an adjacent territory you control.";
        Point pointForTerritoryNotOwnedByPlayer = pointsByTerritoryName.get("Alaska");

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(pointForTerritoryNotOwnedByPlayer);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_withTerritoryNotHavingAdjacentTerritoriesThePlayerOwns_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        skipTurns(2);
        game.setGameState(GameState.MANEUVER);
        String expectedErrorMessage = "When Maneuvering, you must maneuver from a territory with at least 2 troops "
                + "that has an adjacent territory you control.";
        Point pointForTerritoryNotOwnedByPlayer = pointsByTerritoryName.get("Central America");

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(pointForTerritoryNotOwnedByPlayer);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_withTerritoryToNotBeingOwnedByThePlayer_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        skipTurns(1);
        game.setGameState(GameState.MANEUVER);
        String territoryFromName = "Japan";
        String expectedErrorMessage = "When maneuvering, you must select a territory you control.";
        setTroopsAtTerritory(territoryFromName, 2);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point pointForTerritoryNotOwnedByPlayer = pointsByTerritoryName.get("Iceland");

        game.clickedOnPoint(territoryFromPoint);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(pointForTerritoryNotOwnedByPlayer);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(territoryFromName, game.getManeuverFromName());
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_withTerritoryFromAndTerritoryToBeingTheSame_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        game.setGameState(GameState.MANEUVER);
        String territoryFromName = "Great Britain";
        String expectedErrorMessage = "When maneuvering, can not maneuver from and to the same territory.";
        setTroopsAtTerritory(territoryFromName, 2);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);

        game.clickedOnPoint(territoryFromPoint);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(territoryFromPoint);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(territoryFromName, game.getManeuverFromName());
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_withTerritoryFromNotConnectedToTerritoryTo_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        game.setGameState(GameState.MANEUVER);
        String territoryFromName = "Great Britain";
        String expectedErrorMessage = "When Maneuvering to a territory, it must be connected to the territory you are "
                + "maneuvering from through adjacent territories you control.";
        setTroopsAtTerritory(territoryFromName, 2);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point territoryToPoint = pointsByTerritoryName.get("Madagascar");

        game.clickedOnPoint(territoryFromPoint);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.clickedOnPoint(territoryToPoint);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(territoryFromName, game.getManeuverFromName());
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_tryingToManeuverZeroTroops_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        skipTurns(2);
        game.setGameState(GameState.MANEUVER);
        String territoryFromName ="New Guinea";
        String territoryToName = "Indonesia";
        setTroopsAtTerritory(territoryFromName, 3);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point territoryToPoint = pointsByTerritoryName.get(territoryToName);
        String expectedErrorMessage = "When maneuvering, you must maneuver a positive number of troops.";

        game.clickedOnPoint(territoryFromPoint);
        game.clickedOnPoint(territoryToPoint);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.maneuverTroops(new AtomicInteger(0));
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(territoryFromName, game.getManeuverFromName());
        assertEquals(territoryToName, game.getManeuverToName());
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_tryingToManeuverMoreTroopsThanTheTerritoryHas_expectingIllegalArgumentException() throws IOException {
        initializeGameObjects();
        setupBoardState();
        game.setGameState(GameState.MANEUVER);
        String territoryFromName ="North West Territory";
        String territoryToName = "Northern Europe";
        setTroopsAtTerritory(territoryFromName, 3);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point territoryToPoint = pointsByTerritoryName.get(territoryToName);
        String expectedErrorMessage = "When maneuvering, you must have at least one troop remaining on the territory you are maneuvering from.";

        game.clickedOnPoint(territoryFromPoint);
        game.clickedOnPoint(territoryToPoint);
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> {
            game.maneuverTroops(new AtomicInteger(3));
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(territoryFromName, game.getManeuverFromName());
        assertEquals(territoryToName, game.getManeuverToName());
        assertEquals(GameState.MANEUVER, game.getGameState());
    }

    @Test
    public void testManeuverTroops_fromUkraineToSiberiaWithOneTroop() throws IOException {
        validateValidManeuver(0, "Ukraine", 2, "Siberia", Integer.MAX_VALUE - 1, 1);
    }

    @Test
    public void testManeuverTroops_fromIndiaToUralWithThreeTroops() throws IOException {
        validateValidManeuver(2, "India", 3, "Ural", 5, 2);
    }

    @Test
    public void testManeuverTroops_fromMongoliaToJapanWithMaxIntMinusOneTroops() throws IOException {
        validateValidManeuver(1, "Mongolia", Integer.MAX_VALUE, "Japan", 1, Integer.MAX_VALUE - 1);
    }

    @Test
    public void testManeuverTroops_fromVenezuelaToNorthAfricaWithThreeTroops() throws IOException {
        validateValidManeuver(0, "Venezuela", 7, "North Africa", 5, 3);
    }

    @Test
    public void testManeuverTroops_fromAlbertaToQuebecWithSixTroops() throws IOException {
        validateValidManeuver(1, "Alberta", 8, "Quebec", 2, 6);
    }

    @Test
    public void testManeuverTroops_fromSouthAfricaToEastAfricaWithSixTroops() throws IOException {
        validateValidManeuver(0, "South Africa", 7, "East Africa", 7, 6);
    }

    @Test
    public void testManeuverTroops_fromIcelandToAlaskaWith13Troops() throws  IOException {
        validateValidManeuver(0, "Iceland", 23, "Alaska", 13, 15);
    }

    @Test
    public void testManeuverTroops_fromWesternEuropeToAfghanistanWith10Troops() throws IOException {
        validateValidManeuver(2, "Western Europe", 13, "Afghanistan", 17, 10);
    }

    @Test
    public void testManeuverTroops_abilityToSkipManeuverPhase() throws IOException {
        initializeGameObjects();
        setupBoardState();
        resetManeuverableTroops();
        String territoryFromName = "Venezuela";
        String territoryToName = "North Africa";
        game.setGameState(GameState.MANEUVER);
        setTroopsAtTerritory(territoryFromName, 617);
        setTroopsAtTerritory(territoryToName, 213);
        Point territoryFromPoint = pointsByTerritoryName.get(territoryFromName);
        Point territoryToPoint = pointsByTerritoryName.get(territoryToName);
        int expectedTroopsOnTerritoryFrom = 617;
        int expectedTroopsOnTerritoryTo = 213;
        int expectedDeployableTroops = 3;

        game.clickedOnPoint(territoryFromPoint);
        game.clickedOnPoint(territoryToPoint);
        game.transitionFromManeuverToDeploy();

        assertEquals(GameState.DEPLOY, game.getGameState());
        Player currentPlayer = players.get(1);
        int actualTroopsOnTerritoryFrom = territoriesByTerritoryName.get(territoryFromName).getCurrentNumberOfTroops();
        int actualTroopsOnTerritoryTo = territoriesByTerritoryName.get(territoryToName).getCurrentNumberOfTroops();
        assertEquals(expectedTroopsOnTerritoryFrom, actualTroopsOnTerritoryFrom);
        assertEquals(expectedTroopsOnTerritoryTo, actualTroopsOnTerritoryTo);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
    }
}
