package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {

    final private Random random;

    @SuppressFBWarnings
    public Dice(final Random rand) {
        this.random = rand;
    }


    public int rollUnique(ArrayList previousRolls) {
        if (random == null){
            throw new IllegalArgumentException("Passed in a Null random object");
        }else if (previousRolls.size() >5){
            throw new IllegalArgumentException("Passed an array with too many players.");
        }

        int minimumRoll = 1;
        int maxRoll = 6;
        int diceRollValue = random.nextInt(maxRoll) + minimumRoll;
        while (previousRolls.contains(diceRollValue)){
            diceRollValue = random.nextInt(maxRoll) + minimumRoll;
        }

        return diceRollValue;
    }
    public ArrayList<Integer> rollDice(int numOfDice) {
        int minimumRoll = 1;
        int maxRoll = 6;

        if (random == null){
            throw new IllegalArgumentException("Passed in a Null random object");
        } else if (numOfDice > 3){
            throw new IllegalArgumentException("Rolling too much dice");
        } else if (numOfDice < 1){
            throw new IllegalArgumentException("Not rolling enough dice");
        }

        ArrayList<Integer> playerDiceValues = new ArrayList<>();
        for (int k = 0; k < numOfDice; k++) {
            int diceRollValue = random.nextInt(maxRoll) + minimumRoll;
            playerDiceValues.add(k, diceRollValue);
        }
        Collections.sort(playerDiceValues);
        return playerDiceValues;
    }

}
