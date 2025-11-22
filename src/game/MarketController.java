package game;

import common.InputValidator;
import common.RandomGenerator;
import entities.Hero;
import entities.Party;
import items.*;
import items.Spell.SpellType;
import utils.GameDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing Market interactions.
 * Handles the logic for buying and selling items between Heroes and the Shop.
 */
public class MarketController {

    private final List<Item> globalItemCatalog;

    public MarketController() {
        this.globalItemCatalog = new ArrayList<>();
        initializeCatalog();
    }

    /**
     * Loads all possible items into a master catalog.
     * In a larger app, this might be injected rather than loaded here.
     */
    private void initializeCatalog() {
        globalItemCatalog.addAll(GameDataLoader.loadWeapons("Weaponry.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadArmor("Armory.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadPotions("Potions.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("FireSpells.txt", SpellType.FIRE));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("IceSpells.txt", SpellType.ICE));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("LightningSpells.txt", SpellType.LIGHTNING));

        if (globalItemCatalog.isEmpty()) {
            System.err.println("Warning: Market initialized with no items. Check data files.");
        }
    }

    /**
     * Starts the market interaction loop.
     * Generates a random subset of items for this specific market visit.
     */
    public void enterMarket(Scanner scanner, Party party) {
        System.out.println("You enter a bustling marketplace...");

        // Generate a unique inventory for this market session (e.g., 5-10 random items)
        List<Item> marketInventory = generateMarketInventory();

        boolean inMarket = true;
        while (inMarket) {
            System.out.println("\n--- Market Menu ---");
            System.out.println("1. Buy Items");
            System.out.println("2. Sell Items");
            System.out.println("3. Exit Market");

            int choice = InputValidator.getValidInt(scanner, "Choose action: ", 1, 3);

            switch (choice) {
                case 1: buyLoop(scanner, party, marketInventory); break;
                case 2: sellLoop(scanner, party); break;
                case 3: inMarket = false; break;
            }
        }
        System.out.println("You leave the market.");
    }

    private List<Item> generateMarketInventory() {
        List<Item> inventory = new ArrayList<>();
        RandomGenerator rng = RandomGenerator.getInstance();

        // Randomly select items from the global catalog
        // Let's say a market has ~10 items
        int stockSize = 10;
        if (globalItemCatalog.isEmpty()) return inventory;

        for (int i = 0; i < stockSize; i++) {
            int index = rng.nextInt(globalItemCatalog.size());
            inventory.add(globalItemCatalog.get(index));
        }
        return inventory;
    }

    private void buyLoop(Scanner scanner, Party party, List<Item> marketInventory) {
        Hero shopper = selectHero(scanner, party, "Who is buying?");
        if (shopper == null) return;

        while (true) {
            System.out.println("\n--- Items for Sale (Shopper: " + shopper.getName() + " | Gold: " + shopper.getMoney() + ") ---");
            printItemList(marketInventory);
            System.out.println((marketInventory.size() + 1) + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to buy: ", 1, marketInventory.size() + 1);
            if (choice == marketInventory.size() + 1) break;

            Item item = marketInventory.get(choice - 1);
            processPurchase(shopper, item);
        }
    }

    private void processPurchase(Hero hero, Item item) {
        // Rule: Hero cannot buy item if level is too low
        if (hero.getLevel() < item.getMinLevel()) {
            System.out.println("Cannot buy! Required Level: " + item.getMinLevel());
            return;
        }

        // Rule: Hero cannot buy if insufficient gold
        if (hero.getMoney() < item.getPrice()) {
            System.out.println("Insufficient Gold! Cost: " + item.getPrice());
            return;
        }

        // Transaction
        hero.deductMoney(item.getPrice());
        hero.getInventory().addItem(item);
        System.out.println("Purchase successful! " + item.getName() + " added to inventory.");
    }

    // ==========================================
    //               SELLING LOGIC
    // ==========================================

    private void sellLoop(Scanner scanner, Party party) {
        Hero seller = selectHero(scanner, party, "Who is selling?");
        if (seller == null) return;

        while (true) {
            List<Item> sellableItems = seller.getInventory().getItems();
            if (sellableItems.isEmpty()) {
                System.out.println(seller.getName() + " has nothing to sell.");
                break;
            }

            System.out.println("\n--- Your Inventory (Seller: " + seller.getName() + ") ---");
            // Show items with their resale value (50% of price)
            for (int i = 0; i < sellableItems.size(); i++) {
                Item item = sellableItems.get(i);
                double resaleValue = item.getPrice() * 0.5;
                System.out.printf("%d. %s (Sell for: %.0f)\n", (i + 1), item.getName(), resaleValue);
            }
            System.out.println((sellableItems.size() + 1) + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to sell: ", 1, sellableItems.size() + 1);
            if (choice == sellableItems.size() + 1) break;

            Item itemToSell = sellableItems.get(choice - 1);
            processSale(seller, itemToSell);
        }
    }

    private void processSale(Hero hero, Item item) {
        double resaleValue = item.getPrice() * 0.5;

        hero.getInventory().removeItem(item);
        hero.addMoney(resaleValue);

        System.out.println("Sold " + item.getName() + " for " + resaleValue + " gold.");

        // Optional: If you want sold items to appear in the market, add to marketInventory here.
        // For this implementation, the market just absorbs it.
    }

    // ==========================================
    //                HELPERS
    // ==========================================

    private Hero selectHero(Scanner scanner, Party party, String prompt) {
        System.out.println(prompt);
        for (int i = 0; i < party.getSize(); i++) {
            System.out.println((i + 1) + ". " + party.getHero(i).getName());
        }
        System.out.println((party.getSize() + 1) + ". Cancel");

        int choice = InputValidator.getValidInt(scanner, "Select Hero: ", 1, party.getSize() + 1);
        if (choice == party.getSize() + 1) return null;

        return party.getHero(choice - 1);
    }

    private void printItemList(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
    }
}