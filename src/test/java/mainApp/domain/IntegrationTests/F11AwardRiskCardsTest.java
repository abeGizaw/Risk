package mainApp.domain.IntegrationTests;

import mainApp.domain.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class F11AwardRiskCardsTest {

    private ArrayList<Player> players = new ArrayList<>();

    private RiskDeck riskCards;
    private Collection<Territory> allTerritories;
    private final Dimension standardScreenSize = new Dimension(1536, 864);
    private final String[] colors = {"Red", "Green", "Blue"};
    private Game game;

    private Territory player1MainTerritory;
    private Territory player1AttacksThis;
    private Territory player1AlsoAttackThis;

    private Territory player2AttacksThis;


    private HashMap<String, Point> pointsByTerritoryName;

    private final Dice testDice = EasyMock.mock(Dice.class);
    private HashMap<Continent, Territory[]> allContinents;


    private void initializeGameState() throws IOException {
        initializePoints();
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Initializer initializer = new Initializer(standardScreenSize);
        initializer.createAllEntities();
        players = initializer.makePlayers(3, playerColors);
        riskCards = initializer.getRiskCards();

        allTerritories = initializer.getTerritories();
        allContinents = initializer.getTerritoriesByContinentMap();

        game = makeGame(players);
        makeStartUpMap();

        finishStartUp();
    }
    public void makeStartUpMap() {
        int i = 0;
        for (Territory t : allTerritories) {
            players.get(i).addTerritory(t);
            t.addAdditionalTroops(1);
            players.get(i).removeDeployableTroops(1);
            i = (i + 1) % players.size();
        }
    }

    public void finishStartUp() {
        for (Player p : players) {
            for (int i = 0; i < p.getDeployableTroops() - 1; i++) {
                for (Territory t : allTerritories) {
                    if (p.ownsTerritory(t)) {
                        t.addAdditionalTroops(1);
                        break;
                    }
                }
            }
        }

        for (Player p : players) {
            while (p.getDeployableTroops() > 1) {
                p.removeDeployableTroops(1);
            }
        }
    }

    public Game makeGame(ArrayList<Player> allPlayers) {
        Attack attack = new Attack(testDice);
        Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));

        return new Game(allPlayers, allTerritories, allContinents,
                riskCards, attack, maneuver, null);
    }

    @Test
    public void F10playerDrawsRiskCard_LosesAttackDrawCardFalse_ExpectNoExtraRiskCard() throws IOException {
        initializeGameState();
        assignMainTerritories();
        Player attacker = players.get(0);

        ArrayList<Integer> attackerResult = new ArrayList<>(List.of(1));
        ArrayList<Integer> defenderResult = new ArrayList<>(List.of(6));

        game.setGameState(GameState.ATTACK);

        game.clickedOnPoint(pointsByTerritoryName.get(player1MainTerritory.getTerritoryName()));
        game.clickedOnPoint(pointsByTerritoryName.get(player1AttacksThis.getTerritoryName()));


        assertFalse(game.getDrawCard());


        EasyMock.expect(testDice.rollDice(1)).andReturn(attackerResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(defenderResult);
        EasyMock.replay(testDice);

        ArrayList<Integer> losses = game.attack(1, 1);

        assertEquals(0, losses.get(2));
        assertEquals(0, attacker.getCards().size());
        assertFalse(game.getDrawCard());

        game.setGameState(GameState.MANEUVER);
        game.transitionFromManeuverToDeploy();

        assertEquals(0, attacker.getCards().size());

        EasyMock.verify(testDice);
    }

    @Test
    public void F10playerDrawsRiskCard_WinsAttackButNotTerritoryDrawCardFalse_ExpectNoExtraRiskCard() throws IOException {
        initializeGameState();
        assignMainTerritories();
        Player attacker = players.get(0);

        ArrayList<Integer> attackerResult = new ArrayList<>(List.of(6));
        ArrayList<Integer> defenderResult = new ArrayList<>(List.of(1));

        game.setGameState(GameState.ATTACK);

        player1AttacksThis.addAdditionalTroops(1);

        game.clickedOnPoint(pointsByTerritoryName.get(player1MainTerritory.getTerritoryName()));
        game.clickedOnPoint(pointsByTerritoryName.get(player1AttacksThis.getTerritoryName()));


        assertFalse(game.getDrawCard());


        EasyMock.expect(testDice.rollDice(1)).andReturn(attackerResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(defenderResult);
        EasyMock.replay(testDice);

        ArrayList<Integer> losses = game.attack(1, 1);

        assertEquals(0, losses.get(2));
        assertEquals(0, attacker.getCards().size());
        assertFalse(game.getDrawCard());

        game.setGameState(GameState.MANEUVER);
        game.transitionFromManeuverToDeploy();

        assertEquals(0, attacker.getCards().size());

        EasyMock.verify(testDice);
    }

    @Test
    public void F10playerDrawsRiskCard_WinsOneAttackAndTerritoryDrawCardTrue_ExpectExtraRiskCard() throws IOException {
        initializeGameState();
        assignMainTerritories();
        Player attacker = players.get(0);

        ArrayList<Integer> attackerResult = new ArrayList<>(List.of(6));
        ArrayList<Integer> defenderResult = new ArrayList<>(List.of(1));

        game.setGameState(GameState.ATTACK);

        game.clickedOnPoint(pointsByTerritoryName.get(player1MainTerritory.getTerritoryName()));
        game.clickedOnPoint(pointsByTerritoryName.get(player1AttacksThis.getTerritoryName()));


        assertFalse(game.getDrawCard());


        EasyMock.expect(testDice.rollDice(1)).andReturn(attackerResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(defenderResult);
        EasyMock.replay(testDice);

        ArrayList<Integer> losses = game.attack(1, 1);

        assertEquals(1, losses.get(2));
        assertEquals(0, attacker.getCards().size());
        assertTrue(game.getDrawCard());

        game.setGameState(GameState.MANEUVER);
        game.transitionFromManeuverToDeploy();

        assertEquals(1, attacker.getCards().size());

        EasyMock.verify(testDice);
    }

    @Test
    public void F10playerDrawsRiskCard_WinsTwoAttackAndTerritoryDrawCardTrue_ExpectOneExtraRiskCard() throws IOException {
        initializeGameState();
        assignMainTerritories();
        Player attacker = players.get(0);

        ArrayList<Integer> attackerResult = new ArrayList<>(List.of(6));
        ArrayList<Integer> defenderResult = new ArrayList<>(List.of(1));

        player2AttacksThis.addAdditionalTroops(1);

        game.setGameState(GameState.ATTACK);


        game.clickedOnPoint(pointsByTerritoryName.get(player1MainTerritory.getTerritoryName()));
        game.clickedOnPoint(pointsByTerritoryName.get(player1AttacksThis.getTerritoryName()));


        assertFalse(game.getDrawCard());


        EasyMock.expect(testDice.rollDice(1)).andReturn(attackerResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(defenderResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(attackerResult);
        EasyMock.expect(testDice.rollDice(1)).andReturn(defenderResult);
        EasyMock.replay(testDice);


        ArrayList<Integer> losses = game.attack(1, 1);

        assertEquals(1, losses.get(2));
        assertEquals(0, attacker.getCards().size());
        assertTrue(game.getDrawCard());

        game.setAttackState(AttackPhase.ATTACKFROM);
        assertEquals(game.getAttackState(), AttackPhase.ATTACKFROM);

        game.clickedOnPoint(pointsByTerritoryName.get(player2AttacksThis.getTerritoryName()));
        game.clickedOnPoint(pointsByTerritoryName.get(player1AlsoAttackThis.getTerritoryName()));

        ArrayList<Integer> losses2 = game.attack(1, 1);


        assertEquals(1, losses2.get(2));
        assertEquals(0, attacker.getCards().size());
        assertTrue(game.getDrawCard());


        assertTrue(game.getDrawCard());


        game.setGameState(GameState.MANEUVER);
        game.transitionFromManeuverToDeploy();

        assertEquals(1, attacker.getCards().size());

        EasyMock.verify(testDice);
    }

    private void assignMainTerritories() {
        for (Territory territory : allTerritories) {
            String territoryName = territory.getTerritoryName();
            if (territoryName.equals("Ural")) {
                player1MainTerritory = territory;
            }  else if (territoryName.equals("Siberia")) {
                player1AttacksThis = territory;
            }  else if (territoryName.equals("Western United States")) {
                player2AttacksThis = territory;
            } else if (territoryName.equals("Alberta")) {
                player1AlsoAttackThis = territory;
            }
        }

    }

    private void initializePoints() throws IOException {
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


}
