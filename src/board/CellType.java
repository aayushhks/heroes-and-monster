package board;

/**
 * Enum defining the specific types of terrain found on the game board.
 * Encapsulates the visual symbol for each type.
 */
public enum CellType {
    COMMON("   "),       // Open space for battles
    MARKET(" M "),       // Shop location
    INACCESSIBLE(" X "); // Blocked path

    private final String symbol;

    CellType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}