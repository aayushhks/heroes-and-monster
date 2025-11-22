import game.Game;
import game.LegendsGame;
import java.util.Scanner;

/**
 * The entry point for the "Legends: Monsters and Heroes" application.
 * Responsibility: Bootstrap the application and initiate the Game Controller.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Legends: Monsters and Heroes...");

        // Resource management: Use try-with-resources to ensure Scanner closes
        try (Scanner scanner = new Scanner(System.in)) {
            // Polymorphism: We treat the specific LegendsGame as a generic Game
            Game game = new LegendsGame();
            game.play(scanner);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during game execution:");
            e.printStackTrace();
        }

        System.out.println("Application Terminated.");
    }
}