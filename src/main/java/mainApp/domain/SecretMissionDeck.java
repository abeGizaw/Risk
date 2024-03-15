package mainApp.domain;

import java.util.ArrayList;

public class SecretMissionDeck  extends Deck{

    SecretMissionDeck(ArrayList<Card> riskCards) {
        super(riskCards);
        maximumDeckSize = 11;
        if (size() > maximumDeckSize) {
            throw new IllegalStateException("Secret mission deck can not contain more than 11 cards.");
        }
    }

    @Override
    public boolean canTurnInCards(ArrayList<Card> cardsToBeTurnedIn) {
        if (size() + cardsToBeTurnedIn.size() > maximumDeckSize) {
            throw new IllegalStateException("Secret mission deck can not contain more than 11 cards.");
        }
        return true;
    }
}
