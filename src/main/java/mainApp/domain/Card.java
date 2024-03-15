package mainApp.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Card {
    private final String type;
    private final String value;
    private final String filePath;

    public Card(String typeInput, String valueInput, String filePathInput) {
        this.type = typeInput;
        this.value = valueInput;
        this.filePath = filePathInput;
    }

    public String type() {
        return type;
    }

    public String value() {
        return value;
    }

    public String filePath() {
        return filePath;
    }

    @SuppressFBWarnings
    @Override
    public boolean equals(Object o) {
        Card card = (Card) o;
        boolean typeMatch = type.equals(card.type());
        boolean valueMatch = value.equals(card.value());
        boolean filePathMatch = filePath.equals(card.filePath());
        return typeMatch && valueMatch && filePathMatch;
    }
}
