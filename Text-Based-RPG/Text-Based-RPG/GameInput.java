import java.util.Scanner;

/**
 * Centralized console input for the game.
 * Keep one Scanner for System.in; do not close it while the game is running.
 */
public final class GameInput {

    private static final Scanner KEYBOARD = new Scanner(System.in);

    private GameInput() {
        // Utility class; do not instantiate.
    }

    /**
     * Reads one line from the console.
     * Returns null when the input stream has reached EOF.
     */
    public static String readLine() {
        return KEYBOARD.hasNextLine() ? KEYBOARD.nextLine() : null;
    }
}
