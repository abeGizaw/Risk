package mainApp.domain.IntegrationTests;

import mainApp.domain.Game;
import mainApp.domain.Initializer;
import mainApp.domain.Player;
import mainApp.domain.Territory;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class F5PlaceFurtherTroopsForSetupTest {
    private ArrayList<Player> players;
    private ArrayList<Territory> territories;
    private Game game;
    private ArrayList<Point> points;
    private final Dimension standardScreenSize = new Dimension(1536, 864);

    private void initializeGameState(String[] colors) throws IOException {
        initializePoints();
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Initializer initializer = new Initializer(standardScreenSize);
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        ResourceBundle message = ResourceBundle.getBundle("message");
        game = initializer.makeGame(players, false, message);
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t: initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
    }

    private void initializePoints() throws IOException {
        String territoryPointsFilePath = "src/main/java/data/territoryPoints.txt";
        TreeMap<String, Point> pointsByTerritoryName = new TreeMap<>();
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
            points = new ArrayList<>(pointsByTerritoryName.values());
            br.close();
        } catch (
                IOException e) {
            throw new IOException("Cannot find this file." + territoryPointsFilePath);
        }
    }

    private void shufflePointsAndTerritories() {
        HashMap<Point, Territory> pointToTerritoryMap = new HashMap<>(50);
        for (int i = 0; i < points.size(); i++) {
            pointToTerritoryMap.put(points.get(i), territories.get(i));
        }

        Collections.shuffle(points);
        territories = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            territories.add(pointToTerritoryMap.get(points.get(i)));
        }
    }

    private void claimInitialTerritories() {
        for (Point point : points) {
            game.clickedOnPoint(point);
        }
    }

    private void claimInitialTerritoriesInReverseOrder() {
        for (int i = points.size() - 1; i >= 0; i--) {
            game.clickedOnPoint(points.get(i));
        }
    }

    private int calculatePointIndex(int i, int[][] playerPointIndices) {
        int currentPlayerIndex = (i + 2) % players.size();
        int[] currentPlayerPoints = playerPointIndices[currentPlayerIndex];
        int playerPointIndex = i / players.size();
        playerPointIndex = playerPointIndex % currentPlayerPoints.length;
        return currentPlayerPoints[playerPointIndex];
    }

    @Test
    public void testPlaceFurtherTroopsForSetup_with63PointsHavingThreePlayers() throws IOException {
        String[] colors = {"Red", "Green", "Blue"};
        initializeGameState(colors);
        claimInitialTerritories();
        String expectedCurrentPlayer = "Red";
        int expectedDeployableTroops = 0;
        int expectedNumberOfTerritoriesOwned = 14;

        int pointIndex;
        for (int i = 0; i < 63; i++) {
            int currentPlayer = i % players.size();
            if (currentPlayer == 0) {
                pointIndex = i % points.size();
            } else if (currentPlayer == 1) {
                pointIndex = i % points.size();
                pointIndex = 41 - pointIndex;
            } else {
                pointIndex = 2;
            }
            game.clickedOnPoint(points.get(pointIndex));
        }

        String currentPlayer = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayer, currentPlayer);

        for (int i = 1; i < players.size(); i++) {
            assertEquals(expectedDeployableTroops, players.get(i).getDeployableTroops());
            assertEquals(expectedNumberOfTerritoriesOwned, players.get(i).territoryCount());
        }

        Territory territoryUnderTest;
        //Checking the first player's territories.
        for (int i = 0; i < territories.size(); i += 3) {
            territoryUnderTest = territories.get(i);
            if (i < 21) {
                assertEquals(3, territoryUnderTest.getCurrentNumberOfTroops());
            } else {
                assertEquals(2, territoryUnderTest.getCurrentNumberOfTroops());
            }
        }

        //Checking second player's territories.
        for (int i = 1; i < territories.size(); i+= 3) {
            territoryUnderTest = territories.get(i);
            if (i < 21) {
                assertEquals(2, territoryUnderTest.getCurrentNumberOfTroops());
            } else {
                assertEquals(3, territoryUnderTest.getCurrentNumberOfTroops());
            }
        }

        //Checking third player's territories.
        for (int i = 5; i < territories.size(); i+= 3) {
            territoryUnderTest = territories.get(i);
            assertEquals(1, territoryUnderTest.getCurrentNumberOfTroops());
        }
        territoryUnderTest = territories.get(2);
        assertEquals(22, territoryUnderTest.getCurrentNumberOfTroops());
    }

    @Test
    public void testPlaceFurtherTroopsForSetup_With78PointsInReverseAlphabeticalOrderAndOneUnassociatedPointHavingFourPlayers() throws IOException {
        String[] colors = {"Magenta", "Blue", "Black", "Green"};
        initializeGameState(colors);
        claimInitialTerritoriesInReverseOrder();
        int unassociatedPointIndex = 47;
        int[] playerOnePointIndices = {41, 37, 33, 29, 25, 21, 17, 13, 9, 5, 1};
        int[] playerTwoPointIndices = {40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};
        int[] playerThreePointIndices = {39, 35, 31, 27, 23, 19, 15, 11, 7, 3};
        int[] playerFourPointIndices = {38, 34, 30, 26, 22, 18, 14, 10, 6, 2};
        int[][] playerPointIndices = {playerOnePointIndices, playerTwoPointIndices, playerThreePointIndices, playerFourPointIndices};
        String expectedCurrentPlayerColor = "Magenta";
        int expectedDeployableTroops = 0;

        for (int i = 0; i < 78; i++) {
            int pointIndex = calculatePointIndex(i, playerPointIndices);
            game.clickedOnPoint(points.get(pointIndex));
            if (i == unassociatedPointIndex) {
                verifyUnassociatedPoint();
            }
        }

        for (int i = 0; i < players.size(); i++) {
            if (i < 2) {
                verifyPlayer1And1TerritoryInformationHaving4Players(i);
            } else {
                verifyPlayer3And4TerritoryInformationHaving4Players(i);
            }
        }

        for (int i = 1; i < players.size(); i++) {
            int deployableTroops = players.get(i).getDeployableTroops();
            assertEquals(expectedDeployableTroops, deployableTroops);
        }

        String currentPlayerColor = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayerColor, currentPlayerColor);
    }

    private void verifyUnassociatedPoint() {
        ArrayList<Integer> troopCounts = new ArrayList<>();
        for (Territory territory: territories) {
            troopCounts.add(territory.getCurrentNumberOfTroops());
        }
        String expectedCurrentPlayer = game.getCurrentPlayerColor();

        Point unassocatedPoint = new Point(0, 0);
        game.clickedOnPoint(unassocatedPoint);

        for (int i = 0; i < territories.size(); i++) {
            int expectedTroopCount = troopCounts.get(i);
            int troopCount = territories.get(i).getCurrentNumberOfTroops();
            assertEquals(expectedTroopCount, troopCount);
        }
        String currentPlayer = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayer, currentPlayer);
    }

    private void verifyPlayer1And1TerritoryInformationHaving4Players(int i) {
        int expectedTroopCount;
        for (int k = i; k < territories.size(); k += 4) {
            int troopCount = territories.get(k).getCurrentNumberOfTroops();
            if (k < 12) {
                expectedTroopCount = 2;
            } else {
                expectedTroopCount = 3;
            }
            assertEquals(expectedTroopCount, troopCount);
        }
    }

    private void verifyPlayer3And4TerritoryInformationHaving4Players(int i) {
        int expectedTroopCount = 3;
        for (int k = i; k < territories.size(); k += 4) {
            int troopCount = territories.get(k).getCurrentNumberOfTroops();
            assertEquals(expectedTroopCount, troopCount);
        }
    }

    @Test
    public void testF5PlaceFurtherTroopsForSetup_WithWith83PointsInRandomOrderAndFiveInvalidPointsHavingFivePlayers() throws IOException {
        String[] colors = {"Magenta", "Blue", "Red", "Black", "Green"};
        initializeGameState(colors);
        shufflePointsAndTerritories();
        claimInitialTerritories();
        String expectedCurrentPlayer = "Magenta";
        int expectedDeployableTroops = 0;
        int[] invalidPointIndices = {0, 1, 2, 3, 4};
        int[] triggerInvalidIndices = {10, 21, 27, 53, 69, -1};
        int currentInvalidIndex = 0;
        int[] playerOnePointIndices = {0, 5, 10, 15, 20, 25, 30, 35, 40};
        int[] playerTwoPointIndices = {1, 6, 11, 16, 21, 26, 31, 36, 41};
        int[] playerThreePointIndices = {2, 7, 12, 17, 22, 27, 32, 37};
        int[] playerFourPointIndices = {3, 8, 13, 18, 23, 28, 33, 38};
        int[] playerFivePointIndices = {4, 9, 14, 19, 24, 29, 34, 39};
        int[][] playerPointIndices = {playerOnePointIndices, playerTwoPointIndices, playerThreePointIndices,
                playerFourPointIndices, playerFivePointIndices};

        for (int i = 0; i < 83; i++) {
            int pointIndex = calculatePointIndex(i, playerPointIndices);
            game.clickedOnPoint(points.get(pointIndex));
            if (triggerInvalidIndices[currentInvalidIndex] == i) {
                verifyInvalidPoint(invalidPointIndices, currentInvalidIndex);
                currentInvalidIndex++;
            }
        }

        String currentPlayer = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayer, currentPlayer);

        for (int i = 0; i < players.size(); i++) {
            if (i < 2) {
                verifyPlayer1And2TerritoryInformationHaving5Players(i);
            } else {
                verifyPlayer3And4And5TerritoryInformationHaving5Players(i);
            }
        }

        for (int i = 1; i < players.size(); i++) {
            assertEquals(expectedDeployableTroops, players.get(i).getDeployableTroops());
        }
    }

    private void verifyInvalidPoint(int[] invalidPointIndices, int currentInvalidIndex) {
        ArrayList<Integer> expectedTroopCounts = new ArrayList<>();
        for (Territory currentTerritory: territories) {
            expectedTroopCounts.add(currentTerritory.getCurrentNumberOfTroops());
        }
        String expectedCurrentPlayer = game.getCurrentPlayerColor();
        String expectedErrorMessage = "This territory does not belong to you. Please choose one you occupy";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            int invalidPointIndex = invalidPointIndices[currentInvalidIndex];
            game.clickedOnPoint(points.get(invalidPointIndex));
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
        assertEquals(expectedCurrentPlayer, game.getCurrentPlayerColor());
        for (int i = 0; i < territories.size(); i++) {
            Territory currentTerritory = territories.get(i);
            assertEquals(expectedTroopCounts.get(i), currentTerritory.getCurrentNumberOfTroops());
        }
    }

    private void verifyPlayer1And2TerritoryInformationHaving5Players(int i) {
        int expectedTroopCount;
        for (int k = i; k < territories.size(); k += players.size()) {
            Territory currentTerritory = territories.get(k);
            if (k < 35) {
                expectedTroopCount = 3;
            } else {
                expectedTroopCount = 2;
            }
            assertEquals(expectedTroopCount, currentTerritory.getCurrentNumberOfTroops());
        }
    }

    private void verifyPlayer3And4And5TerritoryInformationHaving5Players(int i) {
        int expectedTroopCount;
        for (int k = i; k < territories.size(); k += players.size()) {
            Territory currentTerritory = territories.get(k);
            if (k < 5) {
                expectedTroopCount = 4;
            } else {
                expectedTroopCount = 3;
            }
            assertEquals(expectedTroopCount, currentTerritory.getCurrentNumberOfTroops());
        }
    }
}
