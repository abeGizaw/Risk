package mainApp.domain;

public enum Continent {
    ASIA(7, 12),
    NORTH_AMERICA(5, 9),
    SOUTH_AMERICA(2, 4),
    EUROPE(5, 7),
    AFRICA(3, 6),
    AUSTRALIA(2, 4);

    private final int value;
    private final int territoryCount;

    Continent(int valueInput, int territoryCountInput) {
        this.value = valueInput;
        this.territoryCount = territoryCountInput;
    }

    public int value() {
        return value;
    }

    public int territoryCount(){
        return territoryCount;
    }
}
