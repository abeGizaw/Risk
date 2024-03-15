package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.*;

public class Maneuver {
    private final ResourceBundle message;
    private Player maneuveringPlayer;
    private Territory maneuveringFrom;
    private Territory maneuveringTo;

    public Maneuver(ResourceBundle locale) {
        message = ResourceBundle.getBundle(locale.getBaseBundleName());
    }

    //Need to store player for future validation which is an externally mutable object.
    @SuppressFBWarnings
    public boolean validateManeuverFrom(Player player, Territory maneuveringFromTerritory) {
        if (player == null) {
            String errorMessage = message.getString("playerNullErrorMessage");
            throw new NullPointerException(errorMessage);
        } else if (maneuveringFromTerritory == null) {
            String errorMessage = message.getString("territoryNullErrorMessage");
            throw new NullPointerException(errorMessage);
        } else if (!player.ownsTerritory(maneuveringFromTerritory)) {
            String errorMessage = message.getString("playerDoesNotOwnTerritoryErrorMessage");
            throw new IllegalArgumentException(errorMessage);
        }
        if (maneuveringFromTerritory.getCurrentNumberOfTroops() == 1) {
            return false;
        }
        Set<Territory> adjacentTerritories = maneuveringFromTerritory.getAdjacentTerritories();
        for (Territory adjacentTerritory: adjacentTerritories) {
            if (player.ownsTerritory(adjacentTerritory)) {
                maneuveringPlayer = player;
                maneuveringFrom = maneuveringFromTerritory;
                return true;
            }
        }
        return false;
    }

    @SuppressFBWarnings
    public boolean validateManeuverTo(Territory maneuveringToTerritory) {
        if (maneuveringToTerritory == null) {
            String errorMessage = message.getString("territoryNullErrorMessage");
            throw new NullPointerException(errorMessage);
        } else if (maneuveringToTerritory.equals(maneuveringFrom)) {
            String errorMessage = message.getString("sameFromAndToTerritoryErrorMessage");
            throw new IllegalArgumentException(errorMessage);
        } else if (!maneuveringPlayer.ownsTerritory(maneuveringToTerritory)) {
            String errorMessage = message.getString("playerDoesNotOwnTerritoryErrorMessage");
            throw new IllegalArgumentException(errorMessage);
        }
        boolean validPath = CheckForConnectingPath(maneuveringToTerritory);
        if (validPath) {
            maneuveringTo = maneuveringToTerritory;
        }
        return validPath;
    }

    //Performs a breadth first search from the territoryFrom to the territoryTo,
    //returning true iff there is a path between the two of territories owned by the player.
    private boolean CheckForConnectingPath(Territory maneuveringToTerritory) {
        Set<Territory> visitedTerritories = new HashSet<>();
        Queue<Territory> territoryQueue = new LinkedList<>();
        visitedTerritories.add(maneuveringFrom);
        territoryQueue.add(maneuveringFrom);
        while (!territoryQueue.isEmpty()) {
            Territory currentTerritory = territoryQueue.poll();
            Set<Territory> adjacentTerritories = currentTerritory.getAdjacentTerritories();
            for (Territory adjacentTerritory: adjacentTerritories) {
                if (maneuveringPlayer.ownsTerritory(adjacentTerritory)) {
                    if (adjacentTerritory.equals(maneuveringToTerritory)) {
                        return true;
                    } else if (!visitedTerritories.contains(adjacentTerritory)) {
                        visitedTerritories.add(adjacentTerritory);
                        territoryQueue.add(adjacentTerritory);
                    }
                }
            }
        }
        return false;
    }

    public void maneuverTroopAmount(int troopAmount) {
        if (troopAmount < 1) {
            String errorMessage = message.getString("nonPositiveManeuverTroopAmountErrorMessage");
            throw new IllegalArgumentException(errorMessage);
        } else if (troopAmount >= maneuveringFrom.getCurrentNumberOfTroops()) {
            String errorMessage = message.getString("toManyTroopsManeuveringErrorMessage");
            throw new IllegalArgumentException(errorMessage);
        } else {
            maneuveringFrom.removeFromCurrentTroops(troopAmount);
            maneuveringTo.addAdditionalTroops(troopAmount);
        }
    }

    public int getManeuverableTroops() {
        return maneuveringFrom.getCurrentNumberOfTroops() - 1;
    }

    public String getManeuverFromName() {
        return maneuveringFrom.getTerritoryName();
    }

    public String getManeuverToName() {
        return maneuveringTo.getTerritoryName();
    }

    //The following are protected setter methods for unit testing
    protected void setManeuveringFrom(Territory territory) {
        maneuveringFrom = territory;
    }

    protected void setManeuveringTo(Territory territory) {
        maneuveringTo = territory;
    }
}
