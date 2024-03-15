package mainApp.domain.IntegrationTests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mainApp.domain.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;

public class F15SecretMissionWin {

    private ArrayList<Player> players;
    private ArrayList<Territory> territories;
    private Game game;
    private TreeMap<String, Point> pointsByTerritoryName;
    private Initializer initializer;
    private Dice dice;

    int toAddAttack;
    int toAddDefend;
    String[] colors;
    String cardString;
    private int territoryCount;
    private int troopsToAssign;
    private boolean winByManeuver;

    @Test
    public void secretMissionWin_PlayerBlack_DestroyBlack_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(1, 1, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(1, 1, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 2, 2));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 9;
        toAddDefend = 2;
        colors = new String[]{"Black", "Blue", "Green"};
        cardString = "Black";
        troopsToAssign = 1;

        destroyHelperPlayerHasOwnCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerRed_DestroyRed_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(1, 1, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(1, 1, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 2, 2));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 9;
        toAddDefend = 2;
        colors = new String[]{"Red", "Blue", "Green"};
        cardString = "Red";
        troopsToAssign = 1;

        destroyHelperPlayerHasOwnCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerRed_DestroyBlack_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(1, 1, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(1, 1, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 1, 2));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(1, 1, 0));

        ArrayList<Integer> attackRoll3 = new ArrayList<>(of(3, 3, 6));
        ArrayList<Integer> defendRolls3 = new ArrayList<>(of(1, 2));
        ArrayList<Integer> expectedLosses3 = new ArrayList<>(of(0, 2, 2));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2, attackRoll3));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2, defendRolls3));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2, expectedLosses3));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false, true));
        toAddAttack = 9;
        toAddDefend = 3;
        colors = new String[]{"Red", "Black", "Green"};
        cardString = "Black";
        troopsToAssign = 1;

        destroyHelperPlayerHasDifferentCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerBlue_DestroyRed_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(1, 1, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(1, 1));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(1, 1, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(2, 3, 4));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 2));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRoll3 = new ArrayList<>(of(3, 3, 6));
        ArrayList<Integer> defendRolls3 = new ArrayList<>(of(1, 2));
        ArrayList<Integer> expectedLosses3 = new ArrayList<>(of(0, 2, 2));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2, attackRoll3));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2, defendRolls3));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2, expectedLosses3));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false, true));

        toAddAttack = 6;
        toAddDefend = 4;
        colors = new String[]{"Blue", "Red", "Magenta"};
        cardString = "Red";
        troopsToAssign = 1;


        destroyHelperPlayerHasDifferentCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerBlue_DestroyMagenta_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(1, 2, 3));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(3, 6));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(2, 0, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(2, 3, 4));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRoll3 = new ArrayList<>(of(4, 4, 4));
        ArrayList<Integer> defendRolls3 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses3 = new ArrayList<>(of(0, 2, 2));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2, attackRoll3));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2, defendRolls3));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2, expectedLosses3));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false, true));

        toAddAttack = 7;
        toAddDefend = 3;
        colors = new String[]{"Blue", "Magenta", "Green"};
        cardString = "Magenta";
        troopsToAssign = 1;


        destroyHelperPlayerHasDifferentCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_DestroyBlue_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 3, 4));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 2, 3));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3, 6));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(2, 0, 0));

        ArrayList<Integer> attackRoll3 = new ArrayList<>(of(4, 4, 4));
        ArrayList<Integer> defendRolls3 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses3 = new ArrayList<>(of(0, 2, 2));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2, attackRoll3));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2, defendRolls3));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2, expectedLosses3));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false, true));

        toAddAttack = 9;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Blue", "Green"};
        cardString = "Blue";
        troopsToAssign = 1;

        destroyHelperPlayerHasDifferentCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_DestroyGreen_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(4, 4, 4));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 2, 3));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3, 6));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(2, 0, 0));

        ArrayList<Integer> attackRoll3 = new ArrayList<>(of(2, 3, 4));
        ArrayList<Integer> defendRolls3 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses3 = new ArrayList<>(of(0, 2, 2));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2, attackRoll3));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2, defendRolls3));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2, expectedLosses3));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "Green";
        troopsToAssign = 1;

        destroyHelperPlayerHasDifferentCard(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control18_Start17Territories2Troops_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(6, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(1, 4, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3, 6));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(1, 1, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 1;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "18TerritoriesWith2Troops";
        territoryCount = 17;
        troopsToAssign = 2;

        controlHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control18_Start17Territories2Troops_ExpectNoWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(6, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 1, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false));

        toAddAttack = 15;
        toAddDefend = 1;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "18TerritoriesWith2Troops";
        territoryCount = 17;
        troopsToAssign = 2;

        controlHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control18_Start17Territories2Troops_WinWithManeuver_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(6, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 1, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false));

        toAddAttack = 15;
        toAddDefend = 1;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "18TerritoriesWith2Troops";
        territoryCount = 17;
        troopsToAssign = 2;
        winByManeuver = true;
        controlHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control18_Start17Territories1Troops_Conquer18_ExpectNoWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 1, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, false));

        toAddAttack = 15;
        toAddDefend = 2;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "18TerritoriesWith2Troops";
        territoryCount = 17;
        troopsToAssign = 1;
        winByManeuver = false;
        controlHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control18_WinDuringDeploy_ExpectWin() throws IOException {
        colors = new String[]{"Magenta", "Green", "Blue"};
        initializeGameState();
        Card secretMissionCard = findCard("18TerritoriesWith2Troops", "Control");
        ArrayList<Card> cards = new ArrayList<>(of(secretMissionCard,
                findCard("Blue", "Destroy"), findCard("ASIA-SOUTH_AMERICA", "Conquer")));
        game.assignSecretMissionCards(cards);
        int count = 0;
        int playerIndex = 0;
        for (Territory territory : territories) {
            if (count < 17) {
                territory.addAdditionalTroops(2);
                assertEquals(2, territory.getCurrentNumberOfTroops());
                players.get(0).addTerritory(territory);
                count++;
            } else {
                territory.addAdditionalTroops(1);
                assertEquals(1, territory.getCurrentNumberOfTroops());
                players.get(playerIndex).addTerritory(territory);
            }
            playerIndex = (playerIndex + 1) % players.size();
        }
        int totalWith2 = 0;
        Territory toDeploy = null;
        for (Territory territory : territories) {
            if (players.get(0).ownsTerritory(territory) && territory.getCurrentNumberOfTroops() == 2) {
                totalWith2++;
            } else if (players.get(0).ownsTerritory(territory) && territory.getCurrentNumberOfTroops() == 1) {
                toDeploy = territory;
            }
        }
        assertEquals(17, totalWith2);
        assert toDeploy != null;
        game.updateDeployableTroops(1, toDeploy);
        assertEquals(GameState.WIN, game.getGameState());
    }

    @Test
    public void secretMissionWin_PlayerMagenta_Control24_Start23Territories1Troops_Conquer24_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "24Territories";
        territoryCount = 23;
        troopsToAssign = 1;
        winByManeuver = false;
        controlHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_ConquerAsiaAndAfrica_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<Integer>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "ASIA-AFRICA";
        territoryCount = -1;
        troopsToAssign = 1;
        winByManeuver = false;
        conquerHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_ConquerAsiaAndSouthAmerica_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<Integer>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "ASIA-SOUTH_AMERICA";
        territoryCount = -1;
        troopsToAssign = 1;
        winByManeuver = false;
        conquerHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_ConquerAfricaAndNorthAmerica_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<Integer>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "NORTH_AMERICA-AFRICA";
        territoryCount = -1;
        troopsToAssign = 1;
        winByManeuver = false;
        conquerHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    @Test
    public void secretMissionWin_PlayerMagenta_ConquerAustraliaAndNorthAmerica_ExpectWin() throws IOException {
        ArrayList<Integer> attackRolls1 = new ArrayList<>(of(2, 6, 6));
        ArrayList<Integer> defendRolls1 = new ArrayList<>(of(2, 3));
        ArrayList<Integer> expectedLosses1 = new ArrayList<Integer>(of(0, 2, 0));

        ArrayList<Integer> attackRolls2 = new ArrayList<Integer>(of(2, 5));
        ArrayList<Integer> defendRolls2 = new ArrayList<>(of(1, 3));
        ArrayList<Integer> expectedLosses2 = new ArrayList<>(of(0, 2, 1));

        ArrayList<ArrayList<Integer>> attackAllRolls = new ArrayList<>(of(attackRolls1, attackRolls2));
        ArrayList<ArrayList<Integer>> defendAllRolls = new ArrayList<>(of(defendRolls1, defendRolls2));
        ArrayList<ArrayList<Integer>> allExpectedLosses = new ArrayList<>(of(expectedLosses1, expectedLosses2));
        ArrayList<Boolean> defenderLooses = new ArrayList<>(of(false, true));
        ArrayList<Boolean> gameStateIsWIN = new ArrayList<>(of(false, true));

        toAddAttack = 15;
        toAddDefend = 3;
        colors = new String[]{"Magenta", "Green", "Blue"};
        cardString = "NORTH_AMERICA-AUSTRALIA";
        territoryCount = -1;
        troopsToAssign = 1;
        winByManeuver = false;
        conquerHelper(attackAllRolls, defendAllRolls, allExpectedLosses, defenderLooses, gameStateIsWIN);
    }

    private void conquerHelper(ArrayList<ArrayList<Integer>> attackAllRolls, ArrayList<ArrayList<Integer>> defendAllRolls,
                               ArrayList<ArrayList<Integer>> allExpectedLosses, ArrayList<Boolean> defenderLooses,
                               ArrayList<Boolean> gameStateIsWIN) throws IOException {
        initializeGameState();
        Player attacker = players.get(0);
        Player defender = players.get(1);

        Card secretMissionCard = findCard(cardString, "Conquer");
        ArrayList<Card> cards = new ArrayList<>(of(secretMissionCard,
                findCard("Blue", "Destroy"), findCard("24Territories", "Control")));

        String[] continentNames = cardString.split("-");
        Territory defendingTerritory = makeStartUpMapForConquer(convertNameToContinent(continentNames[0]), convertNameToContinent(continentNames[1]));
        Territory attackingTerritory = findValidAttackingTerritory(defendingTerritory, defender);
        correctTroops(attackingTerritory, defendingTerritory);

        assertEquals(toAddAttack + troopsToAssign, attackingTerritory.getCurrentNumberOfTroops());
        assertEquals(toAddDefend + troopsToAssign, defendingTerritory.getCurrentNumberOfTroops());

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender,
                toAddAttack + troopsToAssign, toAddDefend + troopsToAssign);
        game.assignSecretMissionCards(cards);

        validateNoDeployableTroops();
        validateTroopCountOfNonAttackingDefending(attackingTerritory, defendingTerritory, troopsToAssign);

        int numAttacks = attackAllRolls.size();
        expectDiceRolls(numAttacks, attackAllRolls, defendAllRolls);
        EasyMock.replay(dice);
        for (int i = 0; i < numAttacks; i++) {
            validateOneAttack(defenderLooses.get(i), new ArrayList<>(of(attacker, defender)), defendingTerritory, gameStateIsWIN.get(i),
                    attackAllRolls.get(i), defendAllRolls.get(i), allExpectedLosses.get(i));
        }
        EasyMock.verify(dice);
    }

    private void correctTroops(Territory attackingTerritory, Territory defendingTerritory) {
        for (Player player : players) {
            player.removeDeployableTroops(player.getDeployableTroops());
            assertEquals(0, player.getDeployableTroops());
        }

        int originalCount = attackingTerritory.getCurrentNumberOfTroops();
        attackingTerritory.addAdditionalTroops(toAddAttack);
        defendingTerritory.addAdditionalTroops(toAddDefend);
        assertEquals(toAddAttack + originalCount, attackingTerritory.getCurrentNumberOfTroops());
    }

    private Territory findValidAttackingTerritory(Territory defendingTerritory, Player defender) {
        for (Territory territory : defendingTerritory.getAdjacentTerritories()) {
            if (!defender.ownsTerritory(territory)) {
                return territory;
            }
        }
        throw new IllegalArgumentException("No valid attacking territory");
    }

    private Continent convertNameToContinent(String continentName) {
        switch (continentName) {
            case "ASIA":
                return Continent.ASIA;
            case "NORTH_AMERICA":
                return Continent.NORTH_AMERICA;
            case "AFRICA":
                return Continent.AFRICA;
            case "SOUTH_AMERICA":
                return Continent.SOUTH_AMERICA;
            case "AUSTRALIA":
                return Continent.AUSTRALIA;
            default:
                throw new IllegalArgumentException("Cannot convert continent name: " + continentName);
        }
    }

    private void controlHelper(ArrayList<ArrayList<Integer>> attackAllRolls, ArrayList<ArrayList<Integer>> defendAllRolls,
                               ArrayList<ArrayList<Integer>> allExpectedLosses, ArrayList<Boolean> defenderLooses,
                               ArrayList<Boolean> gameStateIsWIN) throws IOException {
        initializeGameState();
        Player attacker = players.get(0);
        Player defender = players.get(1);

        Card secretMissionCard = findCard(cardString, "Control");
        ArrayList<Card> cards = new ArrayList<>(of(secretMissionCard,
                findCard("Blue", "Destroy"), findCard("ASIA-SOUTH_AMERICA", "Conquer")));

        makeStartUpMapForControl();
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingGivenAttackerAndDefender(attacker, defender);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender,
                toAddAttack + troopsToAssign, toAddDefend + troopsToAssign);
        assertEquals(territoryCount, attacker.territoryCount());
        game.assignSecretMissionCards(cards);

        validateNoDeployableTroops();
        validateTroopCountOfNonAttackingDefending(attackingTerritory, defendingTerritory, troopsToAssign);

        int numAttacks = attackAllRolls.size();
        expectDiceRolls(numAttacks, attackAllRolls, defendAllRolls);
        EasyMock.replay(dice);
        for (int i = 0; i < numAttacks; i++) {
            validateOneAttack(defenderLooses.get(i), new ArrayList<>(of(attacker, defender)), defendingTerritory, gameStateIsWIN.get(i),
                    attackAllRolls.get(i), defendAllRolls.get(i), allExpectedLosses.get(i));
        }
        if (winByManeuver) {
            game.setGameState(GameState.MANEUVER);
            game.clickedOnPoint(pointsByTerritoryName.get(attackingTerritory.getTerritoryName()));
            game.clickedOnPoint(pointsByTerritoryName.get(defendingTerritory.getTerritoryName()));
            game.maneuverTroops(new AtomicInteger(1));
            assertEquals(GameState.WIN, game.getGameState());
        }
        EasyMock.verify(dice);
    }


    private void destroyHelperPlayerHasDifferentCard(ArrayList<ArrayList<Integer>> attackAllRolls,
                                                     ArrayList<ArrayList<Integer>> defendAllRolls, ArrayList<ArrayList<Integer>> allExpectedLosses,
                                                     ArrayList<Boolean> defenderLooses, ArrayList<Boolean> gameStateIsWIN) throws IOException {
        initializeGameState();
        Player attacker = players.get(0);
        Player defender = players.get(1);

        Card secretMissionCard = findCard(cardString, "Destroy");
        ArrayList<Card> cards = new ArrayList<>(of(secretMissionCard,
                findCard("24Territories", "Control"), findCard("ASIA-SOUTH_AMERICA", "Conquer")));

        makeStartUpMapForDestroy(false);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingGivenAttackerAndDefender(attacker, defender);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];

        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender, toAddAttack + 1, toAddDefend + 1);
        assertEquals(1, defender.territoryCount());
        game.assignSecretMissionCards(cards);

        validateNoDeployableTroops();
        validateTroopCountOfNonAttackingDefending(attackingTerritory, defendingTerritory, 1);

        int numAttacks = attackAllRolls.size();
        expectDiceRolls(numAttacks, attackAllRolls, defendAllRolls);
        EasyMock.replay(dice);
        for (int i = 0; i < numAttacks; i++) {
            validateOneAttack(defenderLooses.get(i), new ArrayList<>(of(attacker, defender)), defendingTerritory, gameStateIsWIN.get(i),
                    attackAllRolls.get(i), defendAllRolls.get(i), allExpectedLosses.get(i));
        }
        EasyMock.verify(dice);
    }


    private void destroyHelperPlayerHasOwnCard(ArrayList<ArrayList<Integer>> attackAllRolls,
                                               ArrayList<ArrayList<Integer>> defendAllRolls, ArrayList<ArrayList<Integer>> allExpectedLosses,
                                               ArrayList<Boolean> defenderLooses, ArrayList<Boolean> gameStateIsWIN) throws IOException {
        initializeGameState();
        Player attacker = players.get(0);

        Card secretMissionCard = findCard(cardString, "Destroy");
        ArrayList<Card> cards = new ArrayList<>(of(secretMissionCard,
                findCard("Blue", "Destroy"), findCard("ASIA-SOUTH_AMERICA", "Conquer")));

        makeStartUpMapForDestroy(true);
        Territory[] attackAndDefendingTerritories = initializeAttackingTerritoryAndDefendingTerritory(attacker);
        Territory attackingTerritory = attackAndDefendingTerritories[0];
        Territory defendingTerritory = attackAndDefendingTerritories[1];

        Player defender = findOwner(defendingTerritory);
        assert defender != null;
        validateValidGameSetup(attackingTerritory, defendingTerritory, attacker, defender, toAddAttack + 1, toAddDefend + 1);
        assertEquals(23, attacker.territoryCount());
        game.assignSecretMissionCards(cards);

        validateNoDeployableTroops();
        validateTroopCountOfNonAttackingDefending(attackingTerritory, defendingTerritory, 1);

        int numAttacks = attackAllRolls.size();
        expectDiceRolls(numAttacks, attackAllRolls, defendAllRolls);
        EasyMock.replay(dice);
        System.out.println("Before attacks " + defender.ownsTerritory(defendingTerritory));
        for (int i = 0; i < numAttacks; i++) {
            System.out.println("This is attack " + i);
            validateOneAttack(defenderLooses.get(i), new ArrayList<>(of(attacker, defender)), defendingTerritory, gameStateIsWIN.get(i),
                    attackAllRolls.get(i), defendAllRolls.get(i), allExpectedLosses.get(i));
        }
        EasyMock.verify(dice);
    }

    private void validateOneAttack(boolean defenderLoses, ArrayList<Player> playersInput, Territory defendingTerritory, boolean winGame,
                                   ArrayList<Integer> attackRolls, ArrayList<Integer> defendRolls, ArrayList<Integer> expectedLoss) {
        Player attacker = playersInput.get(0);
        Player defender = playersInput.get(1);
        ArrayList<Integer> losses = game.attack(attackRolls.size(), defendRolls.size());
        assertEquals(expectedLoss.get(0), losses.get(0)); // attacker loss
        assertEquals(expectedLoss.get(1), losses.get(1)); // defender loss
        assertEquals(expectedLoss.get(2), losses.get(2));  // defender loses outright
        assertEquals(!defenderLoses, defender.ownsTerritory(defendingTerritory));
        assertEquals(defenderLoses, attacker.ownsTerritory(defendingTerritory));
        if (winGame) {
            assertEquals(GameState.WIN, game.getGameState());
        } else {
            assertNotEquals(GameState.WIN, game.getGameState());
        }
    }

    private void expectDiceRolls(int numAttacks, ArrayList<ArrayList<Integer>> attackAllRolls, ArrayList<ArrayList<Integer>> defendAllRolls) {
        for (int i = 0; i < numAttacks; i++) {
            EasyMock.expect(dice.rollDice(attackAllRolls.get(i).size())).andReturn(attackAllRolls.get(i));
            EasyMock.expect(dice.rollDice(defendAllRolls.get(i).size())).andReturn(defendAllRolls.get(i));
        }
    }

    private void validateTroopCountOfNonAttackingDefending(Territory ATerritory, Territory DTerritory, int count) {
        for (Territory territory : territories) {
            if (!ATerritory.equals(territory) && !DTerritory.equals(territory)) {
                assertEquals(count, territory.getCurrentNumberOfTroops());
            }
        }
    }

    private void validateNoDeployableTroops() {
        for (Player player : players) {
            assertEquals(0, player.getDeployableTroops());
        }

    }

    private void validateValidGameSetup(Territory attackingTerritory, Territory defendingTerritory,
                                        Player attacker, Player defender, int totalAttackTroopCount, int totalDefendTroopCount) {
        assertTrue(attackingTerritory.getAdjacentTerritories().contains(defendingTerritory));
        assertFalse(attacker.ownsTerritory(defendingTerritory));

        assertTrue(defender.ownsTerritory(defendingTerritory));
        assertTrue(attacker.ownsTerritory(attackingTerritory));

        assertEquals(totalAttackTroopCount, attackingTerritory.getCurrentNumberOfTroops());
        assertEquals(totalDefendTroopCount, defendingTerritory.getCurrentNumberOfTroops());

        assertNull(game.getAttackingTerritory());
        assertNull(game.getDefendingTerritory());

        validateAttackingTerritory(attackingTerritory);
        validateDefendingTerritory(defendingTerritory);
    }

    private void validateAttackingTerritory(Territory attackingTerritory) {
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(AttackPhase.ATTACKFROM, game.getAttackState());
        String nameAttacking = attackingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameAttacking));
        assertEquals(attackingTerritory, game.getAttackingTerritory());
    }

    private void validateDefendingTerritory(Territory defendingTerritory) {
        assertEquals(GameState.ATTACK, game.getGameState());
        assertEquals(AttackPhase.DEFENDWITH, game.getAttackState());
        String nameDefending = defendingTerritory.getTerritoryName();
        game.clickedOnPoint(pointsByTerritoryName.get(nameDefending));
        assertEquals(defendingTerritory, game.getDefendingTerritory());
        assertEquals(AttackPhase.CHOOSETROOPS, game.getAttackState());
    }

    private Player findOwner(Territory territory) {
        for (Player player : players) {
            if (player.ownsTerritory(territory)) {
                return player;
            }
        }
        return null;
    }

    private Territory[] initializeAttackingTerritoryAndDefendingTerritory(Player attacker) {

        Territory[] result = new Territory[2];
        Territory attackingTerritory = null;
        Territory defendingTerritory = null;
        assertNotEquals(null, attacker);

        for (Player player : players) {
            player.removeDeployableTroops(player.getDeployableTroops());
            assertEquals(0, player.getDeployableTroops());
        }

        for (Territory territory : territories) {
            boolean owns = attacker.ownsTerritory(territory);
            if (owns) {
                attackingTerritory = territory;
                break;
            }

        }
        assert attackingTerritory != null;

        for (Territory territory : territories) {
            boolean isNeighbor = attackingTerritory.getAdjacentTerritories().contains(territory);
            boolean owns = attacker.ownsTerritory(territory);
            if (!owns && isNeighbor) {
                defendingTerritory = territory;
                break;
            }
        }
        assert defendingTerritory != null;
        attackingTerritory.addAdditionalTroops(toAddAttack);
        defendingTerritory.addAdditionalTroops(toAddDefend);
        assertEquals(toAddAttack + 1, attackingTerritory.getCurrentNumberOfTroops());

        result[0] = attackingTerritory;
        result[1] = defendingTerritory;
        return result;
    }

    private Territory[] initializeAttackingTerritoryAndDefendingGivenAttackerAndDefender(
            Player attacker, Player defender) {

        Territory[] result = new Territory[2];
        Territory attackingTerritory = null;
        Territory defendingTerritory = null;
        assertNotEquals(null, defender);
        assertNotEquals(null, attacker);

        for (Player player : players) {
            player.removeDeployableTroops(player.getDeployableTroops());
            assertEquals(0, player.getDeployableTroops());
        }
        ArrayList<Territory> territoriesChecked = new ArrayList<>();
        while (attackingTerritory == null) {
            for (Territory territory : territories) {
                boolean owns = defender.ownsTerritory(territory);
                if (owns && !territoriesChecked.contains(territory)) {
                    defendingTerritory = territory;
                    territoriesChecked.add(defendingTerritory);
                    break;
                }

            }
            assert defendingTerritory != null;

            for (Territory territory : territories) {
                boolean isNeighbor = defendingTerritory.getAdjacentTerritories().contains(territory);
                boolean owns = attacker.ownsTerritory(territory);
                if (owns && isNeighbor) {
                    attackingTerritory = territory;
                    break;
                }
            }

        }
        int originalCount = attackingTerritory.getCurrentNumberOfTroops();
        attackingTerritory.addAdditionalTroops(toAddAttack);
        defendingTerritory.addAdditionalTroops(toAddDefend);
        assertEquals(toAddAttack + originalCount, attackingTerritory.getCurrentNumberOfTroops());

        result[0] = attackingTerritory;
        result[1] = defendingTerritory;
        return result;
    }


    private void initializeGameState() throws IOException {
        initializePoints();
        ArrayList<String> playerColors = new ArrayList<>();
        Collections.addAll(playerColors, colors);
        Dimension standardScreenSize = new Dimension(1536, 864);
        initializer = new Initializer(standardScreenSize);
        players = initializer.makePlayers(colors.length, playerColors);
        initializer.createAllEntities();
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        game = makeGame(players);
        game.setGameState(GameState.ATTACK);
    }

    private void initializePoints() throws IOException {
        String territoryPointsFilePath = "src/main/java/data/territoryPoints.txt";
        pointsByTerritoryName = new TreeMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(territoryPointsFilePath), Charset.defaultCharset()));
            String territory;
            while ((territory = br.readLine()) != null) {
                String[] territoryData = territory.split(", ");
                String territoryName = territoryData[0];
                int territoryXPos = Integer.parseInt(territoryData[1]);
                int territoryYPos = Integer.parseInt(territoryData[2]);
                pointsByTerritoryName.put(territoryName, new Point(territoryXPos, territoryYPos));
            }
            br.close();
        } catch (IOException e) {
            throw new IOException("Cannot find this file." + territoryPointsFilePath);
        }
    }

    public Game makeGame(ArrayList<Player> playersInput) {
        RiskDeck allRiskCards = initializer.getRiskCards();
        SecretMissionDeck allSecretMissionCards = initializer.getSecretMissionCards();
        dice = EasyMock.mock(Dice.class);
        Attack attack = new Attack((Dice) dice);
        Maneuver maneuver = new Maneuver(ResourceBundle.getBundle("message"));
        TreeMap<String, Territory> territoriesByTerritoryName = new TreeMap<>();
        for (Territory t : initializer.getTerritories()) {
            territoriesByTerritoryName.put(t.getTerritoryName(), t);
        }
        territories = new ArrayList<>(territoriesByTerritoryName.values());
        return new Game(playersInput, territories, initializer.getTerritoriesByContinentMap(),
                allRiskCards, attack, maneuver, allSecretMissionCards);
    }

    private Card findCard(String targetValue, String targetType) {
        if (initializer.getSecretMissionCardsArray() == null || initializer.getSecretMissionCardsArray().size() == 0) {
            throw new IllegalStateException("There are no mission cards");
        }
        for (Card card : initializer.getSecretMissionCardsArray()) {
            if (card.type().equals(targetType) && card.value().equals(targetValue)) {
                return card;
            }
        }
        throw new IllegalArgumentException("Could Not Find the Card." + targetType + " " + targetValue);
    }

    private void makeStartUpMapForDestroy(boolean playerOwnsTheirColor) {
        if (playerOwnsTheirColor) {
            setUpMapNeedXTerritories(23, players.get(0));
        } else {
            setUpMapNeedXTerritories(1, players.get(1));
        }
        for (Player player : players) {
            System.out.println(player.getColor() + "  " + player.territoryCount());
        }
    }

    private void makeStartUpMapForControl() {
        setUpMapNeedXTerritories(territoryCount, players.get(0));
    }

    @SuppressFBWarnings
    private Territory makeStartUpMapForConquer(Continent continentFull, Continent continentMinus1) {
        ArrayList<Territory> listOfTerritories = new ArrayList<>(territories);
        Player currentPlayer = players.get(0);
        Territory toReturn = null;
        HashMap<Continent, Territory[]> territoriesByContinentMap = initializer.getTerritoriesByContinentMap();
        for (Continent c : territoriesByContinentMap.keySet()) {
            if (c.equals(continentFull)) {
                Territory[] allTerritories = territoriesByContinentMap.get(c);
                for (int i = 0; i < allTerritories.length; i++) {
                    currentPlayer.addTerritory(allTerritories[i]);
                    listOfTerritories.remove(allTerritories[i]);

                    allTerritories[i].addAdditionalTroops(1);
                    currentPlayer.removeDeployableTroops(1);
                }
            }

            if (c.equals(continentMinus1)) {
                Territory[] allTerritories = territoriesByContinentMap.get(c);
                toReturn = allTerritories[allTerritories.length - 1];
                for (int i = 0; i < allTerritories.length - 1; i++) {
                    currentPlayer.addTerritory(allTerritories[i]);
                    listOfTerritories.remove(allTerritories[i]);
                    allTerritories[i].addAdditionalTroops(1);
                    currentPlayer.removeDeployableTroops(1);
                }
            }
        }

        int i = 0;
        for (Territory territory : listOfTerritories) {
            if (players.get(i).getDeployableTroops() <= 0) {
                i = (i + 1) % players.size();
            }
            assert toReturn != null;
            if (toReturn.equals(territory)) {
                players.get(1).addTerritory(territory);
                players.get(1).removeDeployableTroops(1);

            } else {
                players.get(i).addTerritory(territory);
                players.get(i).removeDeployableTroops(1);
            }
            territory.addAdditionalTroops(1);
            i = (i + 1) % players.size();
        }
        return toReturn;
    }

    private void setUpMapNeedXTerritories(int territoryGoal, Player player) {
        for (int i = 0; i < territoryGoal; i++) {
            player.addTerritory(territories.get(i));
        }
        int playerIndex = 1;
        for (int i = territoryGoal; i < territories.size(); i++) {
            if (!players.get(playerIndex).equals(player)) {
                players.get(playerIndex).addTerritory(territories.get(i));
            }
            playerIndex = (playerIndex + 1) % players.size();
        }

        for (Territory territory : territories) {
            territory.addAdditionalTroops(troopsToAssign);
        }

        assertEquals(territoryGoal, player.territoryCount());
    }
}
