package mainApp.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.partialMockBuilder;
import static org.junit.jupiter.api.Assertions.*;

public class AttackTest {


    @Test
    public void validateAttackingTerritory_WithNullTerritory_ExpectNullPointerException(){
        Player attacker = EasyMock.mock(Player.class);
        Dice dice = EasyMock.mock(Dice.class);

        EasyMock.replay(attacker);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Passed in a Null Territory object";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    attack.validateAttackingTerritory(null,  attacker);
                },  "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());

        EasyMock.verify(attacker);
        EasyMock.verify(dice);
    }

    @Test
    public void validateAttackingTerritory_WithNullPlayer_ExpectNullPointerException(){
        Territory attackTerritory = EasyMock.mock(Territory.class);
        Dice dice = EasyMock.mock(Dice.class);

        EasyMock.replay(attackTerritory);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Passed in a Null Attacker";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    attack.validateAttackingTerritory(attackTerritory,  null);
                },  "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());

        EasyMock.verify(attackTerritory);
        EasyMock.verify(dice);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerNoTerritories_ExpectIllegalStateException(){
        Territory attackTerritory = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.mock(Player.class);
        Dice dice = EasyMock.mock(Dice.class);

        expect(attacker.territoryCount()).andReturn(0);

        EasyMock.replay(attackTerritory);
        EasyMock.replay(attacker);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Player has no territories. Cannot attack.";

        Exception thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    attack.validateAttackingTerritory(attackTerritory,  attacker);
                },  "Illegal State Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());

        EasyMock.verify(attackTerritory);
        EasyMock.verify(dice);
        EasyMock.verify(attacker);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerNoOwnPlayer1TerritoryMAXINTMinus1TroopsNoNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  Integer.MAX_VALUE-1, false);
    }
    @Test
    public void validateAttackingTerritory_WithPlayerNoOwnPlayer1TerritoryMAXINTMinus1TroopsCanAttackNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  Integer.MAX_VALUE-1, true);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer1TerritoryMAXINTMinus1TroopsNoNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  Integer.MAX_VALUE-1, false);
    }
    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer1TerritoryMAXINTMinus1TroopsWithNeighbor_ExpectTrue(){
        validateAttackingTerritoryHelper(1,  true,  Integer.MAX_VALUE-1, true);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer1Territory1TroopsWithNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  1, true);
    }
    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer1Territory1TroopsNoNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  1, false);
    }
    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer1Territory0Troops_NoNeighbors_ExpectFalse(){
        validateAttackingTerritoryHelper(1,  false,  0, false);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerNoOwnPlayer41TerritoryMAXINTTroopsWithNeighborToAttack_ExpectFalse(){
        validateAttackingTerritoryHelper(41,  false,  Integer.MAX_VALUE, true);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer41TerritoryMAXINTTroopsWithNeighbor_ExpectTrue(){
        validateAttackingTerritoryHelper(41,  true,  Integer.MAX_VALUE, true);
    }

    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer41TerritoryMAXINTTroopsWithNoNeighbor_ExpectFalse(){
         validateAttackingTerritoryHelper(41,  false,  Integer.MAX_VALUE, false);
    }                                                                                                                       @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer41Territory1TroopWithNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(41,  false,  1, true);
    }
    @Test
    public void validateAttackingTerritory_WithPlayerOwnPlayer41Territory0TroopWithNoNeighbor_ExpectFalse(){
        validateAttackingTerritoryHelper(41,  false,  0, false);
    }

    public void validateAttackingTerritoryHelper(int playerTerritoryCount,  boolean playerOwnsTerritory,  int troopCount, boolean attackNeighbor){
        Territory attackTerritory = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.mock(Player.class);
        Dice dice = EasyMock.mock(Dice.class);
        Attack attackMock = partialMockBuilder(Attack.class)
            .withConstructor(dice)
            .addMockedMethod("borderingPlayers")
            .createMock();

        expect(attacker.territoryCount()).andReturn(playerTerritoryCount);
        expect(attacker.ownsTerritory(attackTerritory)).andReturn(playerOwnsTerritory);
        if (playerOwnsTerritory){
            expect(attackTerritory.getCurrentNumberOfTroops()).andReturn(troopCount);
            expect(attackMock.borderingPlayers(attacker, attackTerritory)).andReturn(attackNeighbor);
        }

        EasyMock.replay(attackTerritory);
        EasyMock.replay(dice);
        EasyMock.replay(attacker);
        EasyMock.replay(attackMock);

        boolean actual = attackMock.validateAttackingTerritory(attackTerritory,  attacker);
        assertEquals(playerOwnsTerritory,  actual);

        EasyMock.verify(attackTerritory);
        EasyMock.verify(dice);
        EasyMock.verify(attacker);
        EasyMock.verify(attackMock);
    }

    @Test
    public void validateDefendingTerritory_PlayerOwns1TerritoryAndNotDefending_ExpectTrue(){
        validateDefendingTerritoryHelper(1,  false,  true);
    }
    @Test
    public void validateDefendingTerritory_PlayerOwns2TerritoryAndDefending_ExpectFalse(){
        validateDefendingTerritoryHelper(2,  true,  false);
    }

    @Test
    public void validateDefendingTerritory_PlayerOwns2TerritoryAndNotDefending_ExpectTrue(){
        validateDefendingTerritoryHelper(2,  false,  true);
    }
    @Test
    public void validateDefendingTerritory_PlayerOwns41TerritoryAndDefending_ExpectFalse(){
        validateDefendingTerritoryHelper(41,  true,  false);
    }
    @Test
    public void validateDefendingTerritory_PlayerOwns41TerritoryAndNotDefending_ExpectTrue(){
        validateDefendingTerritoryHelper(41,  false,  true);
    }


    private void validateDefendingTerritoryHelper(int numTerritories,  boolean playerContainsDefendingTerritory,  boolean expected){
        Territory attackingTerritory = EasyMock.mock(Territory.class);

        Set<Territory> territories = new HashSet<>();
        for (int i=0; i<numTerritories; i++){
            territories.add(EasyMock.mock(Territory.class));
        }
        Player defender = EasyMock.mock(Player.class);
        Player attacker = EasyMock.mock(Player.class);
        Territory defendingTerritory = territories.iterator().next();
        Dice dice = EasyMock.mock(Dice.class);

        Attack attackMock = partialMockBuilder(Attack.class)
            .withConstructor(dice)
            .addMockedMethod("borderingPlayers")
            .createMock();

        // expects for the validateAttackingTerritory
        expect(attacker.territoryCount()).andReturn(2);
        expect(attacker.ownsTerritory(attackingTerritory)).andReturn(true);
        expect(attackingTerritory.getCurrentNumberOfTroops()).andReturn(10);
        expect(attackMock.borderingPlayers(attacker, attackingTerritory)).andReturn(true);

        expect(attackingTerritory.getAdjacentTerritories()).andReturn(territories);
        expect(attacker.ownsTerritory(defendingTerritory)).andReturn(playerContainsDefendingTerritory);

        for (Territory territory: territories){
            EasyMock.replay(territory);
        }
        EasyMock.replay(attackingTerritory);
        EasyMock.replay(defender);
        EasyMock.replay(attacker);
        EasyMock.replay(dice);
        EasyMock.replay(attackMock);


        attackMock.validateAttackingTerritory(attackingTerritory,  attacker);
        boolean actual = attackMock.validateDefendingTerritory(defendingTerritory,  attacker);
        assertEquals(expected,  actual);

        for (Territory territory: territories){
            EasyMock.verify(territory);
        }
        EasyMock.verify(attackingTerritory);
        EasyMock.verify(defender);
        EasyMock.verify(attacker);
        EasyMock.verify(dice);
        EasyMock.verify(attackMock);
    }

    @Test
    public void generateRolls_WithAttacker0Troops_ExpectIllegalArgumentException(){
        generateIllegalTroopsHelper(0,  1,  "attacking");
    }
    @Test
    public void generateRolls_WithAttacker4Troops_ExpectIllegalArgumentException(){
        generateIllegalTroopsHelper(4,  1,  "attacking");
    }

    @Test
    public void generateRolls_WithDefender0Troops_ExpectIllegalArgumentException(){
        generateIllegalTroopsHelper(2,  0,  "defending");
    }

    @Test
    public void generateRolls_WithDefender3Troops_ExpectIllegalArgumentException(){
        generateIllegalTroopsHelper(2,  3,  "defending");
    }

    private void generateIllegalTroopsHelper(int attackTroopCount,  int defendTroopCount,  String position){
        Dice dice = EasyMock.mock(Dice.class);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Illegal amount of " + position +" Troops";

        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    attack.generateRolls(attackTroopCount,  defendTroopCount);
                },  "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());
        EasyMock.verify(dice);

    }

    @Test
    public void generateRolls_WithAttack1_Defend1Troops_ExpectSize1_1(){
        generateRollsHelper(1, 1);
    }

    @Test
    public void generateRolls_WithAttack1_Defend2Troops_ExpectSize1_2(){
        generateRollsHelper(1, 2);
    }

    @Test
    public void generateRolls_WithAttack2_Defend1Troops_ExpectSize2_1(){
        generateRollsHelper(2, 1);
    }

    @Test
    public void generateRolls_WithAttack2_Defend2Troops_ExpectSize2_2(){
        generateRollsHelper(2, 2);
    }
    @Test
    public void generateRolls_WithAttack3_Defend1Troops_ExpectSize3_1(){
        generateRollsHelper(3, 1);
    }

    @Test
    public void generateRolls_WithAttack3_Defend2Troops_ExpectSize3_2(){
        generateRollsHelper(3, 2);
    }
    private void generateRollsHelper(int attackTroopCount,  int defendTroopCount){
        Dice dice = EasyMock.mock(Dice.class);
        Attack attack = new Attack(dice);

        ArrayList<Integer> attackRolls = new ArrayList<>();
        ArrayList<Integer> defendRolls = new ArrayList<>();

        for (int i=0; i<attackTroopCount; i++){
            attackRolls.add(i);
        }
        for (int i=0; i<defendTroopCount; i++){
            defendRolls.add(i);
        }
        expect(dice.rollDice(attackTroopCount)).andReturn(attackRolls);
        expect(dice.rollDice(defendTroopCount)).andReturn(defendRolls);
        EasyMock.replay(dice);

        ArrayList<ArrayList<Integer>> rolls = attack.generateRolls(attackTroopCount,  defendTroopCount);

        assertEquals(attackTroopCount,  rolls.get(0).size());
        assertEquals(defendTroopCount,  rolls.get(1).size());

        EasyMock.verify(dice);


    }


    @Test
    public void attackLogic_nullRolls_ExpectNullPointerException(){
        Dice dice = EasyMock.mock(Dice.class);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Rolls are null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    attack.attackLogic(null);
                },  "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());
        EasyMock.verify(dice);
    }

    @Test
    public void attackLogic_DWins_A6D6_Expect1and0(){
        attackLogicHelper(List.of(6),  List.of(6),  1, 0);

    }

    @Test
    public void attackLogic_DWins_A1D1_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(1),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1D2_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(2),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A5D6_Expect1and0(){
        attackLogicHelper(List.of(5),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1D6_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(6),  1, 0);
    }
    @Test
    public void attackLogic_AWins_A6D1_Expect0and1(){
        attackLogicHelper(List.of(6),  List.of(1),  0, 1);
    }
    @Test
    public void attackLogic_AWins_A6D5_Expect0and1(){
        attackLogicHelper(List.of(6),  List.of(5),  0, 1);
    }

    @Test
    public void attackLogic_AWins_A2D1_Expect0and1(){
        attackLogicHelper(List.of(2),  List.of(1),  0, 1);
    }

    @Test
    public void attackLogic_DWins_A6D6D1_Expect1and0(){
        attackLogicHelper(List.of(6),  List.of(1, 6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1D1D1_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(1, 1),  1, 0);
    }
    @Test
    public void attackLogic_DWins_A1D2D2_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(2, 2),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A5D4D6_Expect1and0(){
        attackLogicHelper(List.of(5),  List.of(4, 6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1D6D6_Expect1and0(){
        attackLogicHelper(List.of(1),  List.of(6, 6),  1, 0);
    }
    @Test
    public void attackLogic_AWins_A6D1D1_Expect0and1(){
        attackLogicHelper(List.of(6),  List.of(1, 1),  0, 1);
    }

    @Test
    public void attackLogic_AWins_A6D4D5_Expect0and1(){
        attackLogicHelper(List.of(6),  List.of(4, 5),  0, 1);
    }
    @Test
    public void attackLogic_AWins_A2D1D1_Expect0and1(){
        attackLogicHelper(List.of(2),  List.of(1, 1),  0, 1);
    }

    @Test
    public void attackLogic_DWins_A5A6D6_Expect1and0(){
        attackLogicHelper(List.of(5, 6),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1A1D1_Expect1and0(){
        attackLogicHelper(List.of(1, 1),  List.of(1),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1A1D2_Expect1and0(){
        attackLogicHelper(List.of(1, 1),  List.of(2),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A4A5D6_Expect1and0(){
        attackLogicHelper(List.of(4, 5),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1A1D6_Expect1and0(){
        attackLogicHelper(List.of(1, 1),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_AWins_A5A6D1_Expect0and1(){
        attackLogicHelper(List.of(5, 6),  List.of(1),  0, 1);
    }
    @Test
    public void attackLogic_AWins_A4A6D5_Expect0and1(){
        attackLogicHelper(List.of(4, 6),  List.of(5),  0, 1);
    }

    @Test
    public void attackLogic_AWins_A1A2D1_Expect0and1(){
        attackLogicHelper(List.of(1, 2),  List.of(1),  0, 1);
    }

    @Test
    public void attackLogic_DWins_A4A5A6D6_Expect1and0(){
        attackLogicHelper(List.of(4, 5, 6),  List.of(6),  1, 0);
    }
    @Test
    public void attackLogic_DWins_A1A1A1D1_Expect1and0(){
        attackLogicHelper(List.of(1, 1, 1),  List.of(1),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1A1A1D2_Expect1and0(){
        attackLogicHelper(List.of(1, 1, 1),  List.of(2),  1, 0);
    }
    @Test
    public void attackLogic_DWins_A3A4A51D6_Expect1and0(){
        attackLogicHelper(List.of(3, 4, 5),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_DWins_A1A1A1D6_Expect1and0(){
        attackLogicHelper(List.of(1, 1, 1),  List.of(6),  1, 0);
    }

    @Test
    public void attackLogic_AWins_A2A5A6D1_Expect0and1(){
        attackLogicHelper(List.of(2, 5, 6),  List.of(1),  0, 1);
    }

    @Test
    public void attackLogic_AWins_A1A4A6D5_Expect0and1(){
        attackLogicHelper(List.of(1, 4, 6),  List.of(5),  0, 1);
    }
    @Test
    public void attackLogic_AWins_A1A1A2D1_Expect0and1(){
        attackLogicHelper(List.of(1, 1, 2),  List.of(1),  0, 1);
    }
    @Test
    public void attackLogic_ALose2_A6A6_D6D6_Expect2and0(){
        attackLogicHelper(List.of(6, 6),  List.of(6, 6),  2, 0);
    }
    @Test
    public void attackLogic_ALose2_A1A1_D1D1_Expect2and0(){
        attackLogicHelper(List.of(1, 1),  List.of(1, 1),  2, 0);
    }
    @Test
    public void attackLogic_ALose2_A4A5_D5D6_Expect2and0(){
        attackLogicHelper(List.of(4, 5),  List.of(5, 6),  2, 0);
    }
    @Test
    public void attackLogic_ALose2_A1A2_D5D6_Expect2and0(){
        attackLogicHelper(List.of(1, 2),  List.of(5, 6),  2, 0);
    }

    @Test
    public void attackLogic_DLose2_A6A6_D1D1_Expect0and2(){
        attackLogicHelper(List.of(6, 6),  List.of(1, 1),  0, 2);
    }
    @Test
    public void attackLogic_DLose2_A5A6_D1D1_Expect0and2(){
        attackLogicHelper(List.of(5, 6),  List.of(1, 1),  0, 2);
    }
    @Test
    public void attackLogic_DLose2_A2A2_D1D1_Expect0and2(){
        attackLogicHelper(List.of(2, 2),  List.of(1, 1),  0, 2);
    }

    @Test
    public void attackLogic_AandDLose1_A1A6_D2D5_Expect1and1(){
        attackLogicHelper(List.of(1, 6),  List.of(2, 5),  1, 1);
    }

    @Test
    public void attackLogic_AandDLose1_A5A6_D4D6_Expect1and1(){
        attackLogicHelper(List.of(5, 6),  List.of(4, 6),  1, 1);
    }
    @Test
    public void attackLogic_AandDLose1_A2A2_D1D2_Expect1and1(){
        attackLogicHelper(List.of(2, 2),  List.of(1, 2),  1, 1);
    }

    @Test
    public void attackLogic_ALose2_A5A6A6_D6D6_Expect2and0(){
        attackLogicHelper(List.of(5, 6, 6),  List.of(6, 6),  2, 0);
    }
    @Test
    public void attackLogic_ALose2_A1A1A1_D1D1_Expect2and0(){
        attackLogicHelper(List.of(1, 1, 1),  List.of(1, 1),  2, 0);
    }

    @Test
    public void attackLogic_ALose2_A4A4A5_D5D6_Expect2and0(){
        attackLogicHelper(List.of(4, 4, 5),  List.of(5, 6),  2, 0);
    }

    @Test
    public void attackLogic_ALose2_A1A2A2_D5D6_Expect2and0(){
        attackLogicHelper(List.of(1, 2, 2),  List.of(5, 6),  2, 0);
    }

    @Test
    public void attackLogic_DLose2_A6A6A6_D1D1_Expect0and2(){
        attackLogicHelper(List.of(6, 6, 6),  List.of(1, 1),  0, 2);
    }

    @Test
    public void attackLogic_DLose2_A4A5A6_D1D1_Expect0and2(){
        attackLogicHelper(List.of(4, 5, 6),  List.of(1, 1),  0, 2);
    }
    @Test
    public void attackLogic_DLose2_A1A2A2_D1D1_Expect0and2(){
        attackLogicHelper(List.of(1, 2, 2),  List.of(1, 1),  0, 2);
    }

    @Test
    public void attackLogic_AandDLose1_A1A1A6_D2D5_Expect1and1(){
        attackLogicHelper(List.of(1, 1, 6),  List.of(2, 5),  1, 1);
    }
    @Test
    public void attackLogic_AandDLose1_A5A5A6_D4D6_Expect1and1(){
        attackLogicHelper(List.of(5, 5, 6),  List.of(4, 6),  1, 1);
    }

    @Test
    public void attackLogic_AandDLose1_A1A2A2_D1D2_Expect1and1(){
        attackLogicHelper(List.of(1, 2, 2),  List.of(1, 2),  1, 1);
    }
    private void attackLogicHelper(List<Integer> aRolls,  List<Integer> dRolls,  int expectedALoss,  int expectedDLoss){
        Dice dice = EasyMock.mock(Dice.class);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);

        ArrayList<Integer> attackerRolls = new ArrayList<>(aRolls);
        ArrayList<Integer> defenderRolls = new ArrayList<>(dRolls);

        ArrayList<ArrayList<Integer>> allRolls = new ArrayList<>();
        allRolls.add(attackerRolls);
        allRolls.add(defenderRolls);

        ArrayList<Integer> troopLoses =  attack.attackLogic(allRolls);
        assertEquals(expectedALoss,  troopLoses.get(0));
        assertEquals(expectedDLoss,  troopLoses.get(1));

        EasyMock.verify(dice);

    }

    @Test
    public void updatePlayerTroop_nullTroops_ExpectNullPointerException(){
        Dice dice = EasyMock.mock(Dice.class);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Troop losses are null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    attack.updatePlayerTroopAndTerritory(null, 1);
                },  "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());
        EasyMock.verify(dice);
    }


    @Test
    public void updatePlayerTroop_Neg1AttackTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(-1, 0));
    }
    @Test
    public void updatePlayerTroop_Neg1DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(0, -1));
    }
    @Test
    public void updatePlayerTroop_3AttackTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(3, 0));
    }
    @Test
    public void updatePlayerTroop_3DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(0, 3));
    }

    @Test
    public void updatePlayerTroop_0Defend0AttackTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(0, 0));
    }

    @Test
    public void updatePlayerTroop_1Defend2AttackTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(1, 2));
    }

    @Test
    public void updatePlayerTroop_2Defend1AttackTroops_ExpectIllegalArgException(){
        updateTroopCountHelperArgumentException(List.of(2, 1));
    }
    private void updateTroopCountHelperArgumentException(List<Integer> counts){
        Dice dice = EasyMock.mock(Dice.class);
        EasyMock.replay(dice);

        Attack attack = new Attack(dice);
        String expectedMessage = "Invalid number of troops.";
        ArrayList<Integer> troops = new ArrayList<>(counts);
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    attack.updatePlayerTroopAndTerritory(troops, 1);
                },  "Illegal Argument Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());
        EasyMock.verify(dice);

    }

    @Test
    public void updatePlayerTroop_NoTakeOver_0Attack_1DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(0, 1),  false);
    }

    @Test
    public void updatePlayerTroop_TakeOver_0Attack_1DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(0, 1),  true);
    }

    @Test
    public void updatePlayerTroop_NoTakeOver_0Attack_2DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(0, 2),  false);
    }

    @Test
    public void updatePlayerTroop_TakeOver_0Attack_2DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(0, 2),  true);
    }
    @Test
    public void updatePlayerTroop_NoTakeOver_1Attack_0DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(1, 0),  false);
    }

    @Test
    public void updatePlayerTroop_TakeOver_1Attack_0DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(1, 0),  true);
    }

    @Test
    public void updatePlayerTroop_NoTakeOver_1Attack_1DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(1, 1),  false);
    }
    @Test
    public void updatePlayerTroop_TakeOver_1Attack_1DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(1, 1),  true);
    }
    @Test
    public void updatePlayerTroop_NoTakeOver_2Attack_0DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(2, 0),  false);
    }

    @Test
    public void updatePlayerTroop_TakeOver_2Attack_0DefendTroops_ExpectIllegalArgException(){
        updateTroopCountHelper(List.of(2, 0),  true);
    }

    private void updateTroopCountHelper(List<Integer> counts,  boolean takeOver){
        Integer attackerLoss = counts.get(0);
        Integer defenderLoss = counts.get(1);

        Territory attackingTerritory = EasyMock.mock(Territory.class);

        Set<Territory> territories = new HashSet<>();
        for (int i=0; i<2; i++){
            territories.add(EasyMock.mock(Territory.class));
        }
        Player defender = EasyMock.mock(Player.class);
        Player attacker = EasyMock.mock(Player.class);
        Territory defendingTerritory = territories.iterator().next();
        Dice dice = EasyMock.mock(Dice.class);

        Attack attackMock = partialMockBuilder(Attack.class)
            .withConstructor(dice)
            .addMockedMethod("borderingPlayers")
            .createMock();

        // expects for the validateAttackingTerritory
        expect(attacker.territoryCount()).andReturn(2);
        expect(attacker.ownsTerritory(attackingTerritory)).andReturn(true);
        expect(attackingTerritory.getCurrentNumberOfTroops()).andReturn(10);
        expect(attackMock.borderingPlayers(attacker, attackingTerritory)).andReturn(true);

        // expects for validateDefendingTerritory
        expect(attackingTerritory.getAdjacentTerritories()).andReturn(territories);
        expect(attacker.ownsTerritory(defendingTerritory)).andReturn(false);

        //expects for updateTroopAndTerritory
        attackingTerritory.removeFromCurrentTroops(attackerLoss);
        EasyMock.expectLastCall();

        defendingTerritory.removeFromCurrentTroops(defenderLoss);
        EasyMock.expectLastCall();

        if (takeOver){
            expect(defendingTerritory.getCurrentNumberOfTroops()).andReturn(0);
            defender.removeTerritory(defendingTerritory);
            EasyMock.expectLastCall();

            attacker.addTerritory(defendingTerritory);
            EasyMock.expectLastCall();

            int toMove = 3 - attackerLoss;
            defendingTerritory.addAdditionalTroops(toMove);
            EasyMock.expectLastCall();

            attackingTerritory.removeFromCurrentTroops(toMove);
            EasyMock.expectLastCall();

        }else {
            expect(defendingTerritory.getCurrentNumberOfTroops()).andReturn(2);
        }


        for (Territory territory: territories){
            EasyMock.replay(territory);
        }
        EasyMock.replay(attackingTerritory);
        EasyMock.replay(attacker);
        EasyMock.replay(defender);
        EasyMock.replay(dice);
        EasyMock.replay(attackMock);

        attackMock.validateAttackingTerritory(attackingTerritory,  attacker);
        attackMock.validateDefendingTerritory(defendingTerritory,  defender);

        boolean result = attackMock.updatePlayerTroopAndTerritory(new ArrayList<>(counts),  3);
        assertEquals(takeOver, result);
        for (Territory territory: territories){
            EasyMock.verify(territory);
        }
        EasyMock.verify(attackingTerritory);
        EasyMock.verify(attacker);
        EasyMock.verify(defender);
        EasyMock.verify(dice);
        EasyMock.verify(attackMock);
    }

    @Test
    public void borderingPlayers_NullColor_ExpectNullPointerException(){
        Territory territory= EasyMock.mock(Territory.class);
        Attack attack = new Attack(null);
        String expectedMessage = "The Player is Null.";

        Exception thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    attack.borderingPlayers(null, territory);
                },  "Null Pointer Exception should be thrown");
        assertEquals(expectedMessage,  thrown.getMessage());

    }

    @Test
    public void borderingPlayers_PlayerOwnsAllNeighbors_ExpectFalse(){
        Dice dice = EasyMock.mock(Dice.class);
        Territory territoryToCheck = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.createMock(Player.class);
        Attack attack = new Attack(dice);

        Set<Territory> neighbors = new HashSet<>();
        for (int i=0; i<4; i++){
            Territory currentTerritory = EasyMock.mock(Territory.class);
            neighbors.add(currentTerritory);
            expect(attacker.ownsTerritory(currentTerritory)).andReturn(true);
            EasyMock.replay(currentTerritory);
        }

        expect(territoryToCheck.getAdjacentTerritories()).andReturn(neighbors);

        EasyMock.replay(dice);
        EasyMock.replay(territoryToCheck);
        EasyMock.replay(attacker);

        boolean actual = attack.borderingPlayers(attacker, territoryToCheck);

        assertFalse(actual);

        for (Territory territory: neighbors){
            EasyMock.verify(territory);
        }
        EasyMock.verify(dice);
        EasyMock.verify(territoryToCheck);
        EasyMock.verify(attacker);
    }


    @Test
    public void borderingPlayers_PlayerOwnsAllButOneNeighbors_ExpectTrue(){
        Dice dice = EasyMock.mock(Dice.class);
        Territory territoryToCheck = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.createMock(Player.class);
        Attack attack = new Attack(dice);

        ArrayList<Territory> neighbors = new ArrayList<>();
        for (int i=0; i<4; i++){
            Territory currentTerritory = EasyMock.mock(Territory.class);
            neighbors.add(currentTerritory);
            if (i==3){
                EasyMock.expect(attacker.ownsTerritory(currentTerritory)).andReturn(false);
            } else {
                EasyMock.expect(attacker.ownsTerritory(currentTerritory)).andReturn(true);

            }
            EasyMock.replay(currentTerritory);
        }
        Set<Territory> set = new LinkedHashSet<Territory>(neighbors);
        expect(territoryToCheck.getAdjacentTerritories()).andReturn(set);

        EasyMock.replay(dice);
        EasyMock.replay(territoryToCheck);
        EasyMock.replay(attacker);

        boolean actual = attack.borderingPlayers(attacker, territoryToCheck);

        assertTrue(actual);

        for (Territory territory: neighbors){
            EasyMock.verify(territory);
        }
        EasyMock.verify(dice);
        EasyMock.verify(territoryToCheck);
        EasyMock.verify(attacker);
    }

    @Test
    public void borderingPlayers_PlayerOwnsOne_Neighbors_ExpectTrue(){
        Dice dice = EasyMock.mock(Dice.class);
        Territory territoryToCheck = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.createMock(Player.class);
        Attack attack = new Attack(dice);

        ArrayList<Territory> neighbors = new ArrayList<>();
        for (int i=0; i<4; i++){
            Territory currentTerritory = EasyMock.mock(Territory.class);
            if (i==3){
                EasyMock.expect(attacker.ownsTerritory(currentTerritory)).andReturn(false);
            } else {
                EasyMock.expect(attacker.ownsTerritory(currentTerritory)).andReturn(true);

            }
            neighbors.add(currentTerritory);
        }
        Set<Territory> set = new LinkedHashSet<Territory>(neighbors);
        expect(territoryToCheck.getAdjacentTerritories()).andReturn(set);

        for (Territory t: neighbors){
            EasyMock.replay(t);
        }
        EasyMock.replay(dice);
        EasyMock.replay(territoryToCheck);
        EasyMock.replay(attacker);

        boolean actual = attack.borderingPlayers(attacker, territoryToCheck);

        assertTrue(actual);

        for (Territory territory: neighbors){
            EasyMock.verify(territory);
        }
        EasyMock.verify(dice);
        EasyMock.verify(territoryToCheck);
        EasyMock.verify(attacker);
    }

    @Test
    public void borderingPlayers_PlayerOwnsNo_Neighbors_ExpectTrue(){
        Dice dice = EasyMock.mock(Dice.class);
        Territory territoryToCheck = EasyMock.mock(Territory.class);
        Player attacker = EasyMock.createMock(Player.class);
        Attack attack = new Attack(dice);

        ArrayList<Territory> neighbors = new ArrayList<>();
        for (int i=0; i<4; i++){
            Territory currentTerritory = EasyMock.mock(Territory.class);
            if (i==0){
                expect(attacker.ownsTerritory(currentTerritory)).andReturn(false);
            }
            neighbors.add(currentTerritory);
        }
        Set<Territory> set = new LinkedHashSet<Territory>(neighbors);
        expect(territoryToCheck.getAdjacentTerritories()).andReturn(set);

        for (Territory t: neighbors){
            EasyMock.replay(t);
        }
        EasyMock.replay(dice);
        EasyMock.replay(territoryToCheck);
        EasyMock.replay(attacker);

        boolean actual = attack.borderingPlayers(attacker, territoryToCheck);

        assertTrue(actual);

        for (Territory territory: neighbors){
            EasyMock.verify(territory);
        }
        EasyMock.verify(dice);
        EasyMock.verify(territoryToCheck);
        EasyMock.verify(attacker);
    }
}
