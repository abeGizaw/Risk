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

import static org.junit.jupiter.api.Assertions.*;

public class F6DrawSecretMissionCardsTest {
    private final Dimension standardScreenSize = new Dimension(1536, 864);
    HashMap<String, Point> pointsByTerritoryName;
    ArrayList<Player> players;
    ArrayList<Territory> territories;
    private Game game;
    private SecretMissionDeck secretMissionDeck;
    private Card[] originalSecretMissionCards;


    private void initializeGameState(String[] colors) throws IOException {
        constructGame(colors);
        initializePointsByTerritoryName();
        initializeOriginalSecretMissionCards();
        assignInitialTerritories();
        setDeployableTroops();
    }

    private void constructGame(String[] colors) throws IOException {
        Initializer initializer = new Initializer(standardScreenSize);
        initializer.createAllEntities();
        players = initializer.makePlayers(colors.length, new ArrayList<>(List.of(colors)));
        territories = new ArrayList<>(initializer.getTerritories());
        HashMap<Continent, Territory[]> continentMap = initializer.getTerritoriesByContinentMap();
        RiskDeck riskDeck = initializer.getRiskCards();
        Dice dice = new Dice(new Random());
        Attack attack = new Attack(dice);
        Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));
        secretMissionDeck = initializer.getSecretMissionCards();
        game = new Game(players, territories, continentMap, riskDeck, attack, maneuver, secretMissionDeck);
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

    private void initializeOriginalSecretMissionCards() {
        String line;
        originalSecretMissionCards = new Card[11];
        try {
            String missionCardFilePath = "src/main/java/data/SecretMissionCards/secretMissionCards.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(missionCardFilePath), Charset.defaultCharset()));
            int index = 0;
            while ((line = br.readLine()) != null){
                String[] missionCardInfo = line.split(",");
                String filePath = missionCardInfo[0];
                String type = missionCardInfo[1];
                String value = missionCardInfo[2];
                originalSecretMissionCards[index] = new Card(type, value, filePath);
                index++;
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDeployableTroops() {
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            if (i == 0) {
                currentPlayer.removeDeployableTroops(currentPlayer.getDeployableTroops() - 1);
            } else {
                currentPlayer.removeDeployableTroops(currentPlayer.getDeployableTroops());
            }
        }
    }

    private void assignInitialTerritories() {
        for (int i = 0; i < territories.size(); i++) {
            Player currentPlayer = players.get(i % players.size());
            currentPlayer.addTerritory(territories.get(i));
        }
    }

    private ArrayList<Card> generateSecretMissionCardsInOrder(int[] secretMissionCardsOrder) {
        ArrayList<Card> secretMissionCardsInOrder = new ArrayList<>();
        for (int cardIndex: secretMissionCardsOrder) {
            secretMissionCardsInOrder.add(originalSecretMissionCards[cardIndex]);
        }
        return secretMissionCardsInOrder;
    }

    private void validateSecretMissionCardsByPlayer(ArrayList<Card> secretMissionCardsByPlayer,
                                                    String[] expectedCardTypesByPlayer, String[] expectedCardValuesByPlayer) {
        for (int i = 0; i < expectedCardTypesByPlayer.length; i++) {
            Card secretMissionCard = secretMissionCardsByPlayer.get(i);
            assertEquals(secretMissionCard.type(), expectedCardTypesByPlayer[i]);
            assertEquals(secretMissionCard.value(), expectedCardValuesByPlayer[i]);
        }
    }

    @Test
    public void testDrawSecretMissionCards_withRGBDrawingCardsZeroSevenAndTen_expectingCardsZeroSevenAndTen() throws IOException {
        String[] playerColors = {"Red", "Green", "Blue"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        //Numbers in the test name and secretMissionCardsOrder refer to the card at the given index in
        //secretMissionCards.txt using zero indexing.
        int[] secretMissionCardsOrder = {0, 7, 10};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Conquer", "Destroy", "Destroy"};
        String[] expectedCardValuesByPlayer = {"ASIA-AFRICA", "Blue", "Red"};

        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }

    @Test
    public void testDrawSecretMissionCards_withRGBBLDrawingCardsZeroEightSevenNineFour_expectingCardsZeroEightSevenAndFour() throws IOException {
        String[] playerColors = {"Red", "Green", "Blue", "Black"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        int[] secretMissionCardsOrder = {0, 8, 7, 9, 4};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Conquer", "Destroy", "Destroy", "Control"};
        String[] expectedCardValuesByPlayer = {"ASIA-AFRICA", "Green", "Blue", "18TerritoriesWith2Troops"};
        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }

    @Test
    public void testDrawSecretMissionCards_withMBBLRGDrawingCardsNineSixSevenFiveAndTen_expectingCardsNineSixSevenFiveAndTen() throws IOException {
        String[] playerColors = {"Magenta", "Blue", "Black", "Red", "Green"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        int[] secretMissionCardsOrder = {9, 6, 7, 5, 10};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Destroy", "Destroy", "Destroy", "Control", "Destroy"};
        String[] expectedCardValuesByPlayer = {"Magenta", "Black", "Blue", "24Territories", "Red"};
        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }

    @Test
    public void testDrawSecretMissionCards_withBLBRDrawingCardsThreeEightOneNineSix_expectingCardsThreeOneAndSix() throws IOException {
        String[] playerColors = {"Black", "Blue", "Red"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        int[] secretMissionCardsOrder = {3, 8, 1, 9, 6};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Conquer", "Conquer", "Destroy"};
        String[] expectedCardValuesByPlayer = {"NORTH_AMERICA-AUSTRALIA", "ASIA-SOUTH_AMERICA", "Black"};
        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }

    @Test
    public void testDrawSecretMissionCards_withRGBMDrawingCardsFiveTwoSixSevenFour_expectingCardsFiveTwoSevenAndFour() throws IOException {
        String[] playerColors = {"Red", "Green", "Blue", "Magenta"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        int[] secretMissionCardsOrder = {5, 2, 6, 7, 4};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Control", "Conquer", "Destroy", "Control"};
        String[] expectedCardValuesByPlayer = {"24Territories", "NORTH_AMERICA-AFRICA", "Blue", "18TerritoriesWith2Troops"};
        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }

    @Test
    public void testDrawSecretMissionCards_withGMBLDrawingCardsInReverseOrder_expectingCardsNineEightAndSix() throws IOException {
        String[] playerColors = {"Green", "Magenta", "Black"};
        initializeGameState(playerColors);
        String territoryOwnedByCurrentPlayerName = territories.get(0).getTerritoryName();
        Point territoryOwnedByCurrentPlayerPoint = pointsByTerritoryName.get(territoryOwnedByCurrentPlayerName);
        int[] secretMissionCardsOrder = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        ArrayList<Card> secretMissionCardsInDrawOrder = generateSecretMissionCardsInOrder(secretMissionCardsOrder);
        secretMissionDeck.setCardOrder(secretMissionCardsInDrawOrder);
        String[] expectedCardTypesByPlayer = {"Destroy", "Destroy", "Destroy"};
        String[] expectedCardValuesByPlayer = {"Magenta", "Green", "Black"};
        game.clickedOnPoint(territoryOwnedByCurrentPlayerPoint);

        ArrayList<Card> secretMissionCardsByPlayer = game.getMissionCards();
        validateSecretMissionCardsByPlayer(secretMissionCardsByPlayer, expectedCardTypesByPlayer, expectedCardValuesByPlayer);
    }
}
