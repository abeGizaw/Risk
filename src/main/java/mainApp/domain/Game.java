package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    private final ArrayList<Player> players;
    private final Collection<Territory> territories;
    private final HashMap<Continent, Territory[]> territoriesByContinentMap;
    private RiskDeck riskCards;
    private final SecretMissionDeck secretMissionDeck;
    private final boolean secretMissionMode;
    private ResourceBundle messages;
    private ArrayList<Card> secretMissionCardsByPlayer;

    private GameState gameState = GameState.SETUP;
    private AttackPhase attackState = AttackPhase.ATTACKFROM;
    private ManeuverPhase maneuverState = ManeuverPhase.MANEUVERFROM;
    private int turnIndex = 0;
    private Territory territoryInPlay;
    private boolean canDrawCard = false;

    private final Attack recentAttack;
    private final Maneuver maneuver;

    @SuppressFBWarnings
    public Game(ArrayList<Player> playersInput, Collection<Territory> allTerritories, HashMap<Continent, Territory[]> continentMapInput,
                RiskDeck allRiskCards, Attack attack, Maneuver maneuverInput, SecretMissionDeck secretMissionCards) {
        messages = ResourceBundle.getBundle("message");

        this.players = playersInput;
        this.territories = allTerritories;
        this.territoriesByContinentMap = continentMapInput;
        this.riskCards = allRiskCards;
        this.recentAttack = attack;
        this.maneuver = maneuverInput;
        secretMissionDeck = secretMissionCards;
        secretMissionMode = (secretMissionCards != null);

        secretMissionCardsByPlayer = new ArrayList<>();

    }

    public void clickedOnPoint(Point point) {
        territoryInPlay = null;
        for (Territory territory : territories) {
            if (territory.clickedOnTerritory(point)) {
                if (gameState == GameState.SETUP) {
                    setupFlow(territory);
                } else if (gameState == GameState.DEPLOY) {
                    deployFlow(territory);
                } else if (gameState == GameState.ATTACK) {
                    attackFlow(territory);
                } else if (gameState == GameState.MANEUVER) {
                    maneuverFlow(territory);
                }
            }
        }
    }


    private void deployFlow(Territory territory) {
        Player currentPlayer = players.get(turnIndex);
        if (!currentPlayer.ownsTerritory(territory)) {
            String deployFlowErrorMessage = messages.getString("deployFlowErrorMessage");
            throw new IllegalStateException(deployFlowErrorMessage);
        }
        territoryInPlay = territory;
    }

    public void updateDeployableTroops(int deployedTroops, Territory currentTerritory) {
        Player currentPlayer = players.get(turnIndex);

        currentPlayer.removeDeployableTroops(deployedTroops);
        currentTerritory.addAdditionalTroops(deployedTroops);

        if (currentPlayer.getDeployableTroops() == 0) {
            gameState = GameState.NEWATTACK;
        }
        if (secretMissionMode) {
            checkForSecretMissionWin();
        }
    }

    private void attackFlow(Territory territory) {
        territoryInPlay = territory;
        if (attackState == AttackPhase.ATTACKFROM) {
            if (recentAttack.validateAttackingTerritory(territory, players.get(turnIndex))) {
                attackState = AttackPhase.DEFENDWITH;
            } else {
                String attackFlowErrorMessage = messages.getString("attackFlowErrorMessage");
                throw new IllegalArgumentException(attackFlowErrorMessage);
            }

        } else if (attackState == AttackPhase.DEFENDWITH) {
            Player owner = null;
            for (Player player : players) {
                if (player.ownsTerritory(territory)) {
                    owner = player;
                }
            }

            if (recentAttack.validateDefendingTerritory(territory, owner)) {
                attackState = AttackPhase.CHOOSETROOPS;
            } else {
                String invalidDefendingMessage = messages.getString("invalidDefendingMessage");
                throw new IllegalArgumentException(invalidDefendingMessage);
            }

        } else if (attackState == AttackPhase.CHOOSETROOPS) {
            gameState = GameState.CHOOSE;
        }
    }

    private void maneuverFlow(Territory territory) {
        if (maneuverState == ManeuverPhase.MANEUVERFROM) {
            handleValidateManeuverFrom(territory);
        } else if (maneuverState == ManeuverPhase.MANEUVERTO) {
            handleValidateManeuverTo(territory);
        }
    }

    private void handleValidateManeuverFrom(Territory territory) {
        Player currentPlayer = players.get(turnIndex);
        if (maneuver.validateManeuverFrom(currentPlayer, territory)) {
            maneuverState = ManeuverPhase.MANEUVERTO;
        } else {
            String invalidManeuverFromMessage = messages.getString("invalidManeuverFromMessage");
            throw new IllegalArgumentException(invalidManeuverFromMessage);
        }
    }

    private void handleValidateManeuverTo(Territory territory) {
        if (maneuver.validateManeuverTo(territory)) {
            maneuverState = ManeuverPhase.CHOOSETROOPSTOMANEUVER;
        } else {
            String invalidManeuverToMessage = messages.getString("invalidManeuverToMessage");
            throw new IllegalArgumentException(invalidManeuverToMessage);
        }
    }

    private void setupFlow(Territory territory) {
        if (!allTerritoriesAssigned()) {
            placeFirstTroops(territory);
        } else if (!placedAllInitialTroops()) {
            placeRestOfTroops(territory);
        }

    }

    /**
     * Ensures: If passed in territory belong to the current players, one troop will be added to the territory
     *
     * @param territory - territory to add a troop too during the setup phase
     */
    private void placeRestOfTroops(Territory territory) {
        Player currentPlayer = players.get(turnIndex);

        if (!currentPlayer.ownsTerritory(territory)) {
            String placeRestTroopsErrorMessage = messages.getString("placeRestTroopsErrorMessage");
            throw new IllegalStateException(placeRestTroopsErrorMessage);
        }
        territory.addAdditionalTroops(1);
        currentPlayer.removeDeployableTroops(1);

        turnIndex = (turnIndex + 1) % players.size();
        territoryInPlay = null;
        if (placedAllInitialTroops()) {
            if (secretMissionMode) {
                drawSecretMissionCards();
            }
            gameState = GameState.DEPLOY;
            allocatePlayerTroops(players.get(turnIndex));
        }
    }


    public void drawSecretMissionCards() {
        secretMissionCardsByPlayer = new ArrayList<>();
        ArrayList<String> invalidColors = generateUnusedPlayerColors();
        for (int i = 0; i < players.size(); i++) {
            Card secretMissionCardToAdd = secretMissionDeck.drawOneCard();
            while (invalidColors.contains(secretMissionCardToAdd.value())) {
                secretMissionCardToAdd = secretMissionDeck.drawOneCard();
            }
            secretMissionCardsByPlayer.add(i, secretMissionCardToAdd);
        }
    }

    private ArrayList<String> generateUnusedPlayerColors() {
        String[] colors = {"Red", "Magenta", "Green", "Blue", "Black"};
        ArrayList<String> unusedPlayerColors = new ArrayList<>(List.of(colors));
        for (int i = 0; i < players.size(); i++) {
            String color = getPlayerColor(i, ResourceBundle.getBundle("message"));
            unusedPlayerColors.remove(color);
        }
        return unusedPlayerColors;
    }

    private void placeFirstTroops(Territory territory) {
        for (Player player : players) {
            if (player.ownsTerritory(territory)) {
                String placeFirstTroopsMessage = messages.getString("placeFirstTroopsMessage");
                throw new IllegalStateException(placeFirstTroopsMessage);

            }
        }
        territory.addAdditionalTroops(1);

        players.get(turnIndex).addTerritory(territory);

        players.get(turnIndex).removeDeployableTroops(1);
        turnIndex = (turnIndex + 1) % players.size();
    }


    public boolean checkIfTerritoryEmpty(Territory territoryToFind) {
        if (territoryToFind == null) {
            String findTerritoryErrorMessage = messages.getString("findTerritoryErrorMessage");
            throw new IllegalArgumentException(findTerritoryErrorMessage);
        }

        for (Player player : players) {
            if (player.ownsTerritory(territoryToFind)) {
                return false;
            }
        }
        return true;
    }

    public boolean allTerritoriesAssigned() {
        int territoriesOccupied = 0;
        for (Player player : players) {
            territoriesOccupied += player.territoryCount();
        }

        if (territoriesOccupied > 42) {
            String toManyTerritories = messages.getString("toManyTerritories");
            throw new IllegalStateException(toManyTerritories);
        }

        if (territoriesOccupied == 42) {
            return true;
        }
        return false;
    }

    public boolean placedAllInitialTroops() {
        for (Player player : players) {
            int troopCount = player.getDeployableTroops();
            if (troopCount > 0) {
                return false;
            }
        }
        return true;
    }


    public void allocatePlayerTroops(Player player) {
        int numTerritories = player.territoryCount();
        if (numTerritories == 0) {
            String zeroTerritories = messages.getString("zeroTerritories");
            throw new IllegalStateException(zeroTerritories);
        }
        if (numTerritories == 42) {
            String allTerritories = messages.getString("allTerritories");
            throw new IllegalStateException(allTerritories);
        }

        int continentBonus = calculateContinentBonus(player);

        if (numTerritories < 12) {
            player.addDeployableTroops(3 + continentBonus);
        } else {
            player.addDeployableTroops((numTerritories / 3) + continentBonus);
        }
    }

    public int calculateContinentBonus(Player player) {
        ArrayList<Continent> continents = calculateContinentsPlayerOwns(player);
        return calculateBonus(continents);
    }


    public int calculateBonus(ArrayList<Continent> continents) {
        if (continents == null) {
            String continentNullErrorMessage = messages.getString("continentNullErrorMessage");
            throw new NullPointerException(continentNullErrorMessage);
        }
        int total = 0;
        for (Continent continent : continents) {
            total += continent.value();
        }
        return total;
    }


    public ArrayList<Continent> calculateContinentsPlayerOwns(Player player) {
        ArrayList<Continent> result = new ArrayList<>();
        for (Continent continent : territoriesByContinentMap.keySet()) {
            boolean fullyOccupied = playerOwnsContinent(continent, player);
            if (fullyOccupied) {
                result.add(continent);
            }
        }
        return result;
    }

    public boolean playerOwnsContinent(Continent continent, Player player) {
        if (player == null || continent == null) {
            throw new IllegalArgumentException("The player and continent cannot be null.");
        }
        Territory[] continentTerritories = territoriesByContinentMap.get(continent);
        boolean fullyOccupied = true;
        for (Territory territory : continentTerritories) {
            if (!player.ownsTerritory(territory)) {
                fullyOccupied = false;
            }
        }
        return fullyOccupied;
    }

    public Territory convertTerritoryNameToObject(String name) {
        String nameInvalid = messages.getString("nameInvalid");
        if (name == null) {
            String nameNull = messages.getString("nameNull");
            throw new NullPointerException(nameNull);
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException(nameInvalid);
        }
        for (Territory territory : territories) {
            if (territory.getTerritoryName().equals(name)) {
                return territory;
            }
        }

        throw new IllegalArgumentException(nameInvalid);
    }

    public String getCurrentPlayerColor() {
        return getPlayerColor(turnIndex, messages);
    }

    private String getPlayerColor(int playerIndex, ResourceBundle messageBundle) {
        Color color = players.get(playerIndex).getColor();
        String playerColorErrorMessage = messageBundle.getString("playerColorErrorMessage");
        String option1 = messageBundle.getString("colorOption1");
        String option2 = messageBundle.getString("colorOption2");
        String option3 = messageBundle.getString("colorOption3");
        String option4 = messageBundle.getString("colorOption4");
        String option5 = messageBundle.getString("colorOption5");

        switch (color.toString()) {
            case ("java.awt.Color[r=169,g=27,b=13]"):
                return option1;
            case ("java.awt.Color[r=0,g=49,b=102]"):
                return option3;
            case ("java.awt.Color[r=102,g=0,b=102]"):
                return option2;
            case ("java.awt.Color[r=30,g=70,b=32]"):
                return option4;
            case ("java.awt.Color[r=0,g=0,b=0]"):
                return option5;
            default:
                throw new IllegalStateException(playerColorErrorMessage);
        }
    }


    public ArrayList<Integer> attack(int attackCount, int defendCount) {
        ArrayList<Integer> attackResult = recentAttack.attackerWins(attackCount, defendCount);
        if (attackResult.get(2) == 1) {
            canDrawCard = true;
        } else if (attackResult.get(2) == 2) {
            canDrawCard = true;
            removePlayer();
            checkForWin();
        }
        if (secretMissionMode) {
            checkForSecretMissionWin();
        }
        return attackResult;
    }

    private void checkForWin() {
        if (players.size() == 1) {
//            System.out.println("one player left win");
            gameState = GameState.WIN;
        }
    }

    protected void checkForSecretMissionWin() {
        Card currentPlayerCard = secretMissionCardsByPlayer.get(turnIndex);
        if (currentPlayerCard.type().equals("Conquer")) {
            checkConquerMission(currentPlayerCard);
        } else if (currentPlayerCard.type().equals("Control")) {
            checkControlMission(currentPlayerCard);
        }
        for (int i = 0; i < players.size(); i++) {
            Card card = secretMissionCardsByPlayer.get(i);
            if (card.type().equals("Destroy")) {
                checkDestroyMission(card, i);
            }
        }
    }

    private void removePlayer() {
        Player currentPlayer = players.get(turnIndex);
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).territoryCount() == 0) {
                transferCards(currentPlayer, players.get(i));
                players.remove(players.get(i));
                if (secretMissionMode) {
                    secretMissionCardsByPlayer.remove(i);
                }
                break;
            }
        }
    }

    private void transferCards(Player currentPlayer, Player elimPlayer) {
        ArrayList<Card> cardsToAdd = elimPlayer.getCards();
        for (Card card : cardsToAdd) {
            currentPlayer.addCard(card);
        }
    }


    public int turnCardsIn(ArrayList<Card> cardsToTurnIn) {
        Player currentPlayer = players.get(turnIndex);
        for (Card card : cardsToTurnIn) {
            if (!card.value().equals("Wildcard")) {
                Territory territoryToCheck = convertTerritoryNameToObject(card.value());
                if (currentPlayer.ownsTerritory(territoryToCheck)) {
                    territoryToCheck.addAdditionalTroops(2);
                }
            }
        }

        int amountWon = riskCards.turnInCards(cardsToTurnIn);
        currentPlayer.addDeployableTroops(amountWon);
        currentPlayer.removeCards(cardsToTurnIn);

        return currentPlayer.getDeployableTroops();

    }

    public ArrayList<ArrayList<Card>> allSetsOfValidCards() {
        Player currentPlayer = players.get(turnIndex);
        ArrayList<Card> cardsToCheck = currentPlayer.getCards();
        ArrayList<ArrayList<Card>> listToReturn = new ArrayList<>();
        int cardSize = cardsToCheck.size();
        for (int i = 0; i < cardSize - 2; i++) {
            for (int j = i + 1; j < cardSize - 1; j++) {
                for (int k = j + 1; k < cardSize; k++) {
                    ArrayList<Card> setToConfirm = new ArrayList<>();
                    setToConfirm.add(cardsToCheck.get(i));
                    setToConfirm.add(cardsToCheck.get(j));
                    setToConfirm.add(cardsToCheck.get(k));
                    if (riskCards.canTurnInCards(setToConfirm)) {
                        listToReturn.add(setToConfirm);
                    }
                }
            }
        }
        return listToReturn;
    }

    public void maneuverTroops(AtomicInteger maneuverableTroopAmount) {
        maneuver.maneuverTroopAmount(maneuverableTroopAmount.get());
//        System.out.println("Secret mission mode " + secretMissionMode);
        if (secretMissionMode) {
            checkForSecretMissionWin();
        }
        if (gameState != GameState.WIN) {
            transitionFromManeuverToDeploy();
        }
    }

    public void transitionFromManeuverToDeploy() {
        territoryInPlay = null;
        if (canDrawCard) {
            drawCardForPlayer();
        }
        turnIndex = (turnIndex + 1) % players.size();
        gameState = GameState.DEPLOY;
        attackState = AttackPhase.ATTACKFROM;
        maneuverState = ManeuverPhase.MANEUVERFROM;
        allocatePlayerTroops(players.get(turnIndex));
    }

    private void drawCardForPlayer() {
        Card cardToAdd = riskCards.drawOneCard();
        Player currentPlayer = players.get(turnIndex);
        currentPlayer.addCard(cardToAdd);
        canDrawCard = false;
    }


    public boolean owns24Territories(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("The player cannot be null.");
        }
        return player.territoryCount() >= 24;
    }

    public void checkDestroyMission(Card secretMissionCard, int playerIndex) {
        validateSecretMissionCard(secretMissionCard, "Destroy");
        if (getPlayerColor(playerIndex, ResourceBundle.getBundle("message")).equals(secretMissionCard.value())) {
            if (players.get(playerIndex).territoryCount() >= 24) {
//                System.out.println("could not destroy but has territories win, player" + (turnIndex + 1));

                gameState = GameState.WIN;
            }
        } else {
            boolean found = false;
            String cardColor = secretMissionCard.value();
            for (int j = 0; j < players.size(); j++) {
                if (this.getPlayerColor(j, ResourceBundle.getBundle("message")).equals(cardColor)) {
                    found = true;
                }
            }
            if (!found) {
                turnIndex = playerIndex;
//                System.out.println("mission destroy set to win player " + (turnIndex + 1) + " destroyed " + cardColor);
                gameState = GameState.WIN;
            }
        }

    }

    private void validateSecretMissionCard(Card secretMissionCard, String type) {
        if (secretMissionCard == null) {
            throw new NullPointerException("The card cannot be null.");
        }
        if (!secretMissionCard.type().equals(type)) {
            throw new IllegalArgumentException("The card is not of the correct type.");
        }
    }

    public void checkConquerMission(Card secretMissionCard) {
        validateSecretMissionCard(secretMissionCard, "Conquer");
        String continentsToParse = secretMissionCard.value();
        String[] parseResult = continentsToParse.split("-");
        boolean ownsAll = true;
        for (String continentName : parseResult) {
            Continent continent = nameToContinent(continentName);
            ownsAll = ownsAll && playerOwnsContinent(continent, players.get(turnIndex));
        }
        if (ownsAll) {
//            System.out.println("Player has conquered both continents " + (turnIndex + 1));
            gameState = GameState.WIN;
        }

    }

    public void checkControlMission(Card secretMissionCard) {
        validateSecretMissionCard(secretMissionCard, "Control");
        if (secretMissionCard.value().equals("18TerritoriesWith2Troops")) {
            int totalSatisfied = 0;
            for (Territory territory : territories) {
                Player currentPlayer = players.get(turnIndex);
                if (currentPlayer.ownsTerritory(territory) && territory.getCurrentNumberOfTroops() >= 2) {
                    totalSatisfied++;
                }
            }
            if (totalSatisfied >= 18) {
//                System.out.println("control small terri amount win player " + (turnIndex + 1));
                gameState = GameState.WIN;
            }
        } else {
            if (players.get(turnIndex).territoryCount() >= 24) {
//                System.out.println("control big terry win player " + (turnIndex + 1));
                gameState = GameState.WIN;
            }
        }
    }

    private Continent nameToContinent(String continentName) {
        if (continentName.equals("AFRICA")) {
            return Continent.AFRICA;
        } else if (continentName.equals(Continent.ASIA.toString())) {
            return Continent.ASIA;
        } else if (continentName.equals(Continent.AUSTRALIA.toString())) {
            return Continent.AUSTRALIA;
        }
        if (continentName.equals(Continent.NORTH_AMERICA.toString())) {
            return Continent.NORTH_AMERICA;
        } else {
            return Continent.SOUTH_AMERICA;
        }

    }


    public GameState getGameState() {
        return this.gameState;
    }

    @SuppressFBWarnings
    public Territory getCurrentTerritory() {
        return territoryInPlay;
    }

    public AttackPhase getAttackState() {
        return attackState;
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public Territory getAttackingTerritory() {
        return recentAttack.getAttackingTerritory();
    }

    public Territory getDefendingTerritory() {
        return recentAttack.getDefendingTerritory();
    }

    public void updateGameState(GameState newGameState, AttackPhase newAttackState) {
        this.gameState = newGameState;
        this.attackState = newAttackState;
    }

    public void forceTurnInSetup() {
        territoryInPlay = null;
        gameState = GameState.DEPLOY;
    }

    public int getManeuverableTroops() {
        return maneuver.getManeuverableTroops();
    }

    public ManeuverPhase getManeuverState() {
        return maneuverState;
    }

    public int attackMax() {
        return recentAttack.attackMax();
    }

    public int defendMax() {
        return recentAttack.defendMax();
    }

    public String getManeuverFromName() {
        return maneuver.getManeuverFromName();
    }

    public String getManeuverToName() {
        return maneuver.getManeuverToName();
    }

    public void setTurnIndex(int index) {
        turnIndex = index;
    }

    //Below methods are for unit testing and integration testing
    @SuppressFBWarnings
    public void setRiskDeck(RiskDeck newRiskCards) {
        this.riskCards = newRiskCards;
    }

    public void setAttackState(AttackPhase newAttackPhase) {
        this.attackState = newAttackPhase;
    }

    //Below methods are for unit testing
    public boolean getDrawCard() {
        return canDrawCard;
    }


    // this test is for integration testing
    public void assignSecretMissionCards(ArrayList<Card> cardsInput) {
        secretMissionCardsByPlayer = new ArrayList<>(cardsInput);
    }

    //Background Graphics helper
    public ArrayList<Card> getMissionCards() {
        return new ArrayList<>(secretMissionCardsByPlayer);
    }

    //Background Graphics helper
    public boolean canTurnInCards() {
        ArrayList<Card> currentPlayerCards = players.get(turnIndex).getCards();
        return riskCards.canTurnInCards(currentPlayerCards) && gameState == GameState.DEPLOY;
    }

    public int getDeployableTroops() {
        Player currentPlayer = players.get(turnIndex);
        return currentPlayer.getDeployableTroops();
    }

    // Background Graphics helper
    public ArrayList<String> getCurrentPlayerCards() {
        ArrayList<String> cardFilePaths = new ArrayList<>();
        if (secretMissionMode && !gameState.equals(GameState.SETUP)) {
            Card currentPlayerSecretMissionCard = secretMissionCardsByPlayer.get(turnIndex);
            cardFilePaths.add(currentPlayerSecretMissionCard.filePath());
        }
        ArrayList<Card> currentPlayerCards = players.get(turnIndex).getCards();
        for (Card c : currentPlayerCards) {
            cardFilePaths.add(c.filePath());
        }
        return cardFilePaths;
    }

    @SuppressFBWarnings
    public void setResourceBundle(ResourceBundle messagesInput) {
        messages = messagesInput;
    }

    // Background Graphics helper
    public DisplayTerritoryData[] getBoardState() {
        int territoriesOccupied = 0;
        for (Player player : players) {
            territoriesOccupied += player.territoryCount();
        }

        DisplayTerritoryData[] boardState = new DisplayTerritoryData[territoriesOccupied];
        int k = 0;

        for (Territory territory : territories) {
            if (!checkIfTerritoryEmpty(territory)) {
                Color territoryColor = findOwnerColor(territory);
                boardState[k] = new DisplayTerritoryData("" + territory.getCurrentNumberOfTroops(),
                        territory.getTerritoryName(),
                        territoryColor);
                k++;
            }
        }
        return boardState;
    }

    // Background Graphics helper
    private Color findOwnerColor(Territory territory) {
        for (Player player : players) {
            if (player.ownsTerritory(territory)) {
                return player.getColor();
            }
        }
        return null;
    }

    // Background Graphics helper
    public int convertTerritoryNameToCard(String[] territoryNames) {
        ArrayList<Card> cardList = new ArrayList<>();
        Player currentPlayer = players.get(turnIndex);

        for (String territoryName : territoryNames) {
            for (Card card : currentPlayer.getCards()) {
                if (card.value().equals(territoryName)) {
                    cardList.add(card);
                    break;
                }
            }
        }

        return turnCardsIn(cardList);
    }

    public boolean canAttack() {
        for (Territory territory : territories) {
            if (recentAttack.validateAttackingTerritory(territory, players.get(turnIndex))) {
                return true;
            }
        }
        return false;
    }
}
