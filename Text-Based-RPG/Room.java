import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for every room in the game.
 *
 * It stores the data and shared commands common to all rooms. Each concrete
 * room decides what happens when the player enters it by implementing
 * playRoom(CustomPlayer).
 */
public abstract class Room {

    // Protected so specialized rooms can customize their room contents.
    protected String roomObjects;
    private final String roomName;
    private final String roomExits;
    private final int roomNum;

    /** Each concrete room defines its own entry behavior. */
    public abstract int playRoom(CustomPlayer player);

    /**
     * Backwards-compatible entry point for code that only has a Player
     * reference. The main game uses CustomPlayer and the abstract method above.
     */
    public int playRoom(Player player) {
        return getNextRoom(player);
    }

    /** Shared command loop used by ordinary rooms and after special-room events. */
    public int getNextRoom(Player player) {
        System.out.println("You are in the " + roomName);
        System.out.println("What would you like to do?");

        int newRoomNum = roomNum;
        String command = GameInput.readLine();
        if (command == null) {
            player.setContinuePlay(false);
            return newRoomNum;
        }
        command = command.trim().toLowerCase();

        switch (command) {
            case "exits":
                printListOfExits();
                break;
            case "look":
                printObjectsInRoom();
                break;
            case "inventory":
                player.printInventory();
                break;
            case "go west":
            case "go east":
            case "go south":
            case "go north":
                newRoomNum = leaveRoom(command);
                break;
            case "quit":
                player.setContinuePlay(false);
                break;
            case "pick up":
                userPicksUp(player);
                break;
            case "drop":
                userDrops(player);
                break;
            case "use":
                userUsesItem(player);
                break;
            case "help":
                Utilities.printHelp();
                break;
            default:
                if (command.startsWith("use ")) {
                    userUsesItem(player, command.substring(4));
                } else {
                    System.out.println("Invalid command. Type help for details.");
                }
        }
        return newRoomNum;
    }

    /** Prompts for a consumable item, or accepts an item name after the command. */
    protected void userUsesItem(Player player) {
        userUsesItem(player, null);
    }

    protected void userUsesItem(Player player, String requestedItem) {
        if (!(player instanceof CustomPlayer)) {
            System.out.println("This player does not have a health system.");
            return;
        }

        String itemName = requestedItem;
        if (itemName == null || itemName.isBlank()) {
            System.out.println("Which item would you like to use?");
            itemName = GameInput.readLine();
            if (itemName == null) {
                player.setContinuePlay(false);
                return;
            }
        }

        ((CustomPlayer) player).useHealthItem(itemName.trim());
    }

    protected void userPicksUp(Player player) {
        System.out.println("What would you like to pick up?");
        String itemToPickUp = GameInput.readLine();
        if (itemToPickUp == null) {
            player.setContinuePlay(false);
            return;
        }
        if (pickUpItem(player, itemToPickUp)) {
            System.out.println("Picked up " + itemToPickUp.trim() + ".");
        } else {
            System.out.println("That item is not in the room");
        }
    }

    protected void userDrops(Player player) {
        System.out.println("What would you like to drop?");
        String itemToDrop = GameInput.readLine();
        if (itemToDrop == null) {
            player.setContinuePlay(false);
            return;
        }
        if (dropItem(player, itemToDrop)) {
            System.out.println("Dropped " + itemToDrop.trim() + ".");
        } else {
            System.out.println("You cannot drop something that you don't have");
        }
    }

    /** Adds one matching room object to the player's inventory. */
    public boolean pickUpItem(Player player, String itemName) {
        if (player == null || itemName == null || itemName.isBlank()
                || !Utilities.isItemInContainer(itemName, roomObjects)) {
            return false;
        }
        player.addToInventory(itemName.trim());
        roomObjects = Utilities.removeFromList(itemName.trim(), roomObjects);
        return true;
    }

    /** Drops one matching inventory entry into this room. */
    public boolean dropItem(Player player, String itemName) {
        if (player == null || itemName == null || itemName.isBlank()
                || !Utilities.isItemInContainer(itemName, player.getInventory())) {
            return false;
        }
        player.removeFromInventory(itemName.trim());
        roomObjects = Utilities.addToString(itemName.trim(), roomObjects);
        return true;
    }

    /** Returns the current room objects as a read-only list. */
    public List<String> getRoomItems() {
        List<String> items = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(roomObjects, ";");
        while (tokens.hasMoreTokens()) {
            String item = tokens.nextToken().trim();
            if (!item.isEmpty()) {
                items.add(item);
            }
        }
        return Collections.unmodifiableList(items);
    }

    public Room(String roomName, String objects, int roomNum, String listOfExits) {
        this.roomName = roomName;
        this.roomObjects = objects == null ? "" : objects;
        this.roomNum = roomNum;
        this.roomExits = listOfExits == null ? "" : listOfExits;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomObjects() {
        return roomObjects;
    }

    public int getRoomNum() {
        return roomNum;
    }

    /** Returns available exits as direction-to-room-number pairs for GUI use. */
    public Map<String, Integer> getAvailableExits() {
        Map<String, Integer> exits = new LinkedHashMap<>();
        StringTokenizer tokens = new StringTokenizer(roomExits, ";");
        while (tokens.hasMoreTokens()) {
            String exit = tokens.nextToken().trim().toLowerCase();
            if (exit.length() < 2) {
                continue;
            }
            String direction;
            switch (exit.charAt(0)) {
                case 'n': direction = "north"; break;
                case 'e': direction = "east"; break;
                case 's': direction = "south"; break;
                case 'w': direction = "west"; break;
                default: continue;
            }
            try {
                int destination = Integer.parseInt(exit.substring(1));
                if (destination >= 0) {
                    exits.put(direction, destination);
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed exit entries.
            }
        }
        return Collections.unmodifiableMap(exits);
    }

    public void addToObjects(String item) {
        roomObjects = Utilities.addToString(item, roomObjects);
    }

    public void removeFromObjects(String item) {
        roomObjects = Utilities.removeFromList(item, roomObjects);
    }

    public int leaveRoom(String command) {
        if (command == null || !command.startsWith("go ")) {
            System.out.println("That exit is not available");
            return roomNum;
        }
        String direction = command.substring(3).trim().toLowerCase();
        Integer destination = getAvailableExits().get(direction);
        if (destination == null) {
            System.out.println("That exit is not available");
            return roomNum;
        }
        return destination;
    }

    public void printObjectsInRoom() {
        StringTokenizer strT = new StringTokenizer(roomObjects, ";");
        System.out.println("Items in " + roomName);
        int numItems = 0;
        while (strT.hasMoreTokens()) {
            System.out.println("  " + strT.nextToken());
            numItems++;
        }
        System.out.println("  A total of " + numItems + " item(s)");
    }

    public void printListOfExits() {
        StringTokenizer exitsT = new StringTokenizer(roomExits, ";");
        while (exitsT.hasMoreTokens()) {
            String nextToken = exitsT.nextToken().trim().toLowerCase();
            if (nextToken.isEmpty()) {
                continue;
            }
            switch (nextToken.charAt(0)) {
                case 'n': System.out.println("There is an exit to the north"); break;
                case 'e': System.out.println("There is an exit to the east"); break;
                case 's': System.out.println("There is an exit to the south"); break;
                case 'w': System.out.println("There is an exit to the west"); break;
                default: break;
            }
        }
    }
}
