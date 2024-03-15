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

import static org.junit.jupiter.api.Assertions.*;

public class F9DeployTroopsToTerritoriesTest {
    private ArrayList<Player> players;
    private Game game;
    private HashMap<String, Point> pointsByTerritoryName;
    private final Dimension standardScreenSize = new Dimension(1536, 864);
    private HashMap<String, Territory> territoriesByTerritoryName;

    private void initializeGameState(String[] colors) throws IOException {
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Initializer initializer = new Initializer(standardScreenSize);
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        ResourceBundle message = ResourceBundle.getBundle("message");
        game = initializer.makeGame(players, false, message);
        territoriesByTerritoryName = new HashMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        initializePointsByTerritoryName();
    }

    private void initializePointsByTerritoryName() throws IOException {
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
        } catch (
                IOException e) {
            throw new IOException("Cannot find this file." + territoryPointsFilePath);
        }
    }

    private void setPlayerDeployableTroops(int deployableTroops) {
        Player currentPlayer = players.get(0);
        currentPlayer.removeDeployableTroops(currentPlayer.getDeployableTroops());
        currentPlayer.addDeployableTroops(deployableTroops);
    }

    private void addTerritoryToPlayer(String territoryName, int troopsOnTerritory) {
        Territory territoryToAdd = territoriesByTerritoryName.get(territoryName);
        territoryToAdd.addAdditionalTroops(troopsOnTerritory);
        Player currentPlayer = players.get(0);
        currentPlayer.addTerritory(territoryToAdd);
    }

    @Test
    public void testDeployTroopsToTerritory_withTerritoryNotOwnedByPlayer_expectingIllegalStateException() throws IOException {
        String[] playerColors = {"Red", "Green", "Blue"};
        initializeGameState(playerColors);
        game.setGameState(GameState.DEPLOY);
        setPlayerDeployableTroops(3);
        Point territoryNotOwnedByPlayerPoint = pointsByTerritoryName.get("Alaska");
        String expectedErrorMessage = "This territory does not belong to you. Please choose one you occupy";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            game.clickedOnPoint(territoryNotOwnedByPlayerPoint);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
        assertEquals(GameState.DEPLOY, game.getGameState());
    }

    @Test
    public void testDeployableTroopsToTerritory_withPlayerHaving3DeployableTroopsAndDeploying3ToTerritoryWith1Troop() throws IOException {
        String[] playerColors = {"Red", "Green", "Blue"};
        String territoryDeployedToName = "Indonesia";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(3);
        addTerritoryToPlayer(territoryDeployedToName, 1);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedTo = territoriesByTerritoryName.get(territoryDeployedToName);
        Point territoryDeployedToPoint = pointsByTerritoryName.get(territoryDeployedToName);
        int expectedDeployableTroops = 0;
        int expectedTroopsOnTerritory = 4;
        GameState expectedGameState = GameState.NEWATTACK;

        game.clickedOnPoint(territoryDeployedToPoint);
        game.updateDeployableTroops(3, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnTerritory = territoryDeployedTo.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnTerritory, actualTroopsOnTerritory);
        assertEquals(expectedGameState, actualGameState);
    }

    @Test
    public void testDeployableTroopsToTerritory_withPlayerHaving3DeployableTroopsAndDeploying1ToTerritoryWith1Troop() throws IOException {
        String[] playerColors = {"Red", "Magenta", "Blue", "Black"};
        String territoryDeployedToName = "Iceland";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(3);
        addTerritoryToPlayer(territoryDeployedToName, 1);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedTo = territoriesByTerritoryName.get(territoryDeployedToName);
        Point territoryDeployedToPoint = pointsByTerritoryName.get(territoryDeployedToName);
        int expectedDeployableTroops = 2;
        int expectedTroopsOnTerritory = 2;
        GameState expectedGameState = GameState.DEPLOY;

        game.clickedOnPoint(territoryDeployedToPoint);
        game.updateDeployableTroops(1, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnTerritory = territoryDeployedTo.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnTerritory, actualTroopsOnTerritory);
        assertEquals(expectedGameState, actualGameState);
    }

    @Test
    public void testDeployableTroopsToTerritory_withPlayerHaving18DeployableTroopsAndDeploying9ToTerritoryWith7Troops() throws IOException {
        String[] playerColors = {"Red", "Magenta", "Blue", "Black", "Green"};
        String territoryDeployedToName = "Brazil";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(18);
        addTerritoryToPlayer(territoryDeployedToName, 7);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedTo = territoriesByTerritoryName.get(territoryDeployedToName);
        Point territoryDeployedToPoint = pointsByTerritoryName.get(territoryDeployedToName);
        int expectedDeployableTroops = 9;
        int expectedTroopsOnTerritory = 16;
        GameState expectedGameState = GameState.DEPLOY;

        game.clickedOnPoint(territoryDeployedToPoint);
        game.updateDeployableTroops(9, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnTerritory = territoryDeployedTo.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnTerritory, actualTroopsOnTerritory);
        assertEquals(expectedGameState, actualGameState);
    }

    @Test
    public void testDeployTroopsToTerritory_withPlayerHaving35DeployableTroopsAndDeploying35ToTerritoryWithMaxIntMinus35Troops() throws IOException {
        String[] playerColors = {"Black", "Magenta", "Blue", "Red", "Green"};
        String territoryDeployedToName = "China";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(35);
        addTerritoryToPlayer(territoryDeployedToName, Integer.MAX_VALUE - 35);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedTo = territoriesByTerritoryName.get(territoryDeployedToName);
        Point territoryDeployedToPoint = pointsByTerritoryName.get(territoryDeployedToName);
        int expectedDeployableTroops = 0;
        int expectedTroopsOnTerritory = Integer.MAX_VALUE;
        GameState expectedGameState = GameState.NEWATTACK;

        game.clickedOnPoint(territoryDeployedToPoint);
        game.updateDeployableTroops(35, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnTerritory = territoryDeployedTo.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnTerritory, actualTroopsOnTerritory);
        assertEquals(expectedGameState, actualGameState);
    }

    @Test
    public void testDeployTroopsToTerritory_withPlayerHaving11DeployableTroopsAndDeployingToTwoSeparateTerritories() throws IOException {
        String[] playerColors = {"Black", "Magenta", "Blue", "Red", "Green"};
        String territoryDeployedToFirstName = "North Africa";
        String territoryDeployedToSecondName = "Madagascar";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(11);
        addTerritoryToPlayer(territoryDeployedToFirstName, 3);
        addTerritoryToPlayer(territoryDeployedToSecondName, 7);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedToFirst = territoriesByTerritoryName.get(territoryDeployedToFirstName);
        Territory territoryDeployedToSecond = territoriesByTerritoryName.get(territoryDeployedToSecondName);
        Point territoryDeployedToFirstPoint = pointsByTerritoryName.get(territoryDeployedToFirstName);
        Point territoryDeployedToSecondPoint = pointsByTerritoryName.get(territoryDeployedToSecondName);
        int expectedDeployableTroops = 0;
        int expectedTroopsOnFirstTerritory = 8;
        int expectedTroopsOnSecondTerritory = 13;
        GameState expectedGameState = GameState.NEWATTACK;

        game.clickedOnPoint(territoryDeployedToFirstPoint);
        game.updateDeployableTroops(5, game.getCurrentTerritory());
        game.clickedOnPoint(territoryDeployedToSecondPoint);
        game.updateDeployableTroops(6, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnFirstTerritory = territoryDeployedToFirst.getCurrentNumberOfTroops();
        int actualTroopsOnSecondTerritory = territoryDeployedToSecond.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnFirstTerritory, actualTroopsOnFirstTerritory);
        assertEquals(expectedTroopsOnSecondTerritory, actualTroopsOnSecondTerritory);
        assertEquals(expectedGameState, actualGameState);
    }

    @Test
    public void testDeployTroopsToTerritory_withPlayerHaving13DeployableTroopsAndDeployingToThreeTerritories() throws IOException {
        String[] playerColors = {"Magenta", "Blue", "Red", "Green"};
        String territoryDeployedToFirstName = "Siam";
        String territoryDeployedToSecondName = "Argentina";
        initializeGameState(playerColors);
        setPlayerDeployableTroops(13);
        addTerritoryToPlayer(territoryDeployedToFirstName, 5);
        addTerritoryToPlayer(territoryDeployedToSecondName, 23);
        game.setGameState(GameState.DEPLOY);
        Territory territoryDeployedToFirst = territoriesByTerritoryName.get(territoryDeployedToFirstName);
        Territory territoryDeployedToSecond = territoriesByTerritoryName.get(territoryDeployedToSecondName);
        Point territoryDeployedToFirstPoint = pointsByTerritoryName.get(territoryDeployedToFirstName);
        Point territoryDeployedToSecondPoint = pointsByTerritoryName.get(territoryDeployedToSecondName);
        int expectedDeployableTroops = 0;
        int expectedTroopsOnFirstTerritory = 13;
        int expectedTroopsOnSecondTerritory = 28;
        GameState expectedGameState = GameState.NEWATTACK;

        game.clickedOnPoint(territoryDeployedToFirstPoint);
        game.updateDeployableTroops(3, game.getCurrentTerritory());
        game.clickedOnPoint(territoryDeployedToSecondPoint);
        game.updateDeployableTroops(5, game.getCurrentTerritory());
        game.clickedOnPoint(territoryDeployedToFirstPoint);
        game.updateDeployableTroops(5, game.getCurrentTerritory());

        int actualDeployableTroops = players.get(0).getDeployableTroops();
        int actualTroopsOnFirstTerritory = territoryDeployedToFirst.getCurrentNumberOfTroops();
        int actualTroopsOnSecondTerritory = territoryDeployedToSecond.getCurrentNumberOfTroops();
        GameState actualGameState = game.getGameState();
        assertEquals(expectedDeployableTroops, actualDeployableTroops);
        assertEquals(expectedTroopsOnFirstTerritory, actualTroopsOnFirstTerritory);
        assertEquals(expectedTroopsOnSecondTerritory, actualTroopsOnSecondTerritory);
        assertEquals(expectedGameState, actualGameState);
    }
}
