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

import static org.junit.jupiter.api.Assertions.*;

public class F4ClaimInitialTerritoriesTest {
    private ArrayList<Player> players;
    private ArrayList<Territory> territories;
    private Game game;
    private ArrayList<Point> points;
    private final Random random = new Random();
    private ArrayList<Integer> unassociatedPointsIndices;
    private Dimension standardScreenSize = new Dimension(1536, 864);

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

    private int addDuplicatePoint() {
        int firstIndexOfDuplicate = random.nextInt(points.size() - 1);
        int secondIndexOfDuplicate = random.nextInt(points.size() - firstIndexOfDuplicate - 1) + firstIndexOfDuplicate + 1;
        Point pointToBeDuplicated = points.get(firstIndexOfDuplicate);
        Point duplicatePoint = new Point(pointToBeDuplicated.x, pointToBeDuplicated.y);
        points.add(secondIndexOfDuplicate, duplicatePoint);
        return secondIndexOfDuplicate;
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

    private void shuffleInUnassociatedPoints(int numberOfUnassociatedPoints) {
        generateUnassociatedPointsIndices(numberOfUnassociatedPoints);
        shufflePointsAndTerritories();
        addInUnassociatedPoints();
    }

    private void generateUnassociatedPointsIndices(int numberOfUnassociatedPoints) {
        unassociatedPointsIndices = new ArrayList<>();
        int index;
        for (int i = 0; i < numberOfUnassociatedPoints; i++) {
            index = random.nextInt(points.size() + numberOfUnassociatedPoints);
            while (unassociatedPointsIndices.contains(index)) {
                index = random.nextInt(points.size() + numberOfUnassociatedPoints);
            }
            unassociatedPointsIndices.add(index);
        }
        Collections.sort(unassociatedPointsIndices);
    }

    private void addInUnassociatedPoints() {
        Point UnassociatedPoint;
        for (Integer pointIndex: unassociatedPointsIndices) {
            UnassociatedPoint = new Point(0, 0);
            points.add(pointIndex, UnassociatedPoint);
        }
    }

    private void validateUnassociatedPoint(int i) {
        int[] numberOfDeployableTroopsBeforeUnassociatedPoint = new int[players.size()];
        String currentPlayerBeforeUnassociatedPoint;

        for (int k = 0; k < players.size(); k++) {
            numberOfDeployableTroopsBeforeUnassociatedPoint[k] = players.get(k).getDeployableTroops();
        }
        currentPlayerBeforeUnassociatedPoint = game.getCurrentPlayerColor();

        game.clickedOnPoint(points.get(i));

        for (int k = 0; k < players.size(); k++) {
            assertEquals(numberOfDeployableTroopsBeforeUnassociatedPoint[k], players.get(k).getDeployableTroops());
        }
        assertEquals(currentPlayerBeforeUnassociatedPoint, game.getCurrentPlayerColor());
    }

    @Test
    public void testF4ClaimInitialTerritories_with42PointsInAlphabeticalOrderAndThreePlayers() throws IOException {
        String[] colors = {"Red", "Green", "Blue"};
        initializeGameState(colors);
        String expectedCurrentPlayer = "Red";
        int expectedDeployableTroops = 21;
        int expectedNumberOfTerritoriesOwned = 14;

        for (int i = 0; i < points.size(); i++) {
            game.clickedOnPoint(points.get(i));
        }

        /*
        Because there are three players, player 0 should own territories 0, 3, 6, and so on.
        Player 1 owns territories 1, 4, 7, and so on. More generally player i own territory j where j % 3 == i;
         */
        for (int i = 0; i < players.size(); i++) {
            for (int k = 0; k < territories.size(); k++) {
                if (k % players.size() == i) {
                    assertTrue(players.get(i).ownsTerritory(territories.get(k)));
                    assertEquals(1, territories.get(i).getCurrentNumberOfTroops());
                }
            }
        }

        String currentPlayer = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayer, currentPlayer);

        for (Player player: players) {
            int numberOfTerritoriesOwned = player.territoryCount();
            int deployableTroops = player.getDeployableTroops();
            assertEquals(expectedNumberOfTerritoriesOwned, numberOfTerritoriesOwned);
            assertEquals(expectedDeployableTroops, deployableTroops);
        }
    }

