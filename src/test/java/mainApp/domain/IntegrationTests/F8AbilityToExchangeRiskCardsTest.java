package mainApp.domain.IntegrationTests;

import mainApp.domain.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class F8AbilityToExchangeRiskCardsTest {
    private ArrayList<Player> players;
    private RiskDeck riskCards;
    private Collection<Territory> allTerritories;
    private final Dimension standardScreenSize = new Dimension(1536, 864);
    private final String[] colors = {"Red", "Green", "Blue"};
    private Game game;



    private void initializeGameState() throws IOException {
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Initializer initializer = new Initializer(standardScreenSize);
        initializer.createAllEntities();
        players = initializer.makePlayers(3, playerColors);
        riskCards = initializer.getRiskCards();

        allTerritories = initializer.getTerritories();



        ResourceBundle message = ResourceBundle.getBundle("message");
        game = initializer.makeGame(players, false, message);
    }



    @Test
    public void playerTriesToTurnInInvalidCards_ExpectException() throws IOException {
        initializeGameState();
        ArrayList<Card> invalidSetOfCards = getMatchingSet("Alaska", "Congo", "Greenland");
        Player currentPlayer = players.get(0);


        for (Card card : invalidSetOfCards){
            currentPlayer.addCard(card);
        }

        assertEquals(3, currentPlayer.getCards().size());

        String expected = "This set of cards can not be turned in.";

        game.setRiskDeck(riskCards);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> game.turnCardsIn(invalidSetOfCards));

        assertEquals(expected, exception.getMessage());


    }

    @Test
    public void playerTurnsInValidCards_OwnsNoneOfTheTerritories_Expect4AdditionalTroops() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Alaska", "India", "Japan");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);

        int initialDeployableTroops = currentPlayer.getDeployableTroops();

        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }

        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(validSetOfCards);

        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());

        for (Territory territory : ownedTerritories){
            assertEquals(1, territory.getCurrentNumberOfTroops());
        }
        assertEquals(0, currentPlayer.getCards().size());
    }

    @Test
    public void playerTurnsInValidCards_OwnsOneOfTheTerritories_Expect4AdditionalTroopsAnd2ToTerritory() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Congo", "India", "Japan");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);


        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }
        int initialDeployableTroops = currentPlayer.getDeployableTroops();


        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(validSetOfCards);

        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());

        for (Territory territory : ownedTerritories){
            if (territory.getTerritoryName().equals("Congo")){
                assertEquals(3, territory.getCurrentNumberOfTroops());
            } else {
                assertEquals(1, territory.getCurrentNumberOfTroops());
            }
        }
        assertEquals(0, currentPlayer.getCards().size());
    }

    @Test
    public void playerTurnsInValidCards_OwnsTwoOfTheTerritories_Expect4AdditionalTroopsAnd2ToBothTerritories() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Congo", "Greenland", "Japan");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);
        int initialDeployableTroops = currentPlayer.getDeployableTroops();


        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }

        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(validSetOfCards);

        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());

        for (Territory territory : ownedTerritories){
            if (territory.getTerritoryName().equals("Brazil")){
                assertEquals(1, territory.getCurrentNumberOfTroops());
            } else {
                assertEquals(3, territory.getCurrentNumberOfTroops());
            }
        }
        assertEquals(0, currentPlayer.getCards().size());
    }

    @Test
    public void playerTurnsInValidCards_OwnsAllOfTheTerritories_Expect4AdditionalTroopsAnd2ToAllTerritories() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Congo", "Greenland", "Brazil");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);

        int initialDeployableTroops = currentPlayer.getDeployableTroops();

        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }

        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(validSetOfCards);

        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());

        for (Territory territory : ownedTerritories){
            assertEquals(3, territory.getCurrentNumberOfTroops());
        }
        assertEquals(0, currentPlayer.getCards().size());


    }


    @Test
    public void multiplePlayersTurnsInCards_EachOwnsATerritory_Expect4and6AdditionalTroopsAnd2ToEachTerritory() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Congo", "Greenland", "Brazil");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "India", "Japan");
        Player currentPlayer = players.get(0);

        int initialDeployableTroops = currentPlayer.getDeployableTroops();
        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }
        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());



        game.setRiskDeck(riskCards);
        int updatedTroops = game.turnCardsIn(validSetOfCards);


        assertEquals(updatedTroops, currentPlayer.getDeployableTroops());
        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());

        for (Territory territory : ownedTerritories){
            if (territory.getTerritoryName().equals("Congo")){
               assertEquals(3, territory.getCurrentNumberOfTroops());
            } else {
                assertEquals(1, territory.getCurrentNumberOfTroops());
            }
        }
        assertEquals(0, currentPlayer.getCards().size());



        ArrayList<Card> nextSetOfValidCards = getMatchingSet("Alaska", "India", "Brazil");
        for (Card card : nextSetOfValidCards){
            currentPlayer.addCard(card);
        }
        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(nextSetOfValidCards);


        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        for (Territory territory : ownedTerritories){
            if (territory.getTerritoryName().equals("Japan")){
                assertEquals(1, territory.getCurrentNumberOfTroops());
            } else {
                assertEquals(3, territory.getCurrentNumberOfTroops());
            }
        }
        assertEquals(0, currentPlayer.getCards().size());
    }

    @Test
    public void playerTurnsInValidCards_OwnsOneOfTheTerritoriesAndHasWildcard_Expect4AdditionalTroopsAnd2ToTerritory() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Congo", "Wildcard", "Japan");
        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);
        int initialDeployableTroops = currentPlayer.getDeployableTroops();

        for (Card card : validSetOfCards){
            currentPlayer.addCard(card);
        }
        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }

        assertEquals(3, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);
        int newTroops = game.turnCardsIn(validSetOfCards);


        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());
        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        for (Territory territory : ownedTerritories){
            if (territory.getTerritoryName().equals("Congo")){
                assertEquals(3, territory.getCurrentNumberOfTroops());
            } else {
                assertEquals(1, territory.getCurrentNumberOfTroops());
            }
        }
        assertEquals(0, currentPlayer.getCards().size());
    }

    @Test
    public void playerTurnsInValidCards_HasMultipleValidSets_Expect4AdditionalTroopsAndChosenToBeRemoved() throws IOException {
        initializeGameState();

        ArrayList<Card> validSetOfCards = getMatchingSet("Wildcard", "Wildcard", "Japan");
        ArrayList<Card> otherSetOfValidCards = getMatchingSet("Afghanistan", "Argentina", "Central America");

        ArrayList<Territory> ownedTerritories = findTerritories("Congo", "Greenland", "Brazil");
        Player currentPlayer = players.get(0);
        int initialDeployableTroops = currentPlayer.getDeployableTroops();

        for (int i = 0; i < validSetOfCards.size(); i++){
            currentPlayer.addCard(validSetOfCards.get(i));
            currentPlayer.addCard(otherSetOfValidCards.get(i));
        }


        for (Territory territory : ownedTerritories){
            currentPlayer.addTerritory(territory);
            territory.addAdditionalTroops(1);
        }

        assertEquals(6, currentPlayer.getCards().size());
        assertEquals(3, currentPlayer.territoryCount());


        game.setRiskDeck(riskCards);

        ArrayList<ArrayList<Card>> allSetsOfCards = game.allSetsOfValidCards();

        assertEquals(18, allSetsOfCards.size());


        int newTroops = game.turnCardsIn(allSetsOfCards.get(0));

        assertEquals(initialDeployableTroops + 4, currentPlayer.getDeployableTroops());
        assertEquals(newTroops, currentPlayer.getDeployableTroops());
        for (Territory territory : ownedTerritories){
            assertEquals(1, territory.getCurrentNumberOfTroops());
        }
        assertEquals(3, currentPlayer.getCards().size());
    }

    private ArrayList<Territory> findTerritories(String territoryOneVal, String territoryTwoVal, String territoryThreeVal) {
        ArrayList<Territory> setOfTerritories = new ArrayList<>();

        for (Territory territory : allTerritories){
            if (territory.getTerritoryName().equals(territoryOneVal)
                    || territory.getTerritoryName().equals(territoryTwoVal)
                    || territory.getTerritoryName().equals(territoryThreeVal)){
                setOfTerritories.add(territory);
            }
        }
        return setOfTerritories;
    }


    private ArrayList<Card> getMatchingSet(String territoryOneVal, String territoryTwoVal, String territoryThreeVal) {
        ArrayList<Card> setOfCards = new ArrayList<>();
        ArrayList<Card> setOfUnusedCards = new ArrayList<>();

        boolean t1Added = false;
        boolean t2Added = false;
        boolean t3Added = false;

        while (setOfCards.size() < 3){
            Card currentCard = riskCards.drawOneCard();
            if (currentCard.value().equals(territoryOneVal) && !t1Added){
                setOfCards.add(currentCard);
                t1Added = true;
            } else if (currentCard.value().equals(territoryTwoVal) && !t2Added){
                setOfCards.add(currentCard);
                t2Added = true;
            } else if (currentCard.value().equals(territoryThreeVal) && !t3Added){
                setOfCards.add(currentCard);
                t3Added = true;
            } else {
                setOfUnusedCards.add(currentCard);
            }
        }

        for (Card card : setOfUnusedCards){
            riskCards.turnInCard(card);
        }

        return setOfCards;

    }




}
