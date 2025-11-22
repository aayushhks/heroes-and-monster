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
 * Integrates Board, Market, and Battle systems.
 */
public class LegendsGame extends Game {

    private LegendsBoard board;
    private Party party;
    private final Random random = new Random();
    private boolean quitGame = false;

    // Controllers
    private MarketController marketController;
    private BattleController battleController;

    // Data Caches
    private List<Hero> availableWarriors;
    private List<Hero> availableSorcerers;
    private List<Hero> availablePaladins;
    private List<Monster> allMonsters;

    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println("Loading Game Data...");
        loadAssets();

        // Initialize Sub-Controllers
        this.marketController = new MarketController();
        this.battleController = new BattleController(allMonsters);

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
        // NOTE: We do NOT print the board here anymore.
        // The first call to processTurn() will handle the initial render.
    }

    private void loadAssets() {
        availableWarriors = GameDataLoader.loadHeroes("Warriors.txt", HeroType.WARRIOR);
        availableSorcerers = GameDataLoader.loadHeroes("Sorcerers.txt", HeroType.SORCERER);
        availablePaladins = GameDataLoader.loadHeroes("Paladins.txt", HeroType.PALADIN);

        // Load all monsters into a central catalog for the BattleController to use
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
        return choiceList.remove(heroIndex);
    }

    @Override
    protected void processTurn(Scanner scanner) {
        // 1. RENDER: Always show the board state at the VERY START of the turn
        board.printBoard();

        // 2. UI: Stats and Controls
        System.out.println(party);
        System.out.println("Controls: [W]Up [A]Left [S]Down [D]Right [M]Market [I]Info [Q]Quit");

        // 3. INPUT: Get user action
        String input = InputValidator.getValidOption(scanner, "Action: ", "w", "a", "s", "d", "m", "i", "q");

        // 4. UPDATE: Process logic
        switch (input) {
            case "w": moveParty(scanner, -1, 0); break;
            case "a": moveParty(scanner, 0, -1); break;
            case "s": moveParty(scanner, 1, 0); break;
            case "d": moveParty(scanner, 0, 1); break;
            case "m": handleMarketInteraction(scanner); break;
            case "i": showDetailedInfo(); break;
            case "q": quitGame = true; break;
        }
    }

    private void moveParty(Scanner scanner, int dRow, int dCol) {
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
            checkForBattle(scanner);
        }
    }

    private void checkForBattle(Scanner scanner) {
        // Spec: "Every time the heroes visit a space, we 'roll a dice'"
        if (random.nextDouble() < 0.50) {
            System.out.println("\n*** AMBUSH! You have encountered monsters! ***");
            battleController.startBattle(scanner, party);

            // After battle ends, the loop repeats, and the board is printed at the top of processTurn
        }
    }

    private void handleMarketInteraction(Scanner scanner) {
        Cell currentCell = board.getCell(party.getRow(), party.getCol());
        if (!currentCell.isMarket()) {
            System.out.println("There is no market here.");
            return;
        }
        // Delegate to Market Controller
        marketController.enterMarket(scanner, party);
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
        return quitGame;
    }

    @Override
    protected void endGame() {
        System.out.println("\nGame Over. Thanks for playing Legends: Monsters and Heroes!");
        if (party != null) {
            System.out.println("Final Status:");
            System.out.println(party);
        }
    }
}