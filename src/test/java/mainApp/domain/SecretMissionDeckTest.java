package mainApp.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecretMissionDeckTest {

    private ArrayList<Card> fillWithCards(int numberOfCards) {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            Card card = new Card("Type", "Value", "FilePath");
            cards.add(card);
        }
        return cards;
    }

    private ArrayList<Card> fillWithCardsOfScaledValue(int numberOfCards, int valueScalar) {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 1; i <= numberOfCards; i++) {
            Card card = new Card("Type", Integer.toString(i * valueScalar), "FilePath");
            cards.add(card);
        }
        return cards;
    }

    @Test
    public void testConstructor_withTwelveCards_expectingIllegalStateException() {
        ArrayList<Card> cards = fillWithCards(12);
        String expectedErrorMessage = "Secret mission deck can not contain more than 11 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
           Deck secretMissionDeck = new SecretMissionDeck(cards);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testContains_withCardAOnDeckContainingOnlyCardA_expectingTrue() {
        Card cardInDeck = new Card("type", "value", "filePath");
        ArrayList<Card> secretMissionCards = new ArrayList<>();
        secretMissionCards.add(cardInDeck);
        Deck secretMissionDeckUnderTest = new SecretMissionDeck(secretMissionCards);

        boolean result = secretMissionDeckUnderTest.containsCard(cardInDeck);

        assertTrue(result);
    }

    @Test
    public void testContains_withEmptyDeck_expectingFalse() {
        Card cardInDeck = new Card("type", "value", "filePath");
        ArrayList<Card> secretMissionCards = new ArrayList<>();
        Deck emptysecretMissionDeck = new SecretMissionDeck(secretMissionCards);

        boolean result = emptysecretMissionDeck.containsCard(cardInDeck);

        assertFalse(result);
    }

    @Test
    public void testContains_withDeckContainingTwoOfThreeCards_expectingTrueFalseTrue() {
        ArrayList<Card> secretMissionCardsInDeck = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Card cardInDeck = new Card("type", Integer.toString(i), "filePath");
            secretMissionCardsInDeck.add(cardInDeck);
        }
        Card firstCardInDeck = new Card("card", "in", "deck");
        Card cardNotInDeck = new Card("not", "in", "deck");
        Card secondCardinDeck = new Card("also", "in", "deck");
        secretMissionCardsInDeck.add(firstCardInDeck);
        secretMissionCardsInDeck.add(secondCardinDeck);
        Deck deckUnderTest = new SecretMissionDeck(secretMissionCardsInDeck);

        boolean firstCardInDeckResult = deckUnderTest.containsCard(firstCardInDeck);
        boolean cardNotInDeckResult = deckUnderTest.containsCard(cardNotInDeck);
        boolean secondCardInDeckResult = deckUnderTest.containsCard(secondCardinDeck);

        assertTrue(firstCardInDeckResult);
        assertFalse(cardNotInDeckResult);
        assertTrue(secondCardInDeckResult);
    }

    @Test
    public void testContains_withNullPointer_expectingNullPointerException() {
        Card nullCard = null;
        ArrayList<Card> secretMissionCards = new ArrayList<>();
        Deck deckUnderTest = new SecretMissionDeck(secretMissionCards);
        String expectedErrorMessage = "Card can not be null for containsCard.";

        Exception exception = assertThrows(NullPointerException.class, () -> {
            deckUnderTest.containsCard(nullCard);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testDrawOneCard_withDeckOfSize2_expectingContainsReturnsFalse() {
        Card cardInDeck = new Card("type", "value", "filepath");
        Card secondCardInDeck = new Card("do you", "read the", "test cases?");
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(cardInDeck);
        cards.add(secondCardInDeck);
        Deck secretMissionCardDeck = new SecretMissionDeck(cards);
        int expectedSizeAfterFirstDraw = 1;
        int expectedSizeAfterSecondDraw = 0;

        Card drawnCard = secretMissionCardDeck.drawOneCard();
        boolean containsFirstDrawnCard = secretMissionCardDeck.containsCard(drawnCard);
        int sizeAfterFirstDraw = secretMissionCardDeck.size();
        Card secondCardDrawn = secretMissionCardDeck.drawOneCard();
        boolean containsSecondDrawnCard = secretMissionCardDeck.containsCard(secondCardDrawn);
        int sizeAfterSecondDraw = secretMissionCardDeck.size();

        assertFalse(containsFirstDrawnCard);
        assertEquals(expectedSizeAfterFirstDraw, sizeAfterFirstDraw);
        assertFalse(containsSecondDrawnCard);
        assertEquals(expectedSizeAfterSecondDraw, sizeAfterSecondDraw);
    }

    @Test
    public void testDrawOneCard_withDeckOfSize1_expectingContainsReturnsFalse() {
        Card cardInDeck = new Card("type", "value", "filepath");
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(cardInDeck);
        Deck secretMissionCardDeck = new SecretMissionDeck(cards);
        int expectedSizeBeforeDraw = 1;
        int expectedSize = 0;

        int sizeBeforeDraw = secretMissionCardDeck.size();
        Card drawnCard = secretMissionCardDeck.drawOneCard();
        boolean containsCard = secretMissionCardDeck.containsCard(drawnCard);
        int actualSize = secretMissionCardDeck.size();

        assertEquals(expectedSizeBeforeDraw, sizeBeforeDraw);
        assertFalse(containsCard);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testDrawOneCard_DrawAllCardsFromDeckOfSizeSeven() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Card cardInDeck = new Card("type", Integer.toString(i), "filePath");
            cards.add(cardInDeck);
        }
        Deck secretMissionCardDeck = new SecretMissionDeck(cards);
        int expectedSize = 7;

        for (Card containedCard: cards) {
            assertTrue(secretMissionCardDeck.containsCard(containedCard));
        }
        int currentSize = secretMissionCardDeck.size();
        assertEquals(expectedSize, currentSize);

        for (int i = 0; i < 7; i++) {
            expectedSize--;
            Card drawnCard = secretMissionCardDeck.drawOneCard();
            boolean deckContainsDrawnCard = secretMissionCardDeck.containsCard(drawnCard);
            currentSize = secretMissionCardDeck.size();

            assertFalse(deckContainsDrawnCard);
            assertEquals(expectedSize, currentSize);
        }
    }

    @Test
    public void testDrawOneCard_withEmptyDeck_expectingIllegalStateException() {
        ArrayList<Card> emptyCards = new ArrayList<>();
        Deck emptySecretMissionDeck = new SecretMissionDeck(emptyCards);
        String expectedErrorMessage = "The deck contains no cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            emptySecretMissionDeck.drawOneCard();
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testDrawOneCard_withDeckOfSize11_expectingIllegalStateException() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Card cardInDeck = new Card(Integer.toString(i), Integer.toString(i * 2), "filePath");
            cards.add(cardInDeck);
        }
        Deck secretMissionDeck = new SecretMissionDeck(cards);
        int expectedSize = 10;

        Card drawnCard = secretMissionDeck.drawOneCard();
        boolean containsDrawnCard = secretMissionDeck.containsCard(drawnCard);
        int actualSize = secretMissionDeck.size();

        assertFalse(containsDrawnCard);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testCanTurnInCards_withZeroCards_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = new ArrayList<>();
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        boolean result = secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withOneCard_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        boolean result = secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withTwoCards_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(2);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        boolean result = secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withElevenCards_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(11);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        boolean result = secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withTwelveCards_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(12);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        String expectedMessage = "Secret mission deck can not contain more than 11 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCanTurnInCards_withOneCardsWithDeckOfSizeEleven_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = fillWithCards(11);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        String expectedMessage = "Secret mission deck can not contain more than 11 cards.";

        assertEquals(11, secretMissionDeck.size());
        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCanTurnInCards_withThreeCardsAndDeckContainingOneCard_expectingTrue() {
        ArrayList<Card> startingDeck = fillWithCards(1);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCards(3);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);

        assertEquals(1, secretMissionDeck.size());
        boolean result = secretMissionDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testTurnInCards_withZeroCardsOnDeckOfZeroCards_ExpectingZero() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = new ArrayList<>();
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        int expectedInitialSize = 0;
        int expectedFinalSize = 0;
        int expectedValueOfTurnedInCards = 0;

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(secretMissionDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = secretMissionDeck.size();
        int valueOfTurnInCards = secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = secretMissionDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnInCards);
        assertEquals(expectedFinalSize, finalSize);

        for (Card cardTurnedIn: cardsToBeTurnedIn) {
            assertTrue(secretMissionDeck.containsCard(cardTurnedIn));
        }
    }

    @Test
    public void testTurnInCards_withOneCardOnDeckOfOneCard_ExpectingZero() {
        ArrayList<Card> startingDeck = fillWithCardsOfScaledValue(1, 12);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCardsOfScaledValue(1, 1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        int expectedInitialSize = 1;
        int expectedFinalSize = 2;
        int expectedValueOfTurnedInCards = 0;

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(secretMissionDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = secretMissionDeck.size();
        int valueOfTurnInCards = secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = secretMissionDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnInCards);
        assertEquals(expectedFinalSize, finalSize);

        for (Card cardTurnedIn: cardsToBeTurnedIn) {
            assertTrue(secretMissionDeck.containsCard(cardTurnedIn));
        }
    }

    @Test
    public void testTurnInCards_withTwoCardOnDeckOfTwoCard_ExpectingZero() {
        ArrayList<Card> startingDeck = fillWithCardsOfScaledValue(2, 12);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCardsOfScaledValue(2, 1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        int expectedInitialSize = 2;
        int expectedFinalSize = 4;
        int expectedValueOfTurnedInCards = 0;

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(secretMissionDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = secretMissionDeck.size();
        int valueOfTurnInCards = secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = secretMissionDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnInCards);
        assertEquals(expectedFinalSize, finalSize);

        for (Card cardTurnedIn: cardsToBeTurnedIn) {
            assertTrue(secretMissionDeck.containsCard(cardTurnedIn));
        }
    }

    @Test
    public void testTurnInCards_withElevenCardOnDeckOfZeroCard_ExpectingZero() {
        ArrayList<Card> startingDeck = fillWithCardsOfScaledValue(0, 12);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCardsOfScaledValue(11, 1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        int expectedInitialSize = 0;
        int expectedFinalSize = 11;
        int expectedValueOfTurnedInCards = 0;

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(secretMissionDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = secretMissionDeck.size();
        int valueOfTurnInCards = secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = secretMissionDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnInCards);
        assertEquals(expectedFinalSize, finalSize);

        for (Card cardTurnedIn: cardsToBeTurnedIn) {
            assertTrue(secretMissionDeck.containsCard(cardTurnedIn));
        }
    }

    @Test
    public void testTurnInCards_withTwelveCardOnDeckOfZeroCard_ExpectingIllegalStateException() {
        ArrayList<Card> startingDeck = fillWithCardsOfScaledValue(0, 12);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCardsOfScaledValue(12, 1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        String expectedErrorMessage = "Secret mission deck can not contain more than 11 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
           secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    public void testTurnInCards_withSixCardOnDeckOfSixCard_ExpectingIllegalStateException() {
        ArrayList<Card> startingDeck = fillWithCardsOfScaledValue(6, 12);
        ArrayList<Card> cardsToBeTurnedIn = fillWithCardsOfScaledValue(6, 1);
        Deck secretMissionDeck = new SecretMissionDeck(startingDeck);
        String expectedErrorMessage = "Secret mission deck can not contain more than 11 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            secretMissionDeck.turnInCards(cardsToBeTurnedIn);
        });
        String errorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, errorMessage);
    }
}
