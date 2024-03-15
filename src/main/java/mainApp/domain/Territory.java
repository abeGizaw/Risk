package mainApp.domain;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Territory {
    private int troops;
    private final Set<Territory> adjacentTerritories;
    private final String territoryName;
    private final double xPos;
    private final double yPos;
    private final double hitboxWidth;
    private final double hitboxHeight;

    public Territory(String name, double xPosition, double yPosition, double width, double height) {
        this.adjacentTerritories = new HashSet<>();
        this.territoryName = name;
        this.xPos = xPosition;
        this.yPos = yPosition;
        this.hitboxWidth = width;
        this.hitboxHeight = height;
    }

    public int getCurrentNumberOfTroops() {
        return troops;
    }

    public void addAdditionalTroops(int additionalTroops) throws IllegalArgumentException, ArithmeticException{
        if (additionalTroops < 0) {
            throw new IllegalArgumentException("Can not add a negative number of troops to a territory.");
        } else if (additionalTroops > (Integer.MAX_VALUE - troops)) {
            throw new ArithmeticException("Adding troops will cause overflow.");
        }
        troops += additionalTroops;
    }

    public void removeFromCurrentTroops(int removableTroops) throws IllegalArgumentException, ArithmeticException {
        if (removableTroops < 0) {
            throw new IllegalArgumentException("Can not remove a non positive number of troops from a territory.");
        } else if (removableTroops > this.troops) {
            throw new ArithmeticException("Removing troops will result in a negative number of troops.");
        }
        this.troops -= removableTroops;
    }

    public boolean isAdjacentTerritory(Territory territory) throws NullPointerException {
        if (territory == null) {
            throw new NullPointerException("Territory can not be null for isAdjacentTerritory.");
        }
        return this.adjacentTerritories.contains(territory);
    }

    public void setAdjacentTerritories(Territory[] adjacentTerritoriesSet) {
        this.adjacentTerritories.addAll(Arrays.asList(adjacentTerritoriesSet));
    }

    public boolean clickedOnTerritory(Point pointClicked) {
        double marginForError = .0001;
        double pointXPosition = pointClicked.getX();
        double pointYPosition = pointClicked.getY();
        if (pointXPosition < 0 || pointYPosition < 0) {
            throw new IllegalArgumentException("Coordinates of the point must be non negative.");
        }
        boolean verticalAlignment = (yPos - marginForError) <= pointYPosition && pointYPosition < (yPos + hitboxHeight + marginForError);
        boolean horizontalAlignment = (xPos - marginForError) <= pointXPosition && pointXPosition < (xPos + hitboxWidth + marginForError);
        return verticalAlignment && horizontalAlignment;
    }

    public String getTerritoryName() {
        return this.territoryName;
    }


    public int maxDeployableAttackTroops() {
        if (this.troops == 0){
            throw new IllegalStateException("Territory does not have any troops");
        }
        if (this.troops >= 5){
            return 3;
        }

        return this.troops - 1;
    }

    public int maxDeployableDefendTroops() {
        if (this.troops == 0){
            throw new IllegalStateException("Territory does not have any troops");
        }
        return this.troops == 1 ? 1 : 2;
    }
    public Set<Territory> getAdjacentTerritories() {
        return new HashSet<Territory>(adjacentTerritories);
    }

}
