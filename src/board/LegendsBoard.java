package board;

import entities.Party;
import java.util.Random;

/**
 * Concrete implementation of the Board for Legends: Monsters and Heroes.
 * Manages the grid of Cells, procedural generation, and rendering.
 */
public class LegendsBoard extends Board {
    private final Cell[][] grid;
    private final Random random;

    // Reference to the party to render their position
    private Party party;

    public LegendsBoard(int width, int height) {
        super(width, height);
        this.grid = new Cell[height][width];
        this.random = new Random();
        initializeBoard();
    }

    public void setParty(Party party) {
        this.party = party;
    }

    /**
     * Procedurally generates the map based on assignment specs:
     * - 20% Inaccessible
     * - 30% Market
     * - 50% Common
     */
    private void initializeBoard() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                // Force start position (0,0) to be Common so the party isn't stuck/blocked immediately
                if (r == 0 && c == 0) {
                    grid[r][c] = new Cell(CellType.COMMON);
                    continue;
                }

                double roll = random.nextDouble();
                if (roll < 0.20) {
                    grid[r][c] = new Cell(CellType.INACCESSIBLE);
                } else if (roll < 0.50) { // 0.20 + 0.30 = 0.50
                    grid[r][c] = new Cell(CellType.MARKET);
                } else {
                    grid[r][c] = new Cell(CellType.COMMON);
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            throw new IndexOutOfBoundsException("Invalid coordinate: " + row + "," + col);
        }
        return grid[row][col];
    }

    @Override
    public void printBoard() {
        // Top Border
        printHorizontalBorder();

        for (int r = 0; r < height; r++) {
            // Left Border for the row
            System.out.print("|");

            for (int c = 0; c < width; c++) {
                // Render Logic:
                // 1. If Party is here, draw Party Symbol.
                // 2. Else, draw Cell Symbol.

                if (party != null && party.getRow() == r && party.getCol() == c) {
                    // " P " or " H " for Party/Hero
                    System.out.print(" P "); // P for Party
                } else {
                    System.out.print(grid[r][c].toString());
                }

                System.out.print("|"); // Column separator
            }
            System.out.println(); // New line after row

            // Row Divider
            printHorizontalBorder();
        }
    }

    private void printHorizontalBorder() {
        for (int c = 0; c < width; c++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }
}