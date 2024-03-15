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
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.TreeMap;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;

public class F13PlayerLoseTest {

    private Initializer initializer;
    private ArrayList<Player> players;
    private Game game;
    private ArrayList<Territory> territories;
    private TreeMap<String, Point> pointsByTerritoryName;
    private Dice dice;
    private final ArrayList<Integer> attackRollsDefenderLose1 = new ArrayList<>(of(2, 6, 6));
    private final ArrayList<Integer> defendRollsDefenderLose1 = new ArrayList<>(of(3));
    private final ArrayList<Integer> expectedLossesDefenderLose1 = new ArrayList<>(of(0, 1, 1));
    private final ArrayList<Integer> attackRollsDefenderLose1AndDie = new ArrayList<>(of(2, 6, 6));
    private final ArrayList<Integer> defendRollsDefenderLose1AndDie = new ArrayList<>(of(3));
    private final ArrayList<Integer> expectedLossesDefenderLose1AndDie = new ArrayList<>(of(0, 1, 2));
    private final ArrayList<Integer> attackRollsAttackerLose1 = new ArrayList<>(of(1, 2, 3));
    private final ArrayList<Integer> defendRollsAttackerLose1 = new ArrayList<>(of(3));
    private final ArrayList<Integer> expectedLossesAttackerLose1 = new ArrayList<>(of(1, 0, 0));

