
import java.util.StringTokenizer;

public class Utilities {

    public static String addToString(String item, String list) {
        StringBuilder listSB = new StringBuilder(list);
        listSB.append(";" + item);
        return listSB.toString();
    }

    public static boolean isItemInContainer(String item, String container) {
        StringTokenizer strT = new StringTokenizer(container, ";");
        while (strT.hasMoreTokens()) {
            String nextToken = strT.nextToken();
            if (nextToken.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static String removeFromList(String item, String list) {
        String newList = "";
        StringTokenizer listT = new StringTokenizer(list, ";");
        while (listT.hasMoreTokens()) {
            String nextToken = listT.nextToken();
            if (!nextToken.equals(item)) {
                newList += nextToken + ";";
            }
        }
        return newList;
    }

    public static void printHelp() {
        System.out.println("The commands that you can use: ");
        System.out.println("  exits      - will print out all exits from current room");
        System.out.println("  look       - will display all (visible) items in the room");
        System.out.println("  inventory  - list the items that you are carrying");
        System.out.println("  go dir     - exit the room, where dir is east, west, north or south");
        System.out.println("  quit       - quit the game");
        System.out.println("  pick up    - pick up an item (another question will display)");
        System.out.println("  drop       - drop an item (another question will display)");
        System.out.println("  help       - this menu.");
    }
}