package board;

/**
 * Enum defining the specific types of terrain found on the game board.
 * Encapsulates the visual symbol and ANSI color codes for each type.
 */
public enum CellType {
    // ANSI Color Codes
    // Format: \u001B[xxm where xx is the color ID
    // 0m=Reset, 31m=Red, 32m=Green, 33m=Yellow, 34m=Blue

    COMMON("   ", "\u001B[0m"),       // Default (White/Reset)
    MARKET(" M ", "\u001B[33m"),      // Yellow text for Commerce
    INACCESSIBLE(" X ", "\u001B[31m"); // Red text for Danger/Blocked

    private final String symbol;
    private final String colorCode;
    private static final String RESET = "\u001B[0m";

    CellType(String symbol, String colorCode) {
        this.symbol = symbol;
        this.colorCode = colorCode;
    }

    public String getSymbol() {
        // Return: Color + Symbol + Reset
        return colorCode + symbol + RESET;
    }
}