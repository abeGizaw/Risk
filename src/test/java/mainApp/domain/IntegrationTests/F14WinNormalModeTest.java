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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class F14WinNormalModeTest {


    private Initializer initializer;
    private Dice dice;
    private ArrayList<Territory> territories;
    private TreeMap<String, Point> pointsByTerritoryName;
    private ArrayList<Player> players;
    private Game game;
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
    public void totalPlayers3_player3Has41TerritoryPlayer3Wins() throws IOException {
        String[] colors = {"Blue", "Black"};
        initializeGameState(colors);

        assignTerritories(41);
        Player attacker = players.get(players.size()-1);
        Player defender = findOwner(territories.get(0));
        Territory defendingTerritory = territories.get(0);
        Territory attackingTerritory = findValidAttackingTerritory(defendingTerritory, attacker);
        assert defender != null;
        game.setTurnIndex(players.size()-1);

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender);
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(1, defender.territoryCount());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.replay(dice);

        oneAttackAttackerLoses(defendingTerritory, attacker, defender);
        oneAttackDefenderLosesAndDies(defendingTerritory, attacker, defender);
        assertEquals(colors.length-1, players.size());
        assertEquals(GameState.WIN, game.getGameState());
        EasyMock.verify(dice);
    }

    @Test
    public void totalPlayers4_player4Has40TerritoryPlayer4Wins() throws IOException {
        String[] colors = {"Blue", "Black", "Red"};
        initializeGameState(colors);

        assignTerritories(40);
        Player attacker = players.get(players.size()-1);
        Player defender1 = findOwner(territories.get(0));
        Player defender2 = findOwner(territories.get(1));
        Territory defendingTerritory1 = territories.get(0);
        Territory defendingTerritory2 = territories.get(1);
        Territory attackingTerritory1 = findValidAttackingTerritory(defendingTerritory1, attacker);
        Territory attackingTerritory2 = findValidAttackingTerritory(defendingTerritory2, attacker);
        assert defender1 != null;
        assert defender2 != null;
        game.setTurnIndex(players.size()-1);
        validateValidGameSetup(attackingTerritory1, defendingTerritory1, attacker, defender1);
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(1, defender1.territoryCount());
        assertEquals(1, defender2.territoryCount());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.replay(dice);
        // attack 1 and the attacker loses
        oneAttackAttackerLoses(defendingTerritory1, attacker, defender1);
        // attack 2 and defender loses and dies
        oneAttackDefenderLosesAndDies(defendingTerritory1, attacker, defender1);
        assertEquals(2, players.size());

        // attack 3 and last defender dies and the last player should win
        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        game.setTurnIndex(players.size()-1);
        validateValidGameSetup(attackingTerritory2, defendingTerritory2, attacker, defender2);
        oneAttackDefenderLosesAndDies(defendingTerritory2, attacker, defender2);

        assertEquals(1, players.size());
        assertEquals(GameState.WIN, game.getGameState());
        EasyMock.verify(dice);
    }
    @Test
    public void totalPlayers5_player5Has39TerritoryPlayer5Wins() throws IOException {
        String[] colors = {"Blue", "Black", "Red", "Magenta"};
        initializeGameState(colors);

        assignTerritories(39);
        Player attacker = players.get(players.size()-1);
        Player defender1 = findOwner(territories.get(0));
        Player defender2 = findOwner(territories.get(1));
        Player defender3 = findOwner(territories.get(2));
        Territory defendingTerritory1 = territories.get(0);
        Territory defendingTerritory2 = territories.get(1);
        Territory defendingTerritory3 = territories.get(2);
        Territory attackingTerritory1 = findValidAttackingTerritory(defendingTerritory1, attacker);
        Territory attackingTerritory2 = findValidAttackingTerritory(defendingTerritory2, attacker);
        Territory attackingTerritory3 = findValidAttackingTerritory(defendingTerritory3, attacker);
        assert defender1 != null;
        assert defender2 != null;
        assert defender3 != null;

        game.setTurnIndex(players.size()-1);
        validateValidGameSetup(attackingTerritory1, defendingTerritory1, attacker, defender1);
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(1, defender1.territoryCount());
        assertEquals(1, defender2.territoryCount());
        assertEquals(1, defender3.territoryCount());

        EasyMock.expect(dice.rollDice(attackRollsAttackerLose1.size())).andReturn(attackRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(defendRollsAttackerLose1.size())).andReturn(defendRollsAttackerLose1);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(attackRollsDefenderLose1AndDie.size())).andReturn(attackRollsDefenderLose1AndDie);
        EasyMock.expect(dice.rollDice(defendRollsDefenderLose1AndDie.size())).andReturn(defendRollsDefenderLose1AndDie);

        EasyMock.replay(dice);
        // attack 1 and the attacker loses
        oneAttackAttackerLoses(defendingTerritory1, attacker, defender1);
        // attack 2 and defender loses and dies
        oneAttackDefenderLosesAndDies(defendingTerritory1, attacker, defender1);
        assertEquals(3, players.size());

        // attack 3 and last defender dies
        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        game.setTurnIndex(players.size()-1);
        validateValidGameSetup(attackingTerritory2, defendingTerritory2, attacker, defender2);
        oneAttackDefenderLosesAndDies(defendingTerritory2, attacker, defender2);
        assertEquals(2, players.size());

        // attack 4 and last defender dies and the last player should win
        game.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
        game.setTurnIndex(players.size()-1);
        validateValidGameSetup(attackingTerritory3, defendingTerritory3, attacker, defender3);
        assertEquals(1, defendingTerritory3.getCurrentNumberOfTroops());
        assertTrue(defendingTerritory3.getCurrentNumberOfTroops() - 1 >= 0);
        oneAttackDefenderLosesAndDies(defendingTerritory3, attacker, defender3);
        assertEquals(1, players.size());
        assertEquals(GameState.WIN, game.getGameState());
        EasyMock.verify(dice);
    }

    private void assignTerritories(int numTerritoriesForLastPlayer) {
        int i;
        int playerIndex = 0;
        for (i = 0; i < territories.size() - numTerritoriesForLastPlayer; i++) {
            Territory territory = territories.get(i);
            territory.addAdditionalTroops(1);
            assertEquals(1, territory.getCurrentNumberOfTroops());
            players.get(playerIndex).addTerritory(territory);
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
            endTerritory.addAdditionalTroops(50);
            assertEquals(50, endTerritory.getCurrentNumberOfTroops());
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
        assertTrue(defendingTerritory.getAdjacentTerritories().contains(attackingTerritory));

        validateAttackingTerritory(attackingTerritory, attacker);
        validateDefendingTerritory(defendingTerritory);
    }

    private void validateAttackingTerritory(Territory attackingTerritory, Player attacker) {
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(AttackPhase.ATTACKFROM, game.getAttackState());
        assertTrue(attackingTerritory.getCurrentNumberOfTroops()>1);
        assertTrue(attacker.ownsTerritory(attackingTerritory));

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

    private Territory findValidAttackingTerritory(Territory defendingTerritory, Player attacker) {
        ArrayList<Territory> neighbors = new ArrayList<>(defendingTerritory.getAdjacentTerritories());
        for (Territory territory : neighbors) {
            if (attacker.ownsTerritory(territory)) {
                return territory;
            }
        }
        Territory territoryToTransfer = neighbors.get(0);
        Player player = findOwner(territoryToTransfer);
        assert player != null;
        player.removeTerritory(territoryToTransfer);
        attacker.addTerritory(territoryToTransfer);
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
