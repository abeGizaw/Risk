package mainApp.domain;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Deck {
    protected final ArrayList<Card> deckOfCards;
    protected int maximumDeckSize;

    Deck(ArrayList<Card> cards) {
        deckOfCards = new ArrayList<>(cards);
        Collections.shuffle(deckOfCards);
    }

    final boolean containsCard(Card card) {
        if (card == null) {
            throw new NullPointerException("Card can not be null for containsCard.");
        }
        return deckOfCards.contains(card);
    }

    final int size() {
        return deckOfCards.size();
    }

    public final Card drawOneCard() {
        if (size() == 0) {
            throw new IllegalStateException("The deck contains no cards.");
        }
        return deckOfCards.remove(size() - 1);
    }

    public abstract boolean canTurnInCards(ArrayList<Card> cardsToBeTurnedIn);

    public final int turnInCards(ArrayList<Card> cardsToBeTurnedIn) {
        verifyCardsCanBeTurnedIn(cardsToBeTurnedIn);
        shuffleCardsBackIntoDeck(cardsToBeTurnedIn);
        return calculateValueOfCards(cardsToBeTurnedIn);
    }

    private void shuffleCardsBackIntoDeck(ArrayList<Card> cardsToBeTurnedIn) {
        deckOfCards.addAll(cardsToBeTurnedIn);
        Collections.shuffle(deckOfCards);
    }

    public void turnInCard(Card card) {
        deckOfCards.add(card);
        Collections.shuffle(deckOfCards);
    }

    protected int calculateValueOfCards(ArrayList<Card> cardsToBeTurnedIn) {
        return 0;
    }

    protected void verifyCardsCanBeTurnedIn(ArrayList<Card> cardsToBeTurnedIn) {
        canTurnInCards(cardsToBeTurnedIn);
    }

    //Used for integration testing in F6 Draw Secret Mission Cards.
    public void setCardOrder(ArrayList<Card> cardsInReverseOrder) {
        deckOfCards.clear();
        deckOfCards.addAll(cardsInReverseOrder);
        Collections.reverse(deckOfCards);
    }
}
