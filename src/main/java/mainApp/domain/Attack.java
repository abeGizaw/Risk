package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Attack {
    private final Dice dice;
    private Territory attackingTerritory;
    private Territory defendingTerritory;
    private Player attacker;
    private Player defender;
    public Attack(Dice diceInput) {
        this.dice = diceInput;
    }

    @SuppressFBWarnings
    public boolean validateAttackingTerritory(Territory attackTerritoryInput, Player attackerInput) {
        if (attackTerritoryInput== null){
            throw new NullPointerException("Passed in a Null Territory object");
        }
        if (attackerInput== null){
            throw new NullPointerException("Passed in a Null Attacker");
        }
        if (attackerInput.territoryCount()==0){
            throw new IllegalStateException("Player has no territories. Cannot attack.");
        }
        if (!(attackerInput.ownsTerritory(attackTerritoryInput) && (attackTerritoryInput.getCurrentNumberOfTroops()>1))){
            return false;
        }
        if (!borderingPlayers(attackerInput, attackTerritoryInput)){
            return false;
        }
        this.attackingTerritory = attackTerritoryInput;
        this.attacker = attackerInput;
        return true;
    }

    boolean borderingPlayers(Player attackerInput, Territory territoryToCheck) {
        if (attackerInput == null){
            throw new NullPointerException("The Player is Null.");
        }
        Set<Territory> neighbors =  territoryToCheck.getAdjacentTerritories();

        for (Territory t: neighbors) {
            if (!attackerInput.ownsTerritory(t)){
                return true;
            }
        }
        return false;
    }

    @SuppressFBWarnings
    public boolean validateDefendingTerritory(Territory defendingTerritoryInput, Player defenderInput) {
        HashSet<Territory> neighbors = (HashSet<Territory>) attackingTerritory.getAdjacentTerritories();
        if (!neighbors.contains(defendingTerritoryInput) || attacker.ownsTerritory(defendingTerritoryInput)){
            return false;
        }
        this.defender = defenderInput;
        this.defendingTerritory = defendingTerritoryInput;
        return true;
    }

    public ArrayList<ArrayList<Integer>> generateRolls(int attackTroopCount, int defendTroopCount) {
        generateRollsValidateHelper(attackTroopCount, defendTroopCount);
        ArrayList<ArrayList<Integer>> allRolls = new ArrayList<>();

        ArrayList<Integer> attackRolls = dice.rollDice(attackTroopCount);
        ArrayList<Integer> defendRolls = dice.rollDice(defendTroopCount);

        allRolls.add(attackRolls);
        allRolls.add(defendRolls);

        return allRolls;
    }

    private void generateRollsValidateHelper(int attackTroopCount, int defendTroopCount) {
        if (attackTroopCount <=0 || attackTroopCount >= 4){
            throw new IllegalArgumentException("Illegal amount of attacking Troops");
        }
        if (defendTroopCount <=0 || defendTroopCount >= 3){
            throw new IllegalArgumentException("Illegal amount of defending Troops");
        }
    }

    public ArrayList<Integer> attackLogic(ArrayList<ArrayList<Integer>> allRolls) {
        if (allRolls == null){
            throw new NullPointerException("Rolls are null.");
        }
        ArrayList<Integer> attackerRolls = allRolls.get(0);
        ArrayList<Integer> defenderRolls = allRolls.get(1);
        ArrayList<Integer> rollLoses = new ArrayList<>();

        int attackerTroopLoss = 0;
        int defenderTroopLoss = 0;

        int minimumDice = Integer.min(attackerRolls.size(), defenderRolls.size());
        for (int i=0; i<minimumDice; i++){
            if (attackerRolls.get(attackerRolls.size()-1-i) > defenderRolls.get(defenderRolls.size()-1-i)){
                defenderTroopLoss++;
            }else {
                attackerTroopLoss++;
            }
        }
        rollLoses.add(attackerTroopLoss);
        rollLoses.add(defenderTroopLoss);
        return rollLoses;
    }

    public boolean updatePlayerTroopAndTerritory(ArrayList<Integer> troopLosses, int attackTroopCount) {
        if (troopLosses== null){
            throw new NullPointerException("Troop losses are null.");
        }
        int attackerCount =troopLosses.get(0);
        int defenderCount = troopLosses.get(1);
        int totalLoss = attackerCount + defenderCount;
        if (attackerCount<0 || defenderCount<0 || totalLoss>2 || totalLoss<1){
            throw new IllegalArgumentException("Invalid number of troops.");
        }

        attackingTerritory.removeFromCurrentTroops(attackerCount);
        defendingTerritory.removeFromCurrentTroops(defenderCount);

        if (defendingTerritory.getCurrentNumberOfTroops() == 0){
            defender.removeTerritory(defendingTerritory);
            attacker.addTerritory(defendingTerritory);
            int toMove = attackTroopCount - attackerCount;
            defendingTerritory.addAdditionalTroops(toMove);
            attackingTerritory.removeFromCurrentTroops(toMove);
            return true;
        }
        return false;
    }

    public ArrayList<Integer> attackerWins(int attackTroopCount, int defendTroopCount){
        ArrayList<ArrayList<Integer>> rolls = generateRolls(attackTroopCount, defendTroopCount);
        ArrayList<Integer> loss = attackLogic(rolls);
        if (updatePlayerTroopAndTerritory(loss, attackTroopCount)){
            if (defender.territoryCount() == 0){
                loss.add(2);
            } else {
                loss.add(1);
            }
        } else {
            loss.add(0);
        }

        return loss;
    }

    public int attackMax(){
        return this.attackingTerritory.maxDeployableAttackTroops();
    }

    public int defendMax(){
        return defendingTerritory.maxDeployableDefendTroops();
    }

    @SuppressFBWarnings
    public Territory getAttackingTerritory(){
        return attackingTerritory;
    }

    @SuppressFBWarnings
    public Territory getDefendingTerritory(){
        return defendingTerritory;
    }


}