    @Test
    public void totalPlayers3_player3Has1TerritoryPlayer3LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors);

        Player attacker = players.get(0);
        Player defender = players.get(players.size() - 1);
        assignTerritories(1);

        Territory defendingTerritory = territories.get(territories.size() - 1);
        Territory attackingTerritory = findValidAttackingTerritory(defendingTerritory);
        assert attackingTerritory != null;

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender);
        assertEquals(GameState.ATTACK, game.getGameState());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory, attacker, defender);

        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        assertEquals(2, players.size());
        EasyMock.verify(dice);
    }

    @Test
    public void totalPlayers4_player4Has1TerritoryPlayer4LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Black", "Green", "Red"};
        initializeGameState(colors);

        Player attacker = players.get(0);
        Player defender = players.get(players.size() - 1);
        assignTerritories(1);

        Territory defendingTerritory = territories.get(territories.size() - 1);
        Territory attackingTerritory = findValidAttackingTerritory(defendingTerritory);
        assert attackingTerritory != null;

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender);
        assertEquals(GameState.ATTACK, game.getGameState());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory, attacker, defender);
        assertEquals(3, players.size());
        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        EasyMock.verify(dice);
    }

    @Test
    public void totalPlayers5_player5Has1TerritoryPlayer5LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Black", "Green", "Red", "Magenta"};
        initializeGameState(colors);

        Player attacker = players.get(0);
        Player defender = players.get(players.size() - 1);
        assignTerritories(1);

        Territory defendingTerritory = territories.get(territories.size() - 1);
        Territory attackingTerritory = findValidAttackingTerritory(defendingTerritory);
        assert attackingTerritory != null;

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender);
        assertEquals(GameState.ATTACK, game.getGameState());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory, attacker, defender);
        assertEquals(4, players.size());
        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        EasyMock.verify(dice);
    }

    @Test
    public void totalPlayers3_player3Has2TerritoryPlayer3LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        with2Territories(colors, 3);

    }

    @Test
    public void totalPlayers4_player4Has2TerritoryPlayer4LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Black", "Green", "Magenta"};
        with2Territories(colors, 4);

    }

    @Test
    public void totalPlayers5_player5Has2TerritoryPlayer5LoseAndDie() throws IOException {
        String[] colors = {"Blue", "Red", "Black", "Green", "Magenta"};
        with2Territories(colors, 5);

    }

    @Test
    public void totalPlayers3_player3Has3TerritoryPlayer3LoseAndDie() throws IOException {
        String[] colors = {"Black", "Green", "Magenta"};
        with3Territories(colors, 3);
    }

    @Test
    public void totalPlayers4_player4Has3TerritoryPlayer4LoseAndDie() throws IOException {
        String[] colors = {"Black", "Green", "Red", "Magenta"};
        with3Territories(colors, 4);
    }

    @Test
    public void totalPlayers5_player5Has3TerritoryPlayer5LoseAndDie() throws IOException {
        String[] colors = {"Black", "Green", "Red", "Magenta", "Blue"};
        with3Territories(colors, 5);
    }

    private void with3Territories(String[] colors, int playerCount) throws IOException {
        initializeGameState(colors);
        assertEquals(playerCount, players.size());
        Player attacker = players.get(0);
        Player defender = players.get(players.size() - 1);

        assignTerritories(3);

        Territory defendingTerritory1 = territories.get(territories.size() - 1);
        Territory attackingTerritory1 = findValidAttackingTerritory(defendingTerritory1);

        Territory defendingTerritory2 = territories.get(territories.size() - 2);
        Territory attackingTerritory2 = findValidAttackingTerritory(defendingTerritory2);

        Territory defendingTerritory3 = territories.get(territories.size() - 3);
        Territory attackingTerritory3 = findValidAttackingTerritory(defendingTerritory3);

        assert attackingTerritory1 != null;
        assert attackingTerritory2 != null;
        assert attackingTerritory3 != null;

        assertEquals(GameState.ATTACK, game.getGameState());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);

        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1.size())).andReturn(attackRollsDefenderLose1);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1.size())).andReturn(defendRollsDefenderLose1);

        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1.size())).andReturn(attackRollsDefenderLose1);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1.size())).andReturn(defendRollsDefenderLose1);

        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);

        validateValidGameSetup(attackingTerritory1, defendingTerritory1, attacker, defender);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory1, attacker, defender);
        oneAttackDefenderLoses(defendingTerritory1, attacker, defender);

        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        validateValidGameSetup(attackingTerritory2, defendingTerritory2, attacker, defender);
        oneAttackDefenderLoses(defendingTerritory2, attacker, defender);

        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        validateValidGameSetup(attackingTerritory3, defendingTerritory3, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory3, attacker, defender);

        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory1.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        assertEquals(playerCount - 1, players.size());
        EasyMock.verify(dice);
    }


    private void with2Territories(String[] colors, int playerCount) throws IOException {
        initializeGameState(colors);
        assertEquals(playerCount, players.size());
        Player attacker = players.get(0);
        Player defender = players.get(players.size() - 1);

        assignTerritories(2);

        Territory defendingTerritory1 = territories.get(territories.size() - 1);
        Territory attackingTerritory1 = findValidAttackingTerritory(defendingTerritory1);

        Territory defendingTerritory2 = territories.get(territories.size() - 2);
        Territory attackingTerritory2 = findValidAttackingTerritory(defendingTerritory2);

        assert attackingTerritory1 != null;
        assert attackingTerritory2 != null;

        assertEquals(GameState.ATTACK, game.getGameState());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1.size())).andReturn(attackRollsDefenderLose1);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1.size())).andReturn(defendRollsDefenderLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);

        validateValidGameSetup(attackingTerritory1, defendingTerritory1, attacker, defender);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory1, attacker, defender);
        oneAttackDefenderLoses(defendingTerritory1, attacker, defender);
        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        validateValidGameSetup(attackingTerritory2, defendingTerritory2, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory2, attacker, defender);

        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory1.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        assertEquals(playerCount - 1, players.size());
        EasyMock.verify(dice);
    }


    private void assignTerritories(int numTerritoriesForLastPlayer) {
        int i;
        int playerIndex = 0;
        for (i = 0; i < territories.size() - numTerritoriesForLastPlayer; i++) {
            Territory territory = territories.get(i);
            territory.addAdditionalTroops(5);
            assertEquals(5, territory.getCurrentNumberOfTroops());
            players.get(playerIndex).addTerritory(territory);
            playerIndex++;
            playerIndex = (playerIndex + 1) % (players.size() - 1);
        }
        for (Player player : players) {
            for (int j = i; j < territories.size(); j++) {
                if (player.ownsTerritory(territories.get(j))) {
                    throw new IllegalStateException("You are double assigning  " + territories.get(j).getTerritoryName());
                }
            }
        }
        while (i < territories.size()) {
            Territory endTerritory = territories.get(i);
            endTerritory.addAdditionalTroops(1);
            assertEquals(1, endTerritory.getCurrentNumberOfTroops());
            players.get(players.size() - 1).addTerritory(endTerritory);
            i++;
        }

        for (Territory territory : territories) {
            boolean owned = false;
            for (Player player : players) {
                if (owned && player.ownsTerritory(territory)) {
                    throw new IllegalStateException("You are doubly assigning territories.");
                }
                owned = player.ownsTerritory(territory);
            }
        }

        assertEquals(numTerritoriesForLastPlayer, players.get(players.size() - 1).territoryCount());
    }

    private void oneAttackDefenderLosesAndDies(Territory defendingTerritory, Player attacker, Player defender) {
        ArrayList<Integer> losses = game.attack(attackRollsDefenderLose1AndDie.size(), defendRollsDefenderLose1AndDie.size());
        assertEquals(expectedLossesDefenderLose1AndDie.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLossesDefenderLose1AndDie.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLossesDefenderLose1AndDie.get(2), losses.get(2));  // defender loses outright
        assertFalse(defender.ownsTerritory(defendingTerritory));
        assertTrue(attacker.ownsTerritory(defendingTerritory));
    }

    private void oneAttackDefenderLoses(Territory defendingTerritory, Player attacker, Player defender) {
        ArrayList<Integer> losses = game.attack(attackRollsDefenderLose1.size(), defendRollsDefenderLose1.size());
        assertEquals(expectedLossesDefenderLose1.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLossesDefenderLose1.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLossesDefenderLose1.get(2), losses.get(2));  // defender loses outright
        assertFalse(defender.ownsTerritory(defendingTerritory));
        assertTrue(attacker.ownsTerritory(defendingTerritory));
    }

    private void oneAttackAttackerLoses(Territory defendingTerritory, Player attacker, Player defender) {
        ArrayList<Integer> losses = game.attack(attackRollsAttackerLose1.size(), defendRollsAttackerLose1.size());
        assertEquals(expectedLossesAttackerLose1.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLossesAttackerLose1.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLossesAttackerLose1.get(2), losses.get(2));  // defender loses outright
        assertTrue(defender.ownsTerritory(defendingTerritory));
        assertFalse(attacker.ownsTerritory(defendingTerritory));
    }

    private void initializeGameState(String[] colors) throws IOException {
        initializePoints();
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        initializer = new Initializer(new Dimension(1536, 864));
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        game = makeGame(players);

        game.setGameState(GameState.ATTACK);
        game.setAttackState(AttackPhase.ATTACKFROM);
    }

    private void initializePoints() throws IOException {
        String territoryPointsFilePath = "src/main/java/data/territoryPoints.txt";
        pointsByTerritoryName = new TreeMap<>();
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

    public Game makeGame(ArrayList<Player> playersInput) {
        RiskDeck allRiskCards = initializer.getRiskCards();

        dice = EasyMock.mock(Dice.class);
        Attack attack = new Attack(dice);
        Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        return new Game(playersInput, territories, initializer.getTerritoriesByContinentMap(),
                allRiskCards, attack, maneuver, null);
    }

    private void validateValidGameSetup(Territory attackingTerritory, Territory defendingTerritory, Player attacker, Player defender) {
        assertTrue(attackingTerritory.getAdjacentTerritories().contains(defendingTerritory));
        assertFalse(attacker.ownsTerritory(defendingTerritory));
        assertTrue(defender.ownsTerritory(defendingTerritory));

        validateAttackingTerritory(attackingTerritory);
        validateDefendingTerritory(defendingTerritory);
    }

    private void validateAttackingTerritory(Territory attackingTerritory) {
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(AttackPhase.ATTACKFROM, game.getAttackState());
        String nameAttacking = attackingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking));
        assertEquals(attackingTerritory, game.getAttackingTerritory());
    }

    private void validateDefendingTerritory(Territory defendingTerritory) {
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(AttackPhase.DEFENDWITH, game.getAttackState());
        String nameDefending = defendingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameDefending));
        assertEquals(defendingTerritory, game.getDefendingTerritory());
        assertEquals(AttackPhase.CHOOSETROOPS, game.getAttackState());
    }

    private Territory findValidAttackingTerritory(Territory defendingTerritory) {
        ArrayList<Territory> neighbors = new ArrayList<>(defendingTerritory.getAdjacentTerritories());
        for (Territory territory : neighbors) {
            if (players.get(0).ownsTerritory(territory)) {
                return territory;
            }
        }
        Territory territoryToTransfer = neighbors.get(0);
        Player player = findOwner(territoryToTransfer);
        assert player != null;
        player.removeTerritory(territoryToTransfer);
        players.get(0).addTerritory(territoryToTransfer);
        return territoryToTransfer;
    }

    private Player findOwner(Territory territory) {
        for (Player player : players) {
            if (player.ownsTerritory(territory)) {
                return player;
            }
        }
        return null;
    }
}
