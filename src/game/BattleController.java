package game;

import common.InputValidator;
import common.RandomGenerator;
import entities.Hero;
import entities.Monster;
import entities.Party;
import items.*;
import items.Spell.SpellType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing turn-based combat.
 * Handles monster spawning, turn order, damage calculation, and victory conditions.
 */
public class BattleController {

    private final List<Monster> monsterCatalog;
    private final RandomGenerator rng;

    public BattleController(List<Monster> monsterCatalog) {
        this.monsterCatalog = monsterCatalog;
        this.rng = RandomGenerator.getInstance();
    }

    /**
     * Initiates and manages a battle sequence.
     */
    public void startBattle(Scanner scanner, Party party) {
        // 1. Setup Phase
        List<Monster> enemies = spawnMonsters(party);
        System.out.println("Battle Started! Enemies approaching:");
        for (Monster m : enemies) System.out.println("- " + m);

        int round = 1;
        boolean battleActive = true;

        // 2. Battle Loop
        while (battleActive) {
            System.out.println("\n=== Round " + round + " ===");

            // Heroes Turn
            processHeroesTurn(scanner, party, enemies);

            // Check Victory (All monsters dead)
            if (enemies.stream().allMatch(Monster::isFainted)) {
                processVictory(party, enemies);
                battleActive = false;
                break;
            }

            // Monsters Turn
            processMonstersTurn(party, enemies);

            // Check Defeat (All heroes fainted)
            if (party.isPartyWipedOut()) {
                System.out.println("The party has been defeated!");
                battleActive = false;
                break;
            }

            // End of Round Regeneration
            performRegeneration(party);
            round++;
        }
    }

    private List<Monster> spawnMonsters(Party party) {
        List<Monster> enemies = new ArrayList<>();
        int partySize = party.getSize();

        // Determine difficulty (based on highest level hero)
        int targetLevel = party.getHeroes().stream()
                .mapToInt(Hero::getLevel)
                .max().orElse(1);

        for (int i = 0; i < partySize; i++) {
            // Pick a random template
            Monster template = monsterCatalog.get(rng.nextInt(monsterCatalog.size()));

            // Create a new instance scaled to target level
            // Note: In a real app, we'd use a Copy Constructor. Here we create manually.
            Monster monster = new Monster(
                    template.getName(),
                    template.getType(),
                    targetLevel, // Force level match
                    template.getBaseDamage() * (targetLevel / (double)Math.max(1, template.getLevel())), // Scale Dmg
                    template.getDefense() * (targetLevel / (double)Math.max(1, template.getLevel())),    // Scale Def
                    template.getDodgeChance() * 100 // Convert back to 0-100 for constructor
            );
            enemies.add(monster);
        }
        return enemies;
    }

    private void processHeroesTurn(Scanner scanner, Party party, List<Monster> enemies) {
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) continue;
            if (enemies.stream().allMatch(Monster::isFainted)) break;

            System.out.println("\nIt is " + hero.getName() + "'s turn.");
            System.out.println(hero);

