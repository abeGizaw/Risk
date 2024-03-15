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

public class F7ReceiveDeployableTroopsTest {
    private ArrayList<Player> players;
    private ArrayList<Territory> territories;
    private Game game;
    private HashMap<Continent, ArrayList<Territory>> territoriesByContinentMap;
    private HashMap<String, Point> pointsByTerritoryName;
    private final Dimension standardScreenSize = new Dimension(1536, 864);

    private void initializeGameState(String[] colors) throws IOException {
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Initializer initializer = new Initializer(standardScreenSize);
        initializePointsByTerritoryName();
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        ResourceBundle message = ResourceBundle.getBundle("message");
        game = initializer.makeGame(players, false, message);
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t: initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        initializeContinentsByTerritory();
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

    private void initializeContinentsByTerritory() {
        String line;
        HashMap<String, Territory> nameToTerritoryMap = new HashMap<>();
        for (Territory territory : territories) {
            nameToTerritoryMap.put(territory.getTerritoryName(), territory);
        }
        territoriesByContinentMap = new HashMap<>();
        try {
            String territoryByContentFilePath = "src/main/java/data/territoriesByContenint.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryByContentFilePath), Charset.defaultCharset()));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] territoryNames = line.split(",");
                ArrayList<Territory> continentTerritories = new ArrayList<>();
                for (int i = 1; i < territoryNames.length; i++) {
                    String territoryName = territoryNames[i];
                    Territory currentTerritory = nameToTerritoryMap.get(territoryName);
                    continentTerritories.add(currentTerritory);
                }
                Continent continent = nameToContinent(territoryNames[0]);
                territoriesByContinentMap.put(continent, continentTerritories);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Continent nameToContinent(String continentName) {
        if (continentName.equals("AFRICA")){
            return Continent.AFRICA;
        }else if (continentName.equals(Continent.ASIA.toString())){
            return Continent.ASIA;
        }else if (continentName.equals(Continent.AUSTRALIA.toString())){
            return Continent.AUSTRALIA;
        }if (continentName.equals(Continent.NORTH_AMERICA.toString())){
            return Continent.NORTH_AMERICA;
        }else if (continentName.equals(Continent.SOUTH_AMERICA.toString())){
            return Continent.SOUTH_AMERICA;
        }else if (continentName.equals("EUROPE")){
            return Continent.EUROPE;
        }else {
            return null;
        }
    }

    private void setDeployableTroops(int[] deployableTroopValues) {
        if (players.size() != deployableTroopValues.length) {
            fail();
        }
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int currentDeployableTroops = currentPlayer.getDeployableTroops();
            currentPlayer.removeDeployableTroops(currentDeployableTroops - deployableTroopValues[i]);
        }
    }

    public void assignContinent(int playerNumber, Continent assignedContinent) {
        int territoriesInContinent = territoriesByContinentMap.get(assignedContinent).size();
        int[] territoriesByPlayer = new int[territoriesInContinent];
        Arrays.fill(territoriesByPlayer, playerNumber);
        assignContinentByTerritory(territoriesByPlayer, assignedContinent);
    }

    private void assignContinentByTerritory(int[] territoriesByPlayer, Continent assignedContinent) {
        ArrayList<Territory> continentTerritories = territoriesByContinentMap.get(assignedContinent);
        if (territoriesByPlayer.length != continentTerritories.size()) {
            fail();
        }
        for (int i = 0; i < territoriesByPlayer.length; i++) {
            int currentPlayerNumber = territoriesByPlayer[i];
            if (currentPlayerNumber == 0) {
                continue;
            }
            Player currentPlayer = players.get(currentPlayerNumber - 1);
            Territory currentTerritory = continentTerritories.get(i);
            currentTerritory.addAdditionalTroops(1);
            currentPlayer.addTerritory(currentTerritory);
        }
    }

    private void verifyOwnsContinents(boolean[] expectedOwnsContinents, int playerNumber) {
        verifyOwnsSpecificContinent(expectedOwnsContinents[0], playerNumber, Continent.NORTH_AMERICA);
        verifyOwnsSpecificContinent(expectedOwnsContinents[1], playerNumber, Continent.SOUTH_AMERICA);
        verifyOwnsSpecificContinent(expectedOwnsContinents[2], playerNumber, Continent.EUROPE);
        verifyOwnsSpecificContinent(expectedOwnsContinents[3], playerNumber, Continent.ASIA);
        verifyOwnsSpecificContinent(expectedOwnsContinents[4], playerNumber, Continent.AFRICA);
        verifyOwnsSpecificContinent(expectedOwnsContinents[5], playerNumber, Continent.AUSTRALIA);

    }

    private void verifyOwnsSpecificContinent(boolean expectedOwnsContinent, int playerNumber, Continent continent) {
        boolean ownsContinent = true;
        ArrayList<Territory> territoriesInContinent = territoriesByContinentMap.get(continent);
        Player currentPlayer = players.get(playerNumber - 1);
        for (Territory territory: territoriesInContinent) {
            if (!currentPlayer.ownsTerritory(territory)) {
                ownsContinent = false;
                break;
            }
        }
        assertEquals(expectedOwnsContinent, ownsContinent);
    }

    private void verifyOwnsTerritoriesByName(String[] expectedTerritoriesOwnedByName, int playerNumber) {
        Player currentPlayer = players.get(playerNumber - 1);
        for (Territory territory: territories) {
            String territoryName = territory.getTerritoryName();
            for (String ownedTerritoryName : expectedTerritoriesOwnedByName) {
                if (territoryName.equals(ownedTerritoryName)) {
                    assertTrue(currentPlayer.ownsTerritory(territory));
                }
            }
        }
    }

    @Test
    public void testReceiveDeployableTroops_WithPlayerOwningOneTerritoryAndNoContinents() throws IOException {
        String[] colors = {"Red", "Green", "Blue"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] australiaTerritoriesByPlayer = {1, 1, 1, 2};
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point westernAustraliaPoint = pointsByTerritoryName.get("Western Australia");
        String expectedPlayerColor = "Green";
        int expectedDeployableTroops = 3;
        int expectedTerritoryCount = 1;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, false};

        game.clickedOnPoint(westernAustraliaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_WithPlayerOwningTwoTerritoriesAndNoContinents() throws IOException {
        String[] colors = {"Red", "Green", "Blue"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] northAmericaTerritoriesByPlayer = {1, 2, 3, 1, 1, 3, 1, 1, 3};
        int[] southAmericaTerritoriesByPlayer = {1, 1, 1, 2};
        assignContinentByTerritory(northAmericaTerritoriesByPlayer, Continent.NORTH_AMERICA);
        assignContinentByTerritory(southAmericaTerritoriesByPlayer, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(1, Continent.AUSTRALIA);
        Point egyptPoint = pointsByTerritoryName.get("Egypt");
        String expectedPlayerColor = "Green";
        int expectedDeployableTroops = 3;
        int expectedTerritoryCount = 2;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, false};

        game.clickedOnPoint(egyptPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_WithPlayerOwningNineTerritoriesAndNoContinents() throws IOException {
        String[] colors = {"Green", "Red", "Blue"};
        int[] initialDeployableTroops = {1, 1, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] northAmericaTerritoriesByPlayer = {1, 2, 3, 1, 1, 3, 1, 1, 3};
        int[] southAmericaTerritoriesByPlayer = {1, 1, 1, 2};
        int[] europeTerritoriesByPlayer = {3, 2, 3, 2, 1, 2, 3};
        int[] asiaTerritoriesByPlayer = {1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 2};
        assignContinentByTerritory(northAmericaTerritoriesByPlayer, Continent.NORTH_AMERICA);
        assignContinentByTerritory(southAmericaTerritoriesByPlayer, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinentByTerritory(asiaTerritoriesByPlayer, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(1, Continent.AUSTRALIA);
        Point greatBritainPoint = pointsByTerritoryName.get("Great Britain");
        Point middleEastPoint = pointsByTerritoryName.get("Middle East");
        String expectedPlayerColor = "Blue";
        int expectedDeployableTroops = 3;
        int expectedTerritoryCount = 9;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, false};

        game.clickedOnPoint(middleEastPoint);
        game.clickedOnPoint(greatBritainPoint);

        Player currentPlayer = players.get(2);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 3);
    }

    @Test
    public void testReceiveDeployableTroops_WithPlayerOwning13TerritoriesAndNoContinents() throws IOException {
        String[] colors = {"Green", "Magenta", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] europeTerritoriesByPlayer = {1, 2, 1, 2, 1, 2, 3};
        int[] asiaTerritoriesByPlayer = {2, 3, 1, 3, 2, 3, 2, 1, 2, 1, 2, 3};
        int[] africaTerritoriesByPlayer = {2, 1, 2, 2, 1, 1};
        int[] australiaTerritoriesByPlayer = {2, 1, 1, 2};
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinentByTerritory(asiaTerritoriesByPlayer, Continent.ASIA);
        assignContinentByTerritory(africaTerritoriesByPlayer, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point westernAustraliaPoint = pointsByTerritoryName.get("Western Australia");
        String expectedPlayerColor = "Magenta";
        int expectedDeployableTroops = 4;
        int expectedTerritoryCount = 13;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, false};

        game.clickedOnPoint(westernAustraliaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning36TerritoriesAndNoContinents() throws IOException {
        String[] colors = {"Green", "Magenta", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] northAmericaTerritoriesByPlayer = {1, 2, 2, 2, 2, 2, 2, 2, 2};
        int[] southAmericaTerritoriesByPlayer = {1, 2, 2, 2};
        int[] europeTerritoriesByPlayer = {3, 2, 2, 2, 2, 2, 2};
        int[] asiaTerritoriesByPlayer = {2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2};
        int[] africaTerritoriesByPlayer = {2, 2, 2, 2, 2, 1};
        int[] australiaTerritoriesByPlayer = {2, 1, 2, 2};
        assignContinentByTerritory(northAmericaTerritoriesByPlayer, Continent.NORTH_AMERICA);
        assignContinentByTerritory(southAmericaTerritoriesByPlayer, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinentByTerritory(asiaTerritoriesByPlayer, Continent.ASIA);
        assignContinentByTerritory(africaTerritoriesByPlayer, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point madagascarPoint = pointsByTerritoryName.get("Madagascar");
        String expectedPlayerColor = "Magenta";
        int expectedDeployableTroops = 12;
        int expectedTerritoryCount = 36;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, false};

        game.clickedOnPoint(madagascarPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningNineTerritoriesAndOnlyNA() throws IOException {
        String[] colors = {"Green", "Magenta", "Black"};
        int[] initialDeployableTroops = {1, 1, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point madagascarPoint = pointsByTerritoryName.get("Madagascar");
        Point uralPoint = pointsByTerritoryName.get("Ural");
        String expectedPlayerColor = "Black";
        int expectedDeployableTroops = 8;
        int expectedTerritoryCount = 9;
        boolean[] expectedOwnsContinents = {true, false, false, false, false, false};

        game.clickedOnPoint(madagascarPoint);
        game.clickedOnPoint(uralPoint);

        Player currentPlayer = players.get(2);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 3);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningFourTerritoriesAndOnlySA() throws IOException {
        String[] colors = {"Green", "Magenta", "Black", "Blue", "Red"};
        int[] initialDeployableTroops = {1, 1, 1, 1, 1};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(4, Continent.ASIA);
        assignContinent(5, Continent.AFRICA);
        assignContinent(5, Continent.AUSTRALIA);
        Point venezuelaPoint = pointsByTerritoryName.get("Venezuela");
        Point alaskaPoint = pointsByTerritoryName.get("Alaska");
        Point ukrainePoint = pointsByTerritoryName.get("Ukraine");
        Point uralPoint = pointsByTerritoryName.get("Ural");
        Point madagascarPoint = pointsByTerritoryName.get("Madagascar");
        String expectedPlayerColor = "Green";
        int expectedDeployableTroops = 5;
        int expectedTerritoryCount = 4;
        boolean[] expectedOwnsContinents = {false, true, false, false, false, false};

        game.clickedOnPoint(venezuelaPoint);
        game.clickedOnPoint(alaskaPoint);
        game.clickedOnPoint(ukrainePoint);
        game.clickedOnPoint(uralPoint);
        game.clickedOnPoint(madagascarPoint);

        Player currentPlayer = players.get(0);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 1);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningSevenTerritoriesAndOnlyUE() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point madagascarPoint = pointsByTerritoryName.get("Madagascar");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 8;
        int expectedTerritoryCount = 7;
        boolean[] expectedOwnsContinents = {false, false, true, false, false, false};

        game.clickedOnPoint(madagascarPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning12TerritoriesAndOnlyAS() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point peruPoint = pointsByTerritoryName.get("Peru");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 11;
        int expectedTerritoryCount = 12;
        boolean[] expectedOwnsContinents = {false, false, false, true, false, false};

        game.clickedOnPoint(peruPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningSixTerritoriesAndOnlyAF() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point peruPoint = pointsByTerritoryName.get("Peru");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 6;
        int expectedTerritoryCount = 6;
        boolean[] expectedOwnsContinents = {false, false, false, false, true, false};

        game.clickedOnPoint(peruPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningFourTerritoriesAndOnlyAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(3, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point peruPoint = pointsByTerritoryName.get("Peru");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 5;
        int expectedTerritoryCount = 4;
        boolean[] expectedOwnsContinents = {false, false, false, false, false, true};

        game.clickedOnPoint(peruPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning19TerritoriesAndContinentsEUAndAs() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point peruPoint = pointsByTerritoryName.get("Peru");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 18;
        int expectedTerritoryCount = 19;
        boolean[] expectedOwnsContinents = {false, false, true, true, false, false};

        game.clickedOnPoint(peruPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning16TerritoriesAndContinentsASAndAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(3, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point peruPoint = pointsByTerritoryName.get("Peru");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 14;
        int expectedTerritoryCount = 16;
        boolean[] expectedOwnsContinents = {false, false, false, true, false, true};

        game.clickedOnPoint(peruPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning13TerritoriesAndContinentsNAAndSA() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point chinaPoint = pointsByTerritoryName.get("China");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 11;
        int expectedTerritoryCount = 13;
        boolean[] expectedOwnsContinents = {true, true, false, false, false, false};

        game.clickedOnPoint(chinaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning15TerritoriesAndContinentsNAAndAF() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point chinaPoint = pointsByTerritoryName.get("China");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 13;
        int expectedTerritoryCount = 15;
        boolean[] expectedOwnsContinents = {true, false, false, false, true, false};

        game.clickedOnPoint(chinaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningEightTerritoriesAndContinentsSAAndAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(3, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point chinaPoint = pointsByTerritoryName.get("China");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 7;
        int expectedTerritoryCount = 8;
        boolean[] expectedOwnsContinents = {false, true, false, false, false, true};

        game.clickedOnPoint(chinaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning10TerritoriesAndContinentsAFAndSA() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(3, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point chinaPoint = pointsByTerritoryName.get("China");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 8;
        int expectedTerritoryCount = 10;
        boolean[] expectedOwnsContinents = {false, true, false, false, true, false};

        game.clickedOnPoint(chinaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning33TerritoriesAndAllButNA() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point northWestTerritoryPoint = pointsByTerritoryName.get("North West Territory");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 30;
        int expectedTerritoryCount = 33;
        boolean[] expectedOwnsContinents = {false, true, true, true, true, true};

        game.clickedOnPoint(northWestTerritoryPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning38TerritoriesAndAllButSA() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(1, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point brazilPoint = pointsByTerritoryName.get("Brazil");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 34;
        int expectedTerritoryCount = 38;
        boolean[] expectedOwnsContinents = {true, false, true, true, true, true};

        game.clickedOnPoint(brazilPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning35TerritoriesAndAllButEU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point scandinaviaPoint = pointsByTerritoryName.get("Scandinavia");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 30;
        int expectedTerritoryCount = 35;
        boolean[] expectedOwnsContinents = {true, true, false, true, true, true};

        game.clickedOnPoint(scandinaviaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning30TerritoriesAndAllButAS() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(1, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point japanPoint = pointsByTerritoryName.get("Japan");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 27;
        int expectedTerritoryCount = 30;
        boolean[] expectedOwnsContinents = {true, true, true, false, true, true};

        game.clickedOnPoint(japanPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning36TerritoriesAndAllButAF() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(1, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point congoPoint = pointsByTerritoryName.get("Congo");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 33;
        int expectedTerritoryCount = 36;
        boolean[] expectedOwnsContinents = {true, true, true, true, false, true};

        game.clickedOnPoint(congoPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning38TerritoriesAndAllButAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(1, Continent.AUSTRALIA);
        Point newGuineaPoint = pointsByTerritoryName.get("New Guinea");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 34;
        int expectedTerritoryCount = 38;
        boolean[] expectedOwnsContinents = {true, true, true, true, true, false};

        game.clickedOnPoint(newGuineaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning41TerritoriesAndAllButAS() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] asiaTerritoriesByPlayer = {2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinentByTerritory(asiaTerritoriesByPlayer, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point siamPoint = pointsByTerritoryName.get("Siam");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 30;
        int expectedTerritoryCount = 41;
        boolean[] expectedOwnsContinents = {true, true, true, false, true, true};

        game.clickedOnPoint(siamPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning41TerritoriesAndAllButAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] australiaTerritoriesByPlayer = {1, 2, 2, 2};
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point indonesiaPoint = pointsByTerritoryName.get("Indonesia");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 35;
        int expectedTerritoryCount = 41;
        boolean[] expectedOwnsContinents = {true, true, true, true, true, false};

        game.clickedOnPoint(indonesiaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning39TerritoriesAndAllButAU() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] australiaTerritoriesByPlayer = {1, 3, 2, 3};
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinent(2, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point indonesiaPoint = pointsByTerritoryName.get("Indonesia");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 35;
        int expectedTerritoryCount = 39;
        boolean[] expectedOwnsContinents = {true, true, true, true, true, false};

        game.clickedOnPoint(indonesiaPoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwning41TerritoriesAndAllButEU() throws IOException{
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] europeTerritoriesByPlayer = {2, 2, 2, 2, 2, 1, 2};
        assignContinent(2, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinent(2, Continent.AFRICA);
        assignContinent(2, Continent.AUSTRALIA);
        Point northernEuropePoint = pointsByTerritoryName.get("Northern Europe");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 32;
        int expectedTerritoryCount = 41;
        boolean[] expectedOwnsContinents = {true, true, false, true, true, true};

        game.clickedOnPoint(northernEuropePoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningEgyptGreenlandBrazilAndAS() throws IOException {
        String[] colors = {"Green", "Red", "Black"};
        int[] initialDeployableTroops = {1, 0, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] northAmericaTerritoriesByPlayer = {1, 3, 1, 3, 1, 3, 1, 3, 2};
        int[] southAmericaTerritoriesByPlayer = {1, 3, 2, 3};
        int[] africaTerritoriesByPlayer = {2, 3, 1, 3, 3, 1};
        assignContinentByTerritory(northAmericaTerritoriesByPlayer, Continent.NORTH_AMERICA);
        assignContinentByTerritory(southAmericaTerritoriesByPlayer, Continent.SOUTH_AMERICA);
        assignContinent(1, Continent.EUROPE);
        assignContinent(2, Continent.ASIA);
        assignContinentByTerritory(africaTerritoriesByPlayer, Continent.AFRICA);
        assignContinent(3, Continent.AUSTRALIA);
        Point northernEuropePoint = pointsByTerritoryName.get("Northern Europe");
        String expectedPlayerColor = "Red";
        int expectedDeployableTroops = 12;
        int expectedTerritoryCount = 15;
        boolean[] expectedOwnsContinents = {false, false, false, true, false, false};
        String[] expectedTerritoriesOwnedByName = {"Egypt", "Greenland", "Brazil"};

        game.clickedOnPoint(northernEuropePoint);

        Player currentPlayer = players.get(1);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 2);
        verifyOwnsTerritoriesByName(expectedTerritoriesOwnedByName, 2);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningIndonesiaNewGuineaGreatBritainAndAS() throws IOException {
        String[] colors = {"Green", "Red", "Black", "Magenta"};
        int[] initialDeployableTroops = {1, 1, 1, 0};
        initializeGameState(colors);
        int[] europeTerritoriesByPlayer = {3, 4, 1, 2, 3, 1, 2};
        int[] australiaTerritoriesByPlayer = {4, 4, 3, 2};
        setDeployableTroops(initialDeployableTroops);
        assignContinent(1, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinent(4, Continent.ASIA);
        assignContinent(3, Continent.AFRICA);
        assignContinentByTerritory(australiaTerritoriesByPlayer, Continent.AUSTRALIA);
        Point alaskaPoint = pointsByTerritoryName.get("Alaska");
        Point brazilPoint = pointsByTerritoryName.get("Brazil");
        Point northAfricaPoint = pointsByTerritoryName.get("North Africa");
        String expectedPlayerColor = "Magenta";
        int expectedDeployableTroops = 12;
        int expectedTerritoryCount = 15;
        boolean[] expectedOwnsContinents = {false, false, false, true, false, false};
        String[] expectedTerritoriesOwnedByName = {"Indonesia", "New Guinea", "Great Britain"};

        game.clickedOnPoint(alaskaPoint);
        game.clickedOnPoint(brazilPoint);
        game.clickedOnPoint(northAfricaPoint);

        Player currentPlayer = players.get(3);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 4);
        verifyOwnsTerritoriesByName(expectedTerritoriesOwnedByName, 4);
    }

    @Test
    public void testReceiveDeployableTroops_withPlayerOwningCongoEasternUnitedStatesIceLandAndAS() throws IOException {
        String[] colors = {"Green", "Red", "Black", "Magenta", "Blue"};
        int[] initialDeployableTroops = {1, 1, 1, 1, 0};
        initializeGameState(colors);
        setDeployableTroops(initialDeployableTroops);
        int[] northAmericaTerritoriesByPlayer = {1, 2, 3, 4, 1, 2, 4, 5, 3};
        int[] europeTerritoriesByPlayer = {5, 4, 3, 2, 1, 2, 3};
        int[] africaTerritoriesByPlayer = {3, 2, 1, 5, 4, 2};
        assignContinentByTerritory(northAmericaTerritoriesByPlayer, Continent.NORTH_AMERICA);
        assignContinent(2, Continent.SOUTH_AMERICA);
        assignContinentByTerritory(europeTerritoriesByPlayer, Continent.EUROPE);
        assignContinent(5, Continent.ASIA);
        assignContinentByTerritory(africaTerritoriesByPlayer, Continent.AFRICA);
        assignContinent(1, Continent.AUSTRALIA);
        Point indonesiaPoint = pointsByTerritoryName.get("Indonesia");
        Point brazilPoint = pointsByTerritoryName.get("Brazil");
        Point egyptPoint = pointsByTerritoryName.get("Egypt");
        Point southAfricaPoint = pointsByTerritoryName.get("South Africa");
        String expectedPlayerColor = "Blue";
        int expectedDeployableTroops = 12;
        int expectedTerritoryCount = 15;
        boolean[] expectedOwnsContinents = {false, false, false, true, false, false};
        String[] expectedTerritoriesOwnedByName = {"Congo", "EasternUnitedStates", "Ice Land"};

        game.clickedOnPoint(indonesiaPoint);
        game.clickedOnPoint(brazilPoint);
        game.clickedOnPoint(egyptPoint);
        game.clickedOnPoint(southAfricaPoint);

        Player currentPlayer = players.get(4);
        String playerColor = game.getCurrentPlayerColor();

        assertEquals(GameState.DEPLOY, game.getGameState());
        assertEquals(expectedPlayerColor, playerColor);
        assertEquals(expectedDeployableTroops, currentPlayer.getDeployableTroops());
        assertEquals(expectedTerritoryCount, currentPlayer.territoryCount());
        verifyOwnsContinents(expectedOwnsContinents, 5);
        verifyOwnsTerritoriesByName(expectedTerritoriesOwnedByName, 5);
    }
}
