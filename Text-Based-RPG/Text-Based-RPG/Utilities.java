import java.util.StringTokenizer;

public class Utilities {

    public static String addToString(String item, String list) {
        if (item == null || item.isBlank()) {
            return list == null ? "" : list;
        }
        if (list == null || list.isBlank()) {
            return item;
        }
        String normalizedList = list.endsWith(";") ? list : list + ";";
        return normalizedList + item;
    }

    public static boolean isItemInContainer(String item, String container) {
        if (item == null || container == null || container.isEmpty()) {
            return false;
        }
        StringTokenizer strT = new StringTokenizer(container, ";");
        while (strT.hasMoreTokens()) {
            if (strT.nextToken().trim().equalsIgnoreCase(item.trim())) {
                return true;
            }
        }
        return false;
    }

    /** Removes only the first matching occurrence, not every duplicate. */
    public static String removeFromList(String item, String list) {
        if (list == null || list.isEmpty() || item == null) {
            return list == null ? "" : list;
        }

        StringBuilder result = new StringBuilder();
        StringTokenizer listT = new StringTokenizer(list, ";");
        boolean removed = false;

        while (listT.hasMoreTokens()) {
            String nextToken = listT.nextToken();
            if (!removed && nextToken.trim().equalsIgnoreCase(item.trim())) {
                removed = true;
                continue;
            }
            if (result.length() > 0) {
                result.append(';');
            }
            result.append(nextToken);
        }
        return result.toString();
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
        System.out.println("  use        - consume a health potion or food item from your inventory");
        System.out.println("  use <item> - consume the specified health item directly");
        System.out.println("  help       - this menu.");
    }
}
