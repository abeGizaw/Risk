package mainApp.domain;

import java.awt.*;

public class DisplayTerritoryData {
    public String troopCount;
    public String territoryName;
    public Color playerColor;

    public DisplayTerritoryData(String troops, String name, Color color) {
        troopCount = troops;
        territoryName = name;
        playerColor = color;
    }
}
