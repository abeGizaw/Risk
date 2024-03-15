package mainApp.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RiskDeckTest {

    /**
     * Ensures: Constructs an array of cards containing an amount of infantry, cavalry, artillery, and wildcards based
     * on the amountOfEachType. Index 0 corresponds to infantry, index 1 corresponds to cavalry and so on.
     * @param amountOfEachType The amount of each type of card to be made.
     * @return The array of cards
     */
    ArrayList<Card> constructCardsOfTroopType(int[] amountOfEachType) {
        if (amountOfEachType.length != 4) {
            throw new IllegalArgumentException("Array must be of size 4.");
        }
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < amountOfEachType[0]; i++) {
            Card infantryCard = new Card("Infantry", Integer.toString(i), "filePath");
            cards.add(infantryCard);
        }
        for (int i = 0; i < amountOfEachType[1]; i++) {
            Card cavalryCard = new Card("Cavalry", Integer.toString(i), "filePath");
            cards.add(cavalryCard);
        }
        for (int i = 0; i < amountOfEachType[2]; i++) {
            Card artilleryCard = new Card("Artillery", Integer.toString(i), "filePath");
            cards.add(artilleryCard);
        }
        for (int i = 0; i < amountOfEachType[3]; i++) {
            Card wildCard = new Card("Wildcard", Integer.toString(i), "filePath");
            cards.add(wildCard);
        }
        return cards;
    }

    /**
     * Ensures: Constructs an array of cards containing an amount of infantry, cavalry, artillery, and wildcards based
     * on the amountOfEachType. Index 0 corresponds to infantry, index 1 corresponds to cavalry and so on. Also scales
     * the value by the scalar to allow for uniqueness testing.
     * @param amountOfEachType The amount of each type of card to be made.
     * @param scalar The amount by which the value is scaled by.
     * @return The array of cards
     */
    private ArrayList<Card> constructScaledValueCardsOfTroopType(int[] amountOfEachType, int scalar) {
        if (amountOfEachType.length != 4) {
            throw new IllegalArgumentException("Array must be of size 4.");
        }
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 1; i <= amountOfEachType[0]; i++) {
            Card infantryCard = new Card("Infantry", Integer.toString(i * scalar), "filePath");
            cards.add(infantryCard);
        }
        for (int i = 1; i <= amountOfEachType[1]; i++) {
            Card cavalryCard = new Card("Cavalry", Integer.toString(i * scalar), "filePath");
            cards.add(cavalryCard);
        }
        for (int i = 1; i <= amountOfEachType[2]; i++) {
            Card artilleryCard = new Card("Artillery", Integer.toString(i * scalar), "filePath");
            cards.add(artilleryCard);
        }
        for (int i = 1; i <= amountOfEachType[3]; i++) {
            Card wildCard = new Card("Wildcard", Integer.toString(i * scalar), "filePath");
            cards.add(wildCard);
        }
        return cards;
    }

    private void deckContainsCards(Deck deck, ArrayList<Card> cards, boolean comparisonType) {
        if (comparisonType) {
            for (Card cardToBeTurnedIn: cards) {
                assertTrue(deck.containsCard(cardToBeTurnedIn));
            }
        } else {
            for (Card cardToBeTurnedIn: cards) {
                assertFalse(deck.containsCard(cardToBeTurnedIn));
            }
        }
    }

    @Test
    public void testConstructor_With45Cards_expectingIllegalStateException() {
        int[] cardTypes = {12, 11, 11, 11};
        ArrayList<Card> cards = constructCardsOfTroopType(cardTypes);
        String expectedErrorMessage = "Risk deck can not contain more than 44 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            Deck riskDeck = new RiskDeck(cards);
        });
        String actualErrorMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testContains_withCardAOnDeckContainingOnlyCardA_expectingTrue() {
        Card cardInDeck = new Card("type", "value", "filePath");
        ArrayList<Card> riskCards = new ArrayList<>();
        riskCards.add(cardInDeck);
        Deck riskDeckUnderTest = new RiskDeck(riskCards);

        boolean result = riskDeckUnderTest.containsCard(cardInDeck);

        assertTrue(result);
    }

    @Test
    public void testContains_withEmptyDeck_expectingFalse() {
        Card cardInDeck = new Card("type", "value", "filePath");
        ArrayList<Card> riskCards = new ArrayList<>();
        Deck emptyRiskDeck = new RiskDeck(riskCards);

        boolean result = emptyRiskDeck.containsCard(cardInDeck);

        assertFalse(result);
    }

    @Test
    public void testContains_withDeckContainingTwoOfThreeCards_expectingTrueFalseTrue() {
        ArrayList<Card> riskCardsInDeck = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            Card cardInDeck = new Card("type", Integer.toString(i), "filePath");
            riskCardsInDeck.add(cardInDeck);
        }
        Card firstCardInDeck = new Card("card", "in", "deck");
        Card cardNotInDeck = new Card("not", "in", "deck");
        Card secondCardinDeck = new Card("also", "in", "deck");
        riskCardsInDeck.add(firstCardInDeck);
        riskCardsInDeck.add(secondCardinDeck);
        Deck deckUnderTest = new RiskDeck(riskCardsInDeck);

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
        ArrayList<Card> riskCards = new ArrayList<>();
        Deck deckUnderTest = new RiskDeck(riskCards);
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
        Deck riskCardDeck = new RiskDeck(cards);
        int expectedSizeAfterFirstDraw = 1;
        int expectedSizeAfterSecondDraw = 0;

        Card drawnCard = riskCardDeck.drawOneCard();
        boolean containsFirstDrawnCard = riskCardDeck.containsCard(drawnCard);
        int sizeAfterFirstDraw = riskCardDeck.size();
        Card secondCardDrawn = riskCardDeck.drawOneCard();
        boolean containsSecondDrawnCard = riskCardDeck.containsCard(secondCardDrawn);
        int sizeAfterSecondDraw = riskCardDeck.size();

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
        Deck riskCardDeck = new RiskDeck(cards);
        int expectedSizeBeforeDraw = 1;
        int expectedSize = 0;

        int sizeBeforeDraw = riskCardDeck.size();
        Card drawnCard = riskCardDeck.drawOneCard();
        boolean containsCard = riskCardDeck.containsCard(drawnCard);
        int actualSize = riskCardDeck.size();

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
        Deck riskCardDeck = new RiskDeck(cards);
        int expectedSize = 7;

        for (Card containedCard: cards) {
            assertTrue(riskCardDeck.containsCard(containedCard));
        }
        int currentSize = riskCardDeck.size();
        assertEquals(expectedSize, currentSize);

        for (int i = 0; i < 7; i++) {
            expectedSize--;
            Card drawnCard = riskCardDeck.drawOneCard();
            boolean deckContainsDrawnCard = riskCardDeck.containsCard(drawnCard);
            currentSize = riskCardDeck.size();

            assertFalse(deckContainsDrawnCard);
            assertEquals(expectedSize, currentSize);
        }
    }

    @Test
    public void testDrawOneCard_withEmptyDeck_expectingIllegalStateException() {
        ArrayList<Card> emptyCards = new ArrayList<>();
        Deck emptyRiskDeck = new RiskDeck(emptyCards);
        String expectedErrorMessage = "The deck contains no cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            emptyRiskDeck.drawOneCard();
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testDrawOneCard_withDeckOfSize44_expectingIllegalStateException() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 44; i++) {
            Card cardInDeck = new Card(Integer.toString(i), Integer.toString(i * 2), "filePath");
            cards.add(cardInDeck);
        }
        Deck riskDeck = new RiskDeck(cards);
        int expectedSize = 43;

        Card drawnCard = riskDeck.drawOneCard();
        boolean containsDrawnCard = riskDeck.containsCard(drawnCard);
        int actualSize = riskDeck.size();

        assertFalse(containsDrawnCard);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testCanTurnInCards_withZeroCards_expectingFalse() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        ArrayList<Card> cardsToBeTurnedIn = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertFalse(result);
    }

    @Test
    public void testCanTurnInCards_withOneCard_expectingFalse() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {1, 0, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertFalse(result);
    }

    @Test
    public void testCanTurnInCards_withTwoCard_expectingFalse() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 1, 0, 1};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertFalse(result);
    }

    @Test
    public void testCanTurnInCards_withThreeInfantryCard_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {3, 0, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withThreeArtilleryCard_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 0, 3, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withThreeCavalryCard_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 3, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withOneInfantryCavalryAndArtillery_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {1, 1, 1, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withTwoCavalryAndOneArtillery_expectingFalse() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 2, 1, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertFalse(result);
    }

    @Test
    public void testCanTurnInCards_withOneWildCardAndTwoArtillery_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 0, 2, 1};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withTwoWildCardsAndOneCavalry_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 1, 0, 2};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_withTwoArtilleryAndTwoCavalry_expectingFalse() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {0, 2, 2, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertFalse(result);
    }

    @Test
    public void testCanTurnInCards_withFiveCards_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {2, 1, 2, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_with44Cards_expectingTrue() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        int[] cardTypes = {11, 11, 11, 11};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);

        boolean result = riskDeck.canTurnInCards(cardsToBeTurnedIn);

        assertTrue(result);
    }

    @Test
    public void testCanTurnInCards_with44CardsAndDeckContainingOneCard_expectingIllegalStateException() {
        int[] startingDeckCardTypes = {0, 0, 0, 1};
        ArrayList<Card> startingDeck = constructCardsOfTroopType(startingDeckCardTypes);
        int[] cardTypes = {11, 11, 11, 11};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);
        String expectedMessage = "Risk deck can not contain more than 44 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.canTurnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCanTurnInCards_withNineCardsAndDeckContaining36Card_expectingIllegalStateException() {
        int[] startingDeckCardTypes = {9, 9, 9, 9};
        ArrayList<Card> startingDeck = constructCardsOfTroopType(startingDeckCardTypes);
        int[] cardTypes = {2, 3, 3, 1};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);
        String expectedMessage = "Risk deck can not contain more than 44 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.canTurnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCanTurnInCards_withOneOfEachCardAndDeckContaining44Card_expectingTrue() {
        int[] startingDeckCardTypes = {14, 14, 14, 2};
        ArrayList<Card> startingDeck = constructCardsOfTroopType(startingDeckCardTypes);
        int[] cardTypes = {1, 1, 1, 1};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);
        String expectedMessage = "Risk deck can not contain more than 44 cards.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.canTurnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testTurnInCards_withOneSetOnDeckOfZeroCardsWithZeroPreviousTrades_expectingFour() {
        int[] startingDeckCardTypes = {0, 0, 0, 0};
        ArrayList<Card> startingDeck = constructScaledValueCardsOfTroopType(startingDeckCardTypes, 45);
        int[] cardTypes = {1, 1, 1, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructScaledValueCardsOfTroopType(cardTypes, 1);
        Deck riskDeck = new RiskDeck(startingDeck);
        int expectedInitialSize = 0;
        int expectedFinalSize = 3;
        int expectedValueOfTurnedInCards = 4;

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(riskDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = riskDeck.size();
        int valueOfTurnedInCards = riskDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = riskDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedFinalSize, finalSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnedInCards);

        for (Card turnedInCard: cardsToBeTurnedIn) {
            assertTrue(riskDeck.containsCard(turnedInCard));
        }
    }

    @Test
    public void testTurnInCards_withOneSetOnDeckOfThreeCardsWithOnePreviousTrade_expectingSix() {
        int[] startingDeckCardTypes = {0, 0, 0, 0};
        ArrayList<Card> startingDeck = constructScaledValueCardsOfTroopType(startingDeckCardTypes, 45);
        int[] firstTradeInCardTypes = {1, 0, 1, 1};
        ArrayList<Card> firstSetTradedIn = constructScaledValueCardsOfTroopType(firstTradeInCardTypes, 4);
        int[] cardTypes = {1, 1, 1, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructScaledValueCardsOfTroopType(cardTypes, 1);
        Deck riskDeck = new RiskDeck(startingDeck);
        int expectedInitialSize = 3;
        int expectedFinalSize = 6;
        int expectedValueOfTurnedInCards = 6;
        riskDeck.turnInCards(firstSetTradedIn);

        for (Card cardToBeTurnedIn: cardsToBeTurnedIn) {
            assertFalse(riskDeck.containsCard(cardToBeTurnedIn));
        }

        int initialSize = riskDeck.size();
        int valueOfTurnedInCards = riskDeck.turnInCards(cardsToBeTurnedIn);
        int finalSize = riskDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedFinalSize, finalSize);
        assertEquals(expectedValueOfTurnedInCards, valueOfTurnedInCards);

        for (Card turnedInCard: cardsToBeTurnedIn) {
            assertTrue(riskDeck.containsCard(turnedInCard));
        }
    }

    @Test
    public void testTurnInCards_withTwoSetsOnDeckOfSixCardsWithTwoPreviousTrade_expectingEightThenTen() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] firstTradeInCardTypes = {1, 0, 1, 1};
        ArrayList<Card> firstSetTradedIn = constructScaledValueCardsOfTroopType(firstTradeInCardTypes, 1);
        int[] secondTradeInCardTypes = {3, 0, 0, 0};
        ArrayList<Card> secondSetTradedIn = constructScaledValueCardsOfTroopType(secondTradeInCardTypes, 4);

        for (int i = 1; i <= 2; i++) {
            int[] cardTypes = {1, 1, 1, 0};
            ArrayList<Card> cards = constructScaledValueCardsOfTroopType(cardTypes, i * 45);
            riskDeck.turnInCards(cards);
        }

        int expectedInitialSize = 6;
        int expectedSizeAfterFirstTradeIn = 9;
        int expectedFinalSize = 12;
        int expectedValueOfFirstTurnedInCards = 8;
        int expectedValueOfSecondTurnedInCards = 10;

        deckContainsCards(riskDeck, firstSetTradedIn, false);
        deckContainsCards(riskDeck, secondSetTradedIn, false);

        int initialSize = riskDeck.size();
        int valueOfFirstTurnedInCards = riskDeck.turnInCards(firstSetTradedIn);
        int sizeAfterFirstTradeIn = riskDeck.size();

        deckContainsCards(riskDeck, firstSetTradedIn, true);
        deckContainsCards(riskDeck, secondSetTradedIn, false);

        int valueOfSecondTurnedInCards = riskDeck.turnInCards(secondSetTradedIn);
        int finalSize = riskDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedSizeAfterFirstTradeIn, sizeAfterFirstTradeIn);
        assertEquals(expectedFinalSize, finalSize);
        assertEquals(expectedValueOfFirstTurnedInCards, valueOfFirstTurnedInCards);
        assertEquals(expectedValueOfSecondTurnedInCards, valueOfSecondTurnedInCards);

        deckContainsCards(riskDeck, firstSetTradedIn, true);
        deckContainsCards(riskDeck, secondSetTradedIn, true);
    }

    @Test
    public void testTurnInCards_withThreeSetsOnDeckOfTwelveCardsWithFourPreviousTrade_expecting12Then15Then20() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] firstTradeInCardTypes = {1, 0, 1, 1};
        ArrayList<Card> firstSetTradedIn = constructScaledValueCardsOfTroopType(firstTradeInCardTypes, 1);
        int[] secondTradeInCardTypes = {3, 0, 0, 0};
        ArrayList<Card> secondSetTradedIn = constructScaledValueCardsOfTroopType(secondTradeInCardTypes, 4);
        int[] thirdTradeInCardTypes = {0, 0, 1, 2};
        ArrayList<Card> thirdSetTradedIn = constructScaledValueCardsOfTroopType(thirdTradeInCardTypes, 5);

        for (int i = 1; i <= 4; i++) {
            int[] cardTypes = {1, 1, 1, 0};
            ArrayList<Card> cards = constructScaledValueCardsOfTroopType(cardTypes, i * 45);
            riskDeck.turnInCards(cards);
        }

        int expectedInitialSize = 12;
        int expectedSizeAfterFirstTradeIn = 15;
        int expectedSizeAfterSecondTradeIn = 18;
        int expectedFinalSize = 21;
        int expectedValueOfFirstTurnedInCards = 12;
        int expectedValueOfSecondTurnedInCards = 15;
        int expectedValueOfThirdTurnedInCards = 20;

        deckContainsCards(riskDeck, firstSetTradedIn, false);
        deckContainsCards(riskDeck, secondSetTradedIn, false);
        deckContainsCards(riskDeck, thirdSetTradedIn, false);

        int initialSize = riskDeck.size();
        int valueOfFirstTurnedInCards = riskDeck.turnInCards(firstSetTradedIn);
        int sizeAfterFirstTradeIn = riskDeck.size();

        deckContainsCards(riskDeck, firstSetTradedIn, true);
        deckContainsCards(riskDeck, secondSetTradedIn, false);
        deckContainsCards(riskDeck, thirdSetTradedIn, false);

        int valueOfSecondTurnedInCards = riskDeck.turnInCards(secondSetTradedIn);
        int sizeAfterSecondTradeIn = riskDeck.size();

        deckContainsCards(riskDeck, firstSetTradedIn, true);
        deckContainsCards(riskDeck, secondSetTradedIn, true);
        deckContainsCards(riskDeck, thirdSetTradedIn, false);

        int valueOfThirdTurnedInCards = riskDeck.turnInCards(thirdSetTradedIn);
        int finalSize = riskDeck.size();

        assertEquals(expectedInitialSize, initialSize);
        assertEquals(expectedSizeAfterFirstTradeIn, sizeAfterFirstTradeIn);
        assertEquals(expectedSizeAfterSecondTradeIn, sizeAfterSecondTradeIn);
        assertEquals(expectedFinalSize, finalSize);
        assertEquals(expectedValueOfFirstTurnedInCards, valueOfFirstTurnedInCards);
        assertEquals(expectedValueOfSecondTurnedInCards, valueOfSecondTurnedInCards);
        assertEquals(expectedValueOfThirdTurnedInCards, valueOfThirdTurnedInCards);

        deckContainsCards(riskDeck, firstSetTradedIn, true);
        deckContainsCards(riskDeck, secondSetTradedIn, true);
        deckContainsCards(riskDeck, thirdSetTradedIn, true);
    }

    @Test
    public void testTurnInCards_withOneSetDrawn13TimesFromDeckOfFourCardsWithZeroPreviousTrades() {
        int[] cardTypes = {1, 1, 1, 1};
        ArrayList<Card> startingDeck = constructCardsOfTroopType(cardTypes);
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] expectedTradeInValues = {4, 6, 8, 10, 12, 15, 20, 25, 30, 35, 40, 45, 50};

        for (int i = 0; i < 13; i++) {
            ArrayList<Card> cardsToTurnIn = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                cardsToTurnIn.add(riskDeck.drawOneCard());
            }
            deckContainsCards(riskDeck, cardsToTurnIn, false);
            int tradeInValue = riskDeck.turnInCards(cardsToTurnIn);
            assertEquals(expectedTradeInValues[i], tradeInValue);
            deckContainsCards(riskDeck, cardsToTurnIn, true);
        }
    }

    @Test
    public void testTurnInCards_withSetOfZeroCards_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        ArrayList<Card> cardsToBeTurnedIn = new ArrayList<>();
        String expectedErrorMessage = "Can only turn in 3 risk cards at a time.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.turnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testTurnInCards_withSetOfOneCard_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] cardTypes = {1, 0, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        String expectedErrorMessage = "Can only turn in 3 risk cards at a time.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.turnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testTurnInCards_withSetOfTwoCards_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] cardTypes = {1, 1, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        String expectedErrorMessage = "Can only turn in 3 risk cards at a time.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.turnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testTurnInCards_withSetOfFourCards_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] cardTypes = {1, 1, 1, 1};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        String expectedErrorMessage = "Can only turn in 3 risk cards at a time.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.turnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    public void testTurnInCards_withInvalidSetOfThreeCards_expectingIllegalStateException() {
        ArrayList<Card> startingDeck = new ArrayList<>();
        Deck riskDeck = new RiskDeck(startingDeck);
        int[] cardTypes = {1, 2, 0, 0};
        ArrayList<Card> cardsToBeTurnedIn = constructCardsOfTroopType(cardTypes);
        String expectedErrorMessage = "This set of cards can not be turned in.";

        Exception exception = assertThrows(IllegalStateException.class, ()-> {
            riskDeck.turnInCards(cardsToBeTurnedIn);
        });
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMessage, actualMessage);
    }
}