    @Test
    public void testF4ClaimInitialTerritories_with42PointsInReverseAlphabeticalOrderAndFourPlayers() throws IOException {
        String[] colors = {"Magenta", "Blue", "Black", "Green"};
        initializeGameState(colors);
        String expectedCurrentPlayerColor = "Black";
        int expectedPlayerOneAndTwoDeployableTroops = 19;
        int expectedPlayerThreeAndFourDeployableTroops = 20;
        int expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo = 11;
        int expectedNumberOfTerritoriesOwnedByPlayerThreeAndFour = 10;

        for (int i = points.size() - 1; i >= 0; i--) {
            game.clickedOnPoint(points.get(i));
        }

        /*
        Because there are four players and territories are clicked on in reverse order, player 0 should own
        territories 41, 37, 33, And so. Player 3 owns territories 38, 34, 30, and so on. More generally,
        player i owns territory j where j % 4 == (1 - i) % 4
         */
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            for (int k = territories.size() - 1; k >= 0; k--) {
                if (k % players.size() == ((1 - i) % players.size())) {
                    assertTrue(currentPlayer.ownsTerritory(territories.get(k)));
                    assertEquals(1, territories.get(i).getCurrentNumberOfTroops());
                }
            }
        }

        String currentPlayerColor = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayerColor, currentPlayerColor);

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int deployableTroops = currentPlayer.getDeployableTroops();
            int numberOfTerritoriesOwned = currentPlayer.territoryCount();
            if (i < 2) {
                assertEquals(expectedPlayerOneAndTwoDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo, numberOfTerritoriesOwned);
            } else {
                assertEquals(expectedPlayerThreeAndFourDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerThreeAndFour, numberOfTerritoriesOwned);
            }
        }
    }

    @Test
    public void testF4ClaimInitialTerritories_with42PointsInRandomOrderAndFivePlayers() throws IOException {
        String[] colors = {"Black", "Red", "Magenta", "Green", "Blue"};
        initializeGameState(colors);
        String expectedCurrentPlayerColor = "Magenta";
        int expectedPlayerOneAndTwoDeployableTroops = 16;
        int expectedPlayerThreeFourAndFiveDeployableTroops = 17;
        int expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo = 9;
        int expectedNumberOfTerritoriesOwnedByPlayerThreeFourAndFive = 8;

        shufflePointsAndTerritories();
        for (Point point: points) {
            game.clickedOnPoint(point);
        }

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            for (int k = 0; k < territories.size(); k++) {
                if (k % players.size() == i) {
                    Territory currentTerritory = territories.get(k);
                    assertTrue(currentPlayer.ownsTerritory(currentTerritory));
                    assertEquals(1, currentTerritory.getCurrentNumberOfTroops());
                }
            }
        }

        String currentPlayerColor = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayerColor, currentPlayerColor);

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int deployableTroops = currentPlayer.getDeployableTroops();
            int numberOfTerritoriesOwned = currentPlayer.territoryCount();
            if (i < 2) {
                assertEquals(expectedPlayerOneAndTwoDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo, numberOfTerritoriesOwned);
            } else {
                assertEquals(expectedPlayerThreeFourAndFiveDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerThreeFourAndFive, numberOfTerritoriesOwned);
            }
        }
    }

    @Test
    public void testF4ClaimInitialTerritories_with43PointsContainingOneDuplicateInRandomOrderAnd4Players() throws IOException {
        String[] colors = {"Red", "Black", "Blue", "Green"};
        initializeGameState(colors);
        String expectedCurrentPlayerColor = "Blue";
        int expectedPlayerOneAndTwoDeployableTroops = 19;
        int expectedPlayerThreeAndFourDeployableTroops = 20;
        int expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo = 11;
        int expectedNumberOfTerritoriesOwnedByPlayerThreeAndFour = 10;
        String expectedErrorMessage = "This territory is already occupied. Choose a different territory";

        shufflePointsAndTerritories();
        int duplicateSecondIndex = addDuplicatePoint();

        for (int i = 0; i < points.size(); i++) {
            System.out.println("Current player color: " + game.getCurrentPlayerColor());
            if (i == duplicateSecondIndex) {
                Exception exception = assertThrows(IllegalStateException.class, ()-> {
                    game.clickedOnPoint(points.get(duplicateSecondIndex));
                });

                String errorMessage = exception.getMessage();
                assertEquals(expectedErrorMessage, errorMessage);
            } else {
                game.clickedOnPoint(points.get(i));
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            for (int k = territories.size() - 1; k >= 0; k--) {
                if (k % players.size() == i) {
                    assertTrue(currentPlayer.ownsTerritory(territories.get(k)));
                    assertEquals(1, territories.get(i).getCurrentNumberOfTroops());
                }
            }
        }



        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int deployableTroops = currentPlayer.getDeployableTroops();
            int numberOfTerritoriesOwned = currentPlayer.territoryCount();
            if (i < 2) {
                assertEquals(expectedPlayerOneAndTwoDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo, numberOfTerritoriesOwned);
            } else {
                assertEquals(expectedPlayerThreeAndFourDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerThreeAndFour, numberOfTerritoriesOwned);
            }
        }

        String currentPlayerColor = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayerColor, currentPlayerColor);
    }

    @Test
    public void testF4ClaimInitialTerritories_withPointsUnassociatedWithTerritoriesAnd5Players() throws IOException {
        String[] colors = {"Black", "Red", "Green", "Blue", "Magenta"};
        initializeGameState(colors);
        String expectedCurrentPlayerColor = "Green";
        int expectedPlayerOneAndTwoDeployableTroops = 16;
        int expectedPlayerThreeFourAndFiveDeployableTroops = 17;
        int expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo = 9;
        int expectedNumberOfTerritoriesOwnedByPlayerThreeFourAndFive = 8;

        int numberOfUnassociatedPoints = random.nextInt(10) + 1;
        shuffleInUnassociatedPoints(numberOfUnassociatedPoints);

        for (int i = 0; i < points.size(); i++) {
            if (unassociatedPointsIndices.contains(i)) {
                validateUnassociatedPoint(i);
            } else {
                game.clickedOnPoint(points.get(i));
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            for (int k = 0; k < territories.size(); k++) {
                if (k % players.size() == i) {
                    Territory currentTerritory = territories.get(k);
                    assertTrue(currentPlayer.ownsTerritory(currentTerritory));
                    assertEquals(1, currentTerritory.getCurrentNumberOfTroops());
                }
            }
        }

        String currentPlayerColor = game.getCurrentPlayerColor();
        assertEquals(expectedCurrentPlayerColor, currentPlayerColor);

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int deployableTroops = currentPlayer.getDeployableTroops();
            int numberOfTerritoriesOwned = currentPlayer.territoryCount();
            if (i < 2) {
                assertEquals(expectedPlayerOneAndTwoDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerOneAndTwo, numberOfTerritoriesOwned);
            } else {
                assertEquals(expectedPlayerThreeFourAndFiveDeployableTroops, deployableTroops);
                assertEquals(expectedNumberOfTerritoriesOwnedByPlayerThreeFourAndFive, numberOfTerritoriesOwned);
            }
        }
    }

    @Test
    public void testRandomShuffleMultipleTimes() throws IOException {
        for (int i = 0; i < 50; i++) {
            testF4ClaimInitialTerritories_with42PointsInRandomOrderAndFivePlayers();
            testF4ClaimInitialTerritories_with43PointsContainingOneDuplicateInRandomOrderAnd4Players();
            testF4ClaimInitialTerritories_withPointsUnassociatedWithTerritoriesAnd5Players();
        }
    }
}
