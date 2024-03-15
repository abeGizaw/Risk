package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Player {
    private final ArrayList<Card> cards;
    private int deployableTroops;
    private HashSet<Territory> territories = new HashSet<>();
    private Color color;

    @SuppressFBWarnings
    public Player(int initialTroops, Color playerColor, ArrayList<Card> starterCards) {
        this.deployableTroops = initialTroops;
        this.color = playerColor;
        this.cards = starterCards;
    }

    public void addDeployableTroops(int addTroops) {
        if (addTroops <= 2) {
            throw new IllegalArgumentException("Adding too few of troops");
        }

        this.deployableTroops += addTroops;

    }

    public void removeDeployableTroops(int removeTroops) {
        if (removeTroops < 0) {
            throw new IllegalArgumentException("Trying to remove a negative number of troops");
        }
        if (removeTroops > this.deployableTroops){
            throw new ArithmeticException("Trying to remove too many troops");
        }

        this.deployableTroops -= removeTroops;
    }

    public boolean ownsTerritory(Territory territory) {
        if (territory == null){
            throw new IllegalArgumentException("Checking for Null Territory");
        }
        return this.territories.contains(territory);
    }

    public void addTerritory(Territory territory){
        if (territory == null){
            throw new NullPointerException("Territory was null. Please enter a valid Territory.");
        }
        if (territoryCount() >=42){
            throw new IllegalStateException("Player already contains 42 territories or more.");
        }
        this.territories.add(territory);
    }

    public void removeTerritory(Territory territory){
        validateTerritoryToRemove(territory);
        this.territories.remove(territory);
    }

    private void validateTerritoryToRemove(Territory territory){
        if (territory == null){
            throw new NullPointerException("Territory was null. Please enter a valid Territory to remove.");
        }
        if (this.territories.isEmpty()){
            throw new RuntimeException("The player does not control any territories.");
        }
        if (!this.territories.contains(territory)) {
            throw new IllegalArgumentException("The player does not control this territory.");
        }
    }

    public String toString(){
        return color.toString();
    }

    public int territoryCount() {
        return territories.size();
    }

    public int getDeployableTroops() {
        return this.deployableTroops;
    }

    public Color getColor() {
        return this.color;
    }

    public ArrayList<Card> getCards(){
        return new ArrayList<>(cards);
    }

    public void addCard(Card cardToAdd) {
        if (cardToAdd == null){
            throw new IllegalArgumentException("Passed in a Null Card");
        }
        cards.add(cardToAdd);

    }

    public void removeCards(ArrayList<Card> cardsToTurnIn) {
        if (cardsToTurnIn == null){
            throw new IllegalArgumentException("Passed in a Null ArrayList of cards");
        }
        if (cardsToTurnIn.size() != 3){
            throw new IllegalArgumentException("Trying to remove " + cardsToTurnIn.size() +  " card instead of 3");
        }

        this.cards.removeAll(cardsToTurnIn);
    }
}
