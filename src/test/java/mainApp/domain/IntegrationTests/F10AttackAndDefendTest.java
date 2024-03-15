package mainApp.domain.IntegrationTests;

import mainApp.domain.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
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

public class F10AttackAndDefendTest {

    private ArrayList<Territory> territories;
    private Game game;
    private final Dimension standardScreenSize = new Dimension(1536, 864);
    private ArrayList<Player> players;
    private Initializer initializer;
    private Dice dice;
    private final ResourceBundle messages = ResourceBundle.getBundle("message");
    private TreeMap<String, Point> pointsByTerritoryName;


    private void initializeGameState(String[] colors, boolean noNeighbors) throws IOException {
        initializePoints();
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        initializer = new Initializer(standardScreenSize);
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        game = makeGame(players, noNeighbors);

        game.setGameState(GameState.ATTACK);
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

    public void makeStartUpMap() {
        int i = 0;

        for (Territory t : territories) {
            players.get(i).addTerritory(t);
            t.addAdditionalTroops(1);
            players.get(i).removeDeployableTroops(1);
            i = (i + 1) % players.size();
        }

    }

    public void assignNoNeighbors() {
        Player attacker = players.get(0);
        for (Territory t : territories) {
            attacker.addTerritory(t);
            t.addAdditionalTroops(1);
        }
        Territory lastTerr = territories.get(territories.size() - 1);
        attacker.removeTerritory(lastTerr);
        players.get(1).addTerritory(lastTerr);
    }

    public Game makeGame(ArrayList<Player> playersInput, boolean noNeighbors) {
        RiskDeck allRiskCards = initializer.getRiskCards();
        dice = EasyMock.mock(Dice.class);
        Attack attack = new Attack(dice);
        Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));
        Game gameTemp = new Game(playersInput, initializer.getTerritories(), initializer.getTerritoriesByContinentMap(),
                allRiskCards, attack, maneuver, null);
        if (noNeighbors) {
            assignNoNeighbors();
        } else {
            makeStartUpMap();
        }
        return gameTemp;
    }

    @Test
    public void testF9AttackAndDefend_withInvalidPoint_expectAttackFieldsStayNull() throws IOException {
        String[] colors = {"Magenta", "Blue", "Black", "Green"};
        initializeGameState(colors, false);

        for (Player player : players) {
            player.removeDeployableTroops(player.getDeployableTroops());
            assertEquals(0, player.getDeployableTroops());
        }

        assertEquals(AttackPhase.ATTACKFROM, game.getAttackState());
        assertEquals(GameState.ATTACK, game.getGameState());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        game.clickedOnPoint(new Point(0, 0));

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
    }

    private Territory[] initializeAttackingTerritoryAndDefendingTerritory(
            Player attacker, boolean ownsAttacking, boolean ownsDefending, int toAddAttack,
            int toAddDefend, boolean neighbors) {

        Territory[] result = new Territory[2];
        Territory attackingTerritory = null;
        Territory defendingTerritory = null;
        assertNotEquals(null, attacker);

        for (Player player : players) {
            player.removeDeployableTroops(player.getDeployableTroops());
            assertEquals(0, player.getDeployableTroops());
        }

        for (Territory territory : territories) {
            boolean owns = attacker.ownsTerritory(territory);
            boolean valid = (ownsAttacking && owns) || (!ownsAttacking && !owns);
            if (valid) {
                attackingTerritory = territory;
                break;
            }

        }
        assert attackingTerritory != null;

        for (Territory territory : territories) {
            boolean isNeighbor = attackingTerritory.getAdjacentTerritories().contains(territory);
            boolean owns = attacker.ownsTerritory(territory);
            boolean valid = ((ownsDefending && owns) || (!ownsDefending && !owns)) && ((neighbors && isNeighbor) || (!neighbors && !isNeighbor));
            if (valid) {
                defendingTerritory = territory;
                break;
            }
        }
        assert defendingTerritory != null;
        attackingTerritory.addAdditionalTroops(toAddAttack);
        defendingTerritory.addAdditionalTroops(toAddDefend);
        assertEquals(toAddAttack + 1, attackingTerritory.getCurrentNumberOfTroops());

        result[0] = attackingTerritory;
        result[1] = defendingTerritory;
        return result;
    }

    @Test
    public void testF9AttackAndDefend_withAttackerNoOwnAttackingTerritory_expectIllegalArgumentException() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);
        String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, false, false, 5, 0, true);
        Territory attackingTerritory = attackAndDefendingTerritories[0];

        assertFalse(attacker.ownsTerritory(attackingTerritory));
        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
        assertEquals(AttackPhase.ATTACKFROM, game.getAttackState());
        assertEquals(GameState.ATTACK, game.getGameState());

        String nameAttacking = attackingTerritory.getTerritoryName();
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking)), "Illegal Argument Exception should be thrown");
        assertEquals(attackFlowErrorMessage, thrown.getMessage());

        assertEquals(attackingTerritory, game.getCurrentTerritory());
        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
    }

    @Test
    public void testF9AttackAndDefend_withAttackerOwnDefendingTerritory_expectIllegalArgumentException() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);
        String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, true, true, 5, 0, true);

        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];

        assertTrue(attacker.ownsTerritory(defendingTerritory));
        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        String nameAttacking = attackingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking));

        assertEquals(AttackPhase.DEFENDWITH, game.getAttackState());
        assertEquals(GameState.ATTACK, game.getGameState());

        String nameDefending = defendingTerritory.getTerritoryName();
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.clickedOnPoint(pointsByTerritoryName.get(nameDefending)), "Illegal Argument Exception should be thrown");
        assertEquals(attackFlowErrorMessage, thrown.getMessage());

        assertEquals(defendingTerritory, game.getCurrentTerritory());
        assert attackingTerritory == game.getAttackingTerritory();
        assertNull(game.getDefendingTerritory());
    }

    @Test
    public void testF9AttackAndDefend_withAttackingTerritoryHaveNoNeighbors_expectIllegalArgumentException() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, true);
        String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, true, false, 5, 0, false);

        Territory attackingTerritory = attackAndDefendingTerritories[0];

        for (Territory neighbor : attackingTerritory.getAdjacentTerritories()) {
            assertTrue(attacker.ownsTerritory(neighbor));
        }

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        String nameAttacking = attackingTerritory.getTerritoryName();
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking)), "Illegal Argument Exception should be thrown");
        assertEquals(attackFlowErrorMessage, thrown.getMessage());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
    }


    @Test
    public void testF9AttackAndDefend_withAttackingAndDefendingTerritoriesNotNeighbors_expectIllegalArgumentException() throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);
        String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, true, false, 5, 0, false);

        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];
        assertFalse(attackingTerritory.getAdjacentTerritories().contains(defendingTerritory));

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        String nameAttacking = attackingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking));

        String nameDefending = attackingTerritory.getTerritoryName();
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.clickedOnPoint(pointsByTerritoryName.get(nameDefending)), "Illegal Argument Exception should be thrown");
        assertEquals(attackFlowErrorMessage, thrown.getMessage());

        assertEquals(attackingTerritory, game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
    }

    @Test
    public void testF9AttackAndDefend_withAttackingNoAttackingTroops_expectIllegalArgumentException() throws IOException {
        validateTroopAttackCountHelper(0);
    }

    @Test
    public void testF9AttackAndDefend_with1AttackingTroops_expectIllegalArgumentException() throws IOException {
        validateTroopAttackCountHelper(1);
    }

    private void validateTroopAttackCountHelper(int expectedTroop) throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);
        String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, true, false, 0, 0, true);

        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];
        assertTrue(attackingTerritory.getAdjacentTerritories().contains(defendingTerritory));

        if (expectedTroop == 0) {
            attackingTerritory.removeFromCurrentTroops(attackingTerritory.getCurrentNumberOfTroops());
        }
        assertEquals(expectedTroop, attackingTerritory.getCurrentNumberOfTroops());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        String nameAttacking = attackingTerritory.getTerritoryName();
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking)), "Illegal Argument Exception should be thrown");
        assertEquals(attackFlowErrorMessage, thrown.getMessage());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());
    }

    @Test
    public void testF9AttackAndDefend_with_2A_1D_Troops_expectDWinsAnd_1A_1DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>();
        ArrayList<Integer> defendRolls = new ArrayList<>();
        attackRolls.add(6);
        defendRolls.add(6);
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 0, 0));
        validGameHelper(1, 0, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_2A_1D_Troops_expectAWinsAnd_1A_1DTroops_AOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>();
        ArrayList<Integer> defendRolls = new ArrayList<>();
        attackRolls.add(6);
        defendRolls.add(1);
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 1, 1));
        validGameHelper(1, 0, attackRolls, defendRolls, expectedLosses, true);
    }

    @Test
    public void testF9AttackAndDefend_with_3A_5D_Troops_expectDWinsAnd_2A_5DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(3));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 4));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 0, 0));
        validGameHelper(2, 4, attackRolls, defendRolls, expectedLosses, false);
    }


    @Test
    public void testF9AttackAndDefend_with_3A_5D_Troops_expectAWinsAnd_3A_4DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(3));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 2));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 1, 0));
        validGameHelper(2, 4, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_3A_1D_Troops_expectDWinsAnd_2A_1DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 6));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 0, 0));
        validGameHelper(2, 0, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_7A_1D_Troops_expectAWinsAnd_6A_1DTroops_AOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(5));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(3, 4));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 1, 1));
        validGameHelper(5, 0, attackRolls, defendRolls, expectedLosses, true);
    }

    @Test
    public void testF9AttackAndDefend_with_4A_1D_Troops_expectDWinsAnd_3A_1DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(5));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(4, 5));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 0, 0));
        validGameHelper(3, 0, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_6A_1D_Troops_expectAWinsAnd_3A_3DTroops_AOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(2));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 1, 1));
        validGameHelper(5, 0, attackRolls, defendRolls, expectedLosses, true);
    }

    @Test
    public void testF9AttackAndDefend_with_3A_2D_Troops_expectDWinsAllAnd_1A_2DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(2, 0, 0));
        validGameHelper(2, 1, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_6A_2D_Troops_expectAWinsAllAnd_4A_2DTroops_AOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(2, 2));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 2, 1));
        validGameHelper(5, 1, attackRolls, defendRolls, expectedLosses, true);
    }

    @Test
    public void testF9AttackAndDefend_with_3A_2D_Troops_expectEachLose1And_3A_1DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1, 2));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 1, 0));
        validGameHelper(3, 1, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_4A_2D_Troops_expectDWinsAllAnd_2A_2DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1, 1, 2));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 2));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(2, 0, 0));
        validGameHelper(3, 1, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_with_4A_2D_Troops_expectAWinsAllAnd_2A_2DTroops_AOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1, 5, 6));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 2, 1));
        validGameHelper(3, 1, attackRolls, defendRolls, expectedLosses, true);
    }

    @Test
    public void testF9AttackAndDefend_with_10A_2D_Troops_expectEachLose1And_9A_1DTroops_DOwnsDefending() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(1, 1, 6));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(1, 1, 0));
        validGameHelper(5, 1, attackRolls, defendRolls, expectedLosses, false);
    }

    @Test
    public void testF9AttackAndDefend_attackTwoTimes_ExpectEachLose1FirstThenDLoseTerritory() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(List.of(1, 1, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(List.of(1, 1, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(List.of(1, 1, 2));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(List.of(1, 1));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(List.of(1, 1, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(List.of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(List.of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(List.of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(List.of(false, true));
        validGameTwoAttacksHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses);
    }

    @Test
    public void testF9AttackAndDefend_with_2A_1D_Troops_expectAWinsAnd_DefenderIsDefeated() throws IOException {
        ArrayList<Integer> attackRolls = new ArrayList<>(List.of(2));
        ArrayList<Integer> defendRolls = new ArrayList<>(List.of(1));
        ArrayList<Integer> expectedLosses = new ArrayList<>(List.of(0, 1, 2));
        defenderEliminatedHelper(attackRolls, defendRolls, expectedLosses);
    }

    private void defenderEliminatedHelper(ArrayList<Integer> attackRolls,
                                          ArrayList<Integer> defendRolls, ArrayList<Integer> expectedLosses) throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);


        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker,
                true, false, 9, 0, true);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];
        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, 9 + 1, 1);

        Player defender = findOwner(defendingTerritory);
        assert defender != null;

        for (Territory territory : territories) {
            if (defender.ownsTerritory(territory)) {
                defender.removeTerritory(territory);
            }
        }

        defender.addTerritory(defendingTerritory);
        assertEquals(1, defender.territoryCount());

        EasyMock.expect(dice.rollDice(attackRolls.size())).andReturn(attackRolls);
        EasyMock.expect(dice.rollDice(defendRolls.size())).andReturn(defendRolls);
        EasyMock.replay(dice);

        ArrayList<Integer> losses = game.attack(attackRolls.size(), defendRolls.size());
        assertEquals(expectedLosses.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLosses.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLosses.get(2), losses.get(2));  // defender loses outright
        assertTrue(attacker.ownsTerritory(defendingTerritory));
        assertEquals(0, defender.territoryCount());
        EasyMock.verify(dice);
    }

    private void validGameTwoAttacksHelper(ArrayList<ArrayList<Integer>> attackRolls, ArrayList<ArrayList<Integer>> defendRolls,
              ArrayList<ArrayList<Integer>> expectedLosses, ArrayList<Boolean> defenderLosesTerritory) throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker, true,
                false, 9, 1, true);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];
        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, 9 + 1, 1 + 1);

        Player defender = findOwner(defendingTerritory);
        assert defender != null;

        EasyMock.expect(dice.rollDice(attackRolls.get(0).size())).andReturn(attackRolls.get(0));
        EasyMock.expect(dice.rollDice(defendRolls.get(0).size())).andReturn(defendRolls.get(0));
        EasyMock.expect(dice.rollDice(attackRolls.get(1).size())).andReturn(attackRolls.get(1));
        EasyMock.expect(dice.rollDice(defendRolls.get(1).size())).andReturn(defendRolls.get(1));
        EasyMock.replay(dice);

        ArrayList<Integer> losses = game.attack(attackRolls.get(0).size(), defendRolls.get(0).size());
        assertEquals(expectedLosses.get(0).get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLosses.get(0).get(1), losses.get(1)); // defender loss
        assertEquals(expectedLosses.get(0).get(2), losses.get(2));  // defender loses outright
        assertEquals(!defenderLosesTerritory.get(0), defender.ownsTerritory(defendingTerritory));
        assertEquals(defenderLosesTerritory.get(0), attacker.ownsTerritory(defendingTerritory));


        losses = game.attack(attackRolls.get(1).size(), defendRolls.get(1).size());
        assertEquals(expectedLosses.get(1).get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLosses.get(1).get(1), losses.get(1)); // defender loss
        assertEquals(expectedLosses.get(1).get(2), losses.get(2));  // defender loses outright
        assertEquals(!defenderLosesTerritory.get(1), defender.ownsTerritory(defendingTerritory));
        assertEquals(defenderLosesTerritory.get(1), attacker.ownsTerritory(defendingTerritory));

        EasyMock.verify(dice);
    }

    private void validGameHelper(int toAddAttack, int toAddDefend, ArrayList<Integer> attackRolls,
           ArrayList<Integer> defendRolls, ArrayList<Integer> expectedLosses, boolean defenderLosesTerritory) throws IOException {
        String[] colors = {"Blue", "Black", "Green"};
        initializeGameState(colors, false);

        Player attacker = players.get(0);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker,
                true, false, toAddAttack, toAddDefend, true);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];
        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, toAddAttack + 1, toAddDefend + 1);

        Player defender = findOwner(defendingTerritory);
        assert defender != null;

        EasyMock.expect(dice.rollDice(attackRolls.size())).andReturn(attackRolls);
        EasyMock.expect(dice.rollDice(defendRolls.size())).andReturn(defendRolls);
        EasyMock.replay(dice);

        ArrayList<Integer> losses = game.attack(attackRolls.size(), defendRolls.size());
        assertEquals(expectedLosses.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLosses.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLosses.get(2), losses.get(2));  // defender loses outright
        assertEquals(!defenderLosesTerritory, defender.ownsTerritory(defendingTerritory));
        assertEquals(defenderLosesTerritory, attacker.ownsTerritory(defendingTerritory));

        game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory.getTerritoryName()));
        assertEquals(GameState.CHOOSE, game.getGameState());
        EasyMock.verify(dice);
    }

    private Player findOwner(Territory territory) {
        for (Player player : players) {
            if (player.ownsTerritory(territory)) {
                return player;
            }
        }
        return null;
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

    private void validateValidGameSetup(Territory attackingTerritory, Territory defendingTerritory,
                                        Player attacker, int totalAttackTroopCount, int totalDefendTroopCount) {
        assertTrue(attackingTerritory.getAdjacentTerritories().contains(defendingTerritory));
        assertFalse(attacker.ownsTerritory(defendingTerritory));

        assertEquals(totalAttackTroopCount, attackingTerritory.getCurrentNumberOfTroops());
        assertEquals(totalDefendTroopCount, defendingTerritory.getCurrentNumberOfTroops());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        validateAttackingTerritory(attackingTerritory);
        validateDefendingTerritory(defendingTerritory);
    }


}
