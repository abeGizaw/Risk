package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;


public class Initializer {

    private final HashMap<String, Territory> allTerritories = new HashMap<String, Territory>();
    private final HashMap<String, Territory[]> territoryNeighborMap = new HashMap<String, Territory[]>();
    private final HashMap<Continent, Territory[]> territoriesByContinentMap = new HashMap<Continent, Territory[]>();
    private final ArrayList<Card> riskCards = new ArrayList<>();
    private final ArrayList<Card> missionCards = new ArrayList<>();

    private final Dimension screenSize;

    private final Random diceRollRandom;
    private ResourceBundle messages;

    public Initializer(Dimension screenDimension) {
        messages = ResourceBundle.getBundle("message");

        diceRollRandom = new Random();
        screenSize = new Dimension(screenDimension.width, screenDimension.height);
    }

    //https://www.javatpoint.com/how-to-read-csv-file-in-java
    private void createAllTerritories() throws IOException {
        String allTerritoriesFilePath = "src/main/java/data/territoryHitbox.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(allTerritoriesFilePath), Charset.defaultCharset()));
            String territory;
            while ((territory = br.readLine()) != null) {
                String[] territoryData = territory.split(",");
                String name = territoryData[0];
                double xPos = Double.parseDouble(territoryData[1]) * screenSize.width;
                double yPos = Double.parseDouble(territoryData[2]) * screenSize.height * .86;
                double hitboxWidth = Double.parseDouble(territoryData[3]) * screenSize.width;
                double hitboxHeight = Double.parseDouble(territoryData[4]) * screenSize.height * .86;
                allTerritories.put(name, new Territory(name, xPos, yPos, hitboxWidth, hitboxHeight));
            }
            br.close();
        } catch (IOException e) {
            throw new IOException("Cannot find this file." + allTerritoriesFilePath);
        }
    }

    private void createTerritoryNeighbors() {
        String line = "";
        try {
            String territoryNeighborFilePath = "src/main/java/data/territoryMapping.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryNeighborFilePath), Charset.defaultCharset()));
            while ((line = br.readLine()) != null) {
                String[] territoryNeighbors = line.split(",");
                Territory[] neighbors = new Territory[territoryNeighbors.length - 1];

                for (int i = 1; i < territoryNeighbors.length; i++) {
                    String neighborName = territoryNeighbors[i];
                    Territory currentTerritory = allTerritories.get(neighborName);
                    neighbors[i - 1] = currentTerritory;
                }
                territoryNeighborMap.put(territoryNeighbors[0], neighbors);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAllAdjacentTerritoryFields(){
        for (Map.Entry<String, Territory[]> territoryEntry: territoryNeighborMap.entrySet()){
            Territory currentTerritory = allTerritories.get(territoryEntry.getKey());
            currentTerritory.setAdjacentTerritories(territoryEntry.getValue());
        }
    }

    public void createAllEntities() throws IOException {
        createAllTerritories();
        createTerritoryNeighbors();
        createContinentByTerritoryMap();
        setAllAdjacentTerritoryFields();
        createMissionCards();
        createRiskCards();
    }

    private void createRiskCards() {
        String line = "";
        try {
            String riskCardsFilePath = "src/main/java/data/RiskCards/riskCards.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(riskCardsFilePath), Charset.defaultCharset()));
            while ((line = br.readLine()) != null){
                String[] riskCardInfo = line.split(",");
                String filePath = riskCardInfo[0];
                String type = riskCardInfo[2];
                String value = riskCardInfo[1];
                riskCards.add(new Card(type, value, filePath));
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMissionCards() {
        String line = "";
        try {
            String missionCardFilePath = "src/main/java/data/SecretMissionCards/secretMissionCards.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(missionCardFilePath), Charset.defaultCharset()));
            while ((line = br.readLine()) != null){
                String[] missionCardInfo = line.split(",");
                String filePath = missionCardInfo[0];
                String type = missionCardInfo[1];
                String value = missionCardInfo[2];
                missionCards.add(new Card(type, value, filePath));
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createContinentByTerritoryMap() {
        String line = "";
        try {
            String territoryByContentFilePath = "src/main/java/data/territoriesByContenint.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryByContentFilePath), Charset.defaultCharset()));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] territoryNames = line.split(",");
                Territory[] territories = new Territory[territoryNames.length - 1];
                for (int i = 1; i < territoryNames.length; i++) {
                    String territoryName = territoryNames[i];
                    Territory currentTerritory = allTerritories.get(territoryName);
                    territories[i - 1] = currentTerritory;
                }
                Continent continent = nameToContinent(territoryNames[0]);
                territoriesByContinentMap.put(continent, territories);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Continent nameToContinent(String continentName){
        if (continentName.equals("AFRICA")) {
            return Continent.AFRICA;
        } else if (continentName.equals(Continent.ASIA.toString())) {
            return Continent.ASIA;
        } else if (continentName.equals(Continent.AUSTRALIA.toString())){
            return Continent.AUSTRALIA;
        }if (continentName.equals(Continent.NORTH_AMERICA.toString())) {
            return Continent.NORTH_AMERICA;
        } else if (continentName.equals(Continent.SOUTH_AMERICA.toString())) {
            return Continent.SOUTH_AMERICA;
        } else if (continentName.equals("EUROPE")) {
            return Continent.EUROPE;
        } else {
            throw new IllegalArgumentException("Not a valid continent name: " + continentName);
        }
    }


    public Collection<Territory> getTerritories() {
        return allTerritories.values();
    }
    public ArrayList<Player> makePlayers(int amountOfPlayers, ArrayList<String> allPlayerColors) {
        ArrayList<Player> players = new ArrayList<>();
        int initialTroopCount = calculateInitialTroopCount(amountOfPlayers);

        for (int k = 0; k < amountOfPlayers; k++){
            Color playerColor = convertColor(allPlayerColors.get(k));
            Player playerToAdd = new Player(initialTroopCount, playerColor, new ArrayList<Card>());
            players.add(playerToAdd);
        }
        return players;

    }

    private Color convertColor(String colorToConvert) {
        String option1 = messages.getString("colorOption1");
        String option2 = messages.getString("colorOption2");
        String option3 = messages.getString("colorOption3");
        String option4 = messages.getString("colorOption4");
        String option5 = messages.getString("colorOption5");
        String playerColorErrorMessage = messages.getString("playerColorErrorMessage");

        if (colorToConvert.equals(option1)){
            return Color.decode("#a91b0d");
        } else if (colorToConvert.equals(option2)){
            return Color.decode("#660066");
        } else if (colorToConvert.equals(option3)){
            return Color.decode("#003166");
        } else if (colorToConvert.equals(option4)){
            return Color.decode("#1e4620");
        } else if (colorToConvert.equals(option5)) {
            return Color.decode("#000000");
        } else {
            throw new IllegalArgumentException(playerColorErrorMessage);
        }

    }

    private int calculateInitialTroopCount(int amountOfPlayers) {
        return 50 - (5 * amountOfPlayers);
    }



    public Game makeGame(ArrayList<Player> players, boolean secretMissionMode, ResourceBundle locale) {
        RiskDeck allRiskCards = new RiskDeck(riskCards);
        Dice dice =  new Dice(diceRollRandom);
        Attack attack = new Attack(dice);
        Maneuver maneuver = new Maneuver(locale);

        SecretMissionDeck secretMissionCards = null;
        if (secretMissionMode) {
            secretMissionCards = new SecretMissionDeck(missionCards);
        }
        Game game = new Game(players, this.getTerritories(), territoriesByContinentMap, allRiskCards, attack, maneuver, secretMissionCards);
        return game;
    }

    public RiskDeck getRiskCards() {
        return new RiskDeck(riskCards);
    }

    public SecretMissionDeck getSecretMissionCards(){
        return new SecretMissionDeck(missionCards);
    }

    public ArrayList<Card> getSecretMissionCardsArray() {
        return new ArrayList<>(missionCards);
    }

    public HashMap<Continent, Territory[]> getTerritoriesByContinentMap(){
        return new HashMap<>(territoriesByContinentMap);
    }

    @SuppressFBWarnings
    public void setMessages(ResourceBundle messagesInput) {
        messages = messagesInput;
    }
}