            boolean actionTaken = false;
            while (!actionTaken) {
                System.out.println("1. Attack");
                System.out.println("2. Cast Spell");
                System.out.println("3. Use Potion");
                System.out.println("4. Equip Gear");
                System.out.println("5. Info");

                int choice = InputValidator.getValidInt(scanner, "Action: ", 1, 5);
                switch (choice) {
                    case 1: actionTaken = performAttack(scanner, hero, enemies); break;
                    case 2: actionTaken = performSpell(scanner, hero, enemies); break;
                    case 3: actionTaken = performPotion(scanner, hero); break;
                    case 4: performEquip(scanner, hero); break; // Doesn't consume turn
                    case 5: showBattleInfo(party, enemies); break; // Doesn't consume turn
                }
            }
        }
    }

    private boolean performAttack(Scanner scanner, Hero hero, List<Monster> enemies) {
        Monster target = selectMonster(scanner, enemies);
        if (target == null) return false; // Cancelled

        // Dodge Check
        if (rng.nextDouble() < target.getDodgeChance()) {
            System.out.println(target.getName() + " dodged the attack!");
            return true;
        }

        // Calculate Damage
        double weaponDmg = (hero.getEquippedWeapon() != null) ? hero.getEquippedWeapon().getDamage() : 0;
        double rawDamage = (hero.getStrength() + weaponDmg) * 0.05;

        // Apply Mitigation
        double actualDamage = Math.max(0, rawDamage - (target.getDefense() * 0.05)); // Simple mitigation formula

        target.setHp(target.getHp() - actualDamage);
        System.out.printf("%s attacks %s for %.0f damage!\n", hero.getName(), target.getName(), actualDamage);

        if (target.isFainted()) System.out.println(target.getName() + " has been defeated!");

        return true;
    }

    private boolean performSpell(Scanner scanner, Hero hero, List<Monster> enemies) {
        List<Spell> spells = hero.getInventory().getSpells();
        if (spells.isEmpty()) {
            System.out.println("You have no spells!");
            return false;
        }

        System.out.println("--- Spellbook ---");
        for (int i = 0; i < spells.size(); i++) {
            System.out.println((i + 1) + ". " + spells.get(i));
        }
        System.out.println((spells.size() + 1) + ". Cancel");

        int choice = InputValidator.getValidInt(scanner, "Select Spell: ", 1, spells.size() + 1);
        if (choice == spells.size() + 1) return false;

        Spell spell = spells.get(choice - 1);
        if (hero.getMana() < spell.getManaCost()) {
            System.out.println("Not enough Mana!");
            return false;
        }

        Monster target = selectMonster(scanner, enemies);
        if (target == null) return false;

        // Execute Spell
        hero.setMana(hero.getMana() - spell.getManaCost());

        // Damage Calculation
        double damage = spell.getDamage() + ((hero.getDexterity() / 10000.0) * spell.getDamage());
        target.setHp(target.getHp() - damage);

        // Apply Status Effect
        if (!target.isFainted()) {
            if (spell.getType() == SpellType.ICE) {
                target.reduceDamage(target.getBaseDamage() * 0.1);
                System.out.println(target.getName() + "'s damage reduced by Ice!");
            } else if (spell.getType() == SpellType.FIRE) {
                target.reduceDefense(target.getDefense() * 0.1);
                System.out.println(target.getName() + "'s defense melted by Fire!");
            } else if (spell.getType() == SpellType.LIGHTNING) {
                target.reduceDodgeChance(target.getDodgeChance() * 0.1);
                System.out.println(target.getName() + "'s dodge reduced by Lightning!");
            }
        }

        System.out.printf("%s casts %s on %s for %.0f damage!\n", hero.getName(), spell.getName(), target.getName(), damage);
        hero.getInventory().removeItem(spell); // Consumed
        return true;
    }

    private boolean performPotion(Scanner scanner, Hero hero) {
        List<Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println("No potions in inventory.");
            return false;
        }

        System.out.println("--- Potions ---");
        for(int i=0; i<potions.size(); i++) System.out.println((i+1) + ". " + potions.get(i));

        int choice = InputValidator.getValidInt(scanner, "Use Potion: ", 1, potions.size());
        Potion potion = potions.get(choice - 1);

        // Apply Effect
        double val = potion.getAttributeIncrease();
        if (potion.affects("Health")) hero.setHp(hero.getHp() + val);
        if (potion.affects("Mana")) hero.setMana(hero.getMana() + val);
        if (potion.affects("Strength")) hero.setStrength(hero.getStrength() + val);
        if (potion.affects("Dexterity")) hero.setDexterity(hero.getDexterity() + val);
        if (potion.affects("Agility")) hero.setAgility(hero.getAgility() + val);

        System.out.println(hero.getName() + " used " + potion.getName() + "!");
        hero.getInventory().removeItem(potion);
        return true;
    }

    private void performEquip(Scanner scanner, Hero hero) {
        System.out.println("1. Weapons");
        System.out.println("2. Armor");
        int type = InputValidator.getValidInt(scanner, "Type: ", 1, 2);

        if (type == 1) {
            List<Weapon> weps = hero.getInventory().getWeapons();
            if (weps.isEmpty()) { System.out.println("No weapons."); return; }
            for(int i=0; i<weps.size(); i++) System.out.println((i+1) + ". " + weps.get(i));
            int sel = InputValidator.getValidInt(scanner, "Equip: ", 1, weps.size());
            hero.equipWeapon(weps.get(sel-1));
        } else {
            List<Armor> arms = hero.getInventory().getArmor();
            if (arms.isEmpty()) { System.out.println("No armor."); return; }
            for(int i=0; i<arms.size(); i++) System.out.println((i+1) + ". " + arms.get(i));
            int sel = InputValidator.getValidInt(scanner, "Equip: ", 1, arms.size());
            hero.equipArmor(arms.get(sel-1));
        }
    }

    private void processMonstersTurn(Party party, List<Monster> enemies) {
        for (Monster monster : enemies) {
            if (monster.isFainted()) continue;

            // Pick random alive hero
            List<Hero> aliveHeroes = party.getHeroes().stream()
                    .filter(h -> !h.isFainted())
                    .collect(Collectors.toList());

            if (aliveHeroes.isEmpty()) break;

            Hero target = aliveHeroes.get(rng.nextInt(aliveHeroes.size()));

            // Hero Dodge Check
            if (rng.nextDouble() < target.getAgility() * 0.002) {
                System.out.println(target.getName() + " dodged " + monster.getName() + "'s attack!");
                continue;
            }

            // Calc Damage
            double rawDmg = monster.getBaseDamage();
            double mitigation = (target.getEquippedArmor() != null) ? target.getEquippedArmor().getDamageReduction() : 0;
            // Armor mitigates damage (assuming simple subtraction or percentage based on spec interpretation)
            // Spec says "damage reduction value". Let's assume generic RPG subtraction but clamped.
            double finalDmg = Math.max(0, rawDmg - (mitigation * 0.2)); // Factor 0.2 to balance big numbers

            target.setHp(target.getHp() - finalDmg);
            System.out.printf("%s attacks %s for %.0f damage!\n", monster.getName(), target.getName(), finalDmg);

            if (target.isFainted()) {
                System.out.println(target.getName() + " has fainted!");
            }
        }
    }

    private void performRegeneration(Party party) {
        for (Hero h : party.getHeroes()) {
            if (!h.isFainted()) {
                h.setHp(h.getHp() * 1.1);
                h.setMana(h.getMana() * 1.1);
            }
        }
        System.out.println("Heroes regain some health and mana.");
    }

    private void processVictory(Party party, List<Monster> enemies) {
        System.out.println("\n*** VICTORY! ***");
        double goldReward = enemies.stream().mapToDouble(Monster::getLevel).sum() * 100;
        int xpReward = enemies.size() * 2;

        System.out.printf("Party gains %.0f Gold and %d XP!\n", goldReward, xpReward);

        for (Hero h : party.getHeroes()) {
            if (h.isFainted()) {
                System.out.println(h.getName() + " is revived.");
                h.revive();
            } else {
                h.addMoney(goldReward);
                h.gainExperience(xpReward);
            }
        }
    }

    private Monster selectMonster(Scanner scanner, List<Monster> enemies) {
        List<Monster> alive = enemies.stream().filter(m -> !m.isFainted()).collect(Collectors.toList());
        if (alive.isEmpty()) return null;

        System.out.println("Select Target:");
        for(int i=0; i<alive.size(); i++) {
            System.out.println((i+1) + ". " + alive.get(i));
        }
        int choice = InputValidator.getValidInt(scanner, "Target: ", 1, alive.size());
        return alive.get(choice - 1);
    }

    private void showBattleInfo(Party party, List<Monster> enemies) {
        System.out.println("\n--- Battle Status ---");
        System.out.println("HEROES:");
        party.getHeroes().forEach(System.out::println);
        System.out.println("MONSTERS:");
        enemies.forEach(System.out::println);
        System.out.println("---------------------");
    }
}