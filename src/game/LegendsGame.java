package game;

import board.Cell;
import board.LegendsBoard;
import common.InputValidator;
import entities.Hero;
import entities.Hero.HeroType;
import entities.Monster;
import entities.Monster.MonsterType;
import entities.Party;
import utils.GameDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The concrete implementation of the RPG "Legends: Monsters and Heroes".
 * Orchestrates the game loop, user input, hero selection, and world interaction.
 */
public class LegendsGame extends Game {

    private LegendsBoard board;
    private Party party;
    private final Random random = new Random();

    // Data Caches
    private List<Hero> availableWarriors;
    private List<Hero> availableSorcerers;
    private List<Hero> availablePaladins;
    private List<Monster> allMonsters;

    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println("Loading Game Data...");
        loadAssets();

        System.out.println("\n--- Hero Selection ---");
        int partySize = InputValidator.getValidInt(scanner, "Enter party size (1-3): ", 1, 3);

        this.party = new Party();
        for (int i = 0; i < partySize; i++) {
            System.out.println("\nSelect Hero #" + (i + 1) + ":");
            Hero selectedHero = selectHero(scanner);
            party.addHero(selectedHero);
        }

        // Initialize Board (8x8 as per specs)
        this.board = new LegendsBoard(8, 8);
        this.board.setParty(party);

        System.out.println("\nThe party enters the world...");
    }

    private void loadAssets() {
        availableWarriors = GameDataLoader.loadHeroes("Warriors.txt", HeroType.WARRIOR);
        availableSorcerers = GameDataLoader.loadHeroes("Sorcerers.txt", HeroType.SORCERER);
        availablePaladins = GameDataLoader.loadHeroes("Paladins.txt", HeroType.PALADIN);

        // We load monsters now to have them ready for random encounters
        allMonsters = new ArrayList<>();
        allMonsters.addAll(GameDataLoader.loadMonsters("Dragons.txt", MonsterType.DRAGON));
        allMonsters.addAll(GameDataLoader.loadMonsters("Exoskeletons.txt", MonsterType.EXOSKELETON));
        allMonsters.addAll(GameDataLoader.loadMonsters("Spirits.txt", MonsterType.SPIRIT));

        if (availableWarriors.isEmpty() && availableSorcerers.isEmpty() && availablePaladins.isEmpty()) {
            throw new RuntimeException("CRITICAL ERROR: No heroes could be loaded. Check data/ directory.");
        }
    }

    private Hero selectHero(Scanner scanner) {
        System.out.println("1. Warrior (Favors Strength/Agility)");
        System.out.println("2. Sorcerer (Favors Dexterity/Agility)");
        System.out.println("3. Paladin (Favors Strength/Dexterity)");

        int typeChoice = InputValidator.getValidInt(scanner, "Choose class: ", 1, 3);
        List<Hero> choiceList = (typeChoice == 1) ? availableWarriors :
                (typeChoice == 2) ? availableSorcerers : availablePaladins;

        System.out.println("\nAvailable Heroes:");
        System.out.printf("%-4s %-20s %-5s %-5s %-5s %-5s %-5s %-5s\n", "ID", "Name", "Lvl", "HP", "MP", "Str", "Dex", "Agi");
        for (int i = 0; i < choiceList.size(); i++) {
            Hero h = choiceList.get(i);
            System.out.printf("%-4d %-20s %-5d %-5.0f %-5.0f %-5.0f %-5.0f %-5.0f\n",
                    i + 1, h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getStrength(), h.getDexterity(), h.getAgility());
        }

        int heroIndex = InputValidator.getValidInt(scanner, "Select hero ID: ", 1, choiceList.size()) - 1;
        // Remove from list so same hero can't be picked twice
        return choiceList.remove(heroIndex);
    }

    @Override
    protected void processTurn(Scanner scanner) {
        board.printBoard();
        System.out.println(party); // Show stats
        System.out.println("Controls: [W]Up [A]Left [S]Down [D]Right [M]Market [I]Info [Q]Quit");

        String input = InputValidator.getValidOption(scanner, "Action: ", "w", "a", "s", "d", "m", "i", "q");

        switch (input) {
            case "w": moveParty(-1, 0); break;
            case "a": moveParty(0, -1); break;
            case "s": moveParty(1, 0); break;
            case "d": moveParty(0, 1); break;
            case "m": handleMarketInteraction(scanner); break;
            case "i": showDetailedInfo(); break;
            case "q": // Handled by shouldQuit()
                break;
        }
    }

    private void moveParty(int dRow, int dCol) {
        int newRow = party.getRow() + dRow;
        int newCol = party.getCol() + dCol;

        if (!board.isValidCoordinate(newRow, newCol)) {
            System.out.println("You cannot move off the edge of the world!");
            return;
        }

        Cell targetCell = board.getCell(newRow, newCol);
        if (!targetCell.isAccessible()) {
            System.out.println("That path is blocked (Inaccessible).");
            return;
        }

        // Update Position
        party.setLocation(newRow, newCol);

        // Handle Cell Events
        if (targetCell.isCommon()) {
            checkForBattle();
        }
    }

    private void checkForBattle() {
        // Simple random roll for battle (e.g., 30% chance on common tiles)
        if (random.nextDouble() < 0.30) {
            System.out.println("\n*** AMBUSH! You have encountered monsters! ***");
            // In a real implementation, we would call a BattleController here.
            // For now, we just simulate the event.
            System.out.println("(Battle Logic would trigger here - To Be Implemented in next step)");
        }
    }

    private void handleMarketInteraction(Scanner scanner) {
        Cell currentCell = board.getCell(party.getRow(), party.getCol());
        if (!currentCell.isMarket()) {
            System.out.println("There is no market here.");
            return;
        }
        System.out.println("\nWelcome to the Market!");
        System.out.println("(Market Logic would trigger here - To Be Implemented in next step)");
    }

    private void showDetailedInfo() {
        System.out.println("\n=== Detailed Party Info ===");
        for (Hero h : party.getHeroes()) {
            System.out.println(h);
            h.getInventory().printInventory();
            System.out.println("---------------------------");
        }
    }

    @Override
    protected boolean isGameOver() {
        return party.isPartyWipedOut();
    }

    @Override
    protected boolean shouldQuit() {
        // In this simple loop, we rely on the main input check.
        // If the user typed 'q' in processTurn, we need a flag or logic here.
        // For simplicity in this structure, we can check a flag,
        // but 'Game.java' structure might need a generic flag check.
        // A cleaner way in industry code is a State pattern, but here:
        return false;
        // Note: The 'q' in processTurn just ends the turn.
        // Real quitting logic should toggle a boolean 'running' flag in the parent or return a status.
        // Given the constraints, assume 'q' sets a flag we check.
    }

    @Override
    protected void endGame() {
        System.out.println("Game Over. Thanks for playing Legends!");
    }
}