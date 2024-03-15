package mainApp.domain;

import java.util.ArrayList;
import java.util.Collections;

public class RiskDeck extends Deck {
    private int currentValueOfCards = 2;

    RiskDeck(ArrayList<Card> cards) {
        super(cards);
        maximumDeckSize = 44;
        if (size() > maximumDeckSize) {
            throw new IllegalStateException("Risk deck can not contain more than 44 cards.");
        }
    }

    @Override
    public boolean canTurnInCards(ArrayList<Card> cardsToBeTurnedIn) {
        if (cardsToBeTurnedIn.size() < 3) {
            return false;
        } else if (size() + cardsToBeTurnedIn.size() > maximumDeckSize) {
            throw new IllegalStateException("Risk deck can not contain more than 44 cards.");
        }
        int[] cardTypes = determineCountsOfCardTypes(cardsToBeTurnedIn);
        return ensureCorrectNumberOfCardTypes(cardTypes);
    }

    private int[] determineCountsOfCardTypes(ArrayList<Card> cardsToBeTurnedIn) {
        int[] cardTypes = new int[4];
        for (Card card: cardsToBeTurnedIn) {
            if (card.type().equals("Infantry")) {
                cardTypes[0] += 1;
            } else if (card.type().equals("Cavalry")) {
                cardTypes[1] += 1;
            } else if (card.type().equals("Artillery")){
                cardTypes[2] += 1;
            } else {
                cardTypes[3] += 1;
            }
        }
        return cardTypes;
    }

    private boolean ensureCorrectNumberOfCardTypes(int[] cardTypes) {
        if (cardTypes[3] > 0) {
            return true;
        }
        boolean canTurnIn = true;
        for (int i = 0; i < 3; i++) {
            if (cardTypes[i] > 2) {
                return true;
            } else if (cardTypes[i] == 0) {
                canTurnIn = false;
            }
        }
        return canTurnIn;
    }

    @Override
    protected int calculateValueOfCards(ArrayList<Card> cardsToBeTurnedIn) {
        if (currentValueOfCards < 12) {
            currentValueOfCards += 2;
        } else if (currentValueOfCards > 12) {
            currentValueOfCards += 5;
        } else {
            currentValueOfCards += 3;
        }
        return currentValueOfCards;
    }

    @Override
    protected void verifyCardsCanBeTurnedIn(ArrayList<Card> cardsToBeTurnedIn) {
        if (cardsToBeTurnedIn.size() != 3) {
            throw new IllegalStateException("Can only turn in 3 risk cards at a time.");
        } else if (!canTurnInCards(cardsToBeTurnedIn)) {
            throw new IllegalStateException("This set of cards can not be turned in.");
        }
    }



}
