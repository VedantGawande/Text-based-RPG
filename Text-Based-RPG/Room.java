import java.util.StringTokenizer;

// The superclass with fields and methods for a generic game room.
public class Room {

    // Protected so specialized rooms can customize their room contents.
    protected String roomObjects;
    private final String roomName;
    private final String roomExits;
    private final int roomNum;

    public int playRoom(CustomPlayer player) {
        return getNextRoom(player);
    }

    public int playRoom(Player player) {
        return getNextRoom(player);
    }

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
        itemToPickUp = itemToPickUp.trim();

        if (Utilities.isItemInContainer(itemToPickUp, roomObjects)) {
            player.addToInventory(itemToPickUp);
            roomObjects = Utilities.removeFromList(itemToPickUp, roomObjects);
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
        itemToDrop = itemToDrop.trim();

        if (Utilities.isItemInContainer(itemToDrop, player.getInventory())) {
            player.removeFromInventory(itemToDrop);
            roomObjects = Utilities.addToString(itemToDrop, roomObjects);
        } else {
            System.out.println("You cannot drop something that you don't have");
        }
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

        char requestedDirection = 0;
        switch (command) {
            case "go north": requestedDirection = 'n'; break;
            case "go east":  requestedDirection = 'e'; break;
            case "go south": requestedDirection = 's'; break;
            case "go west":  requestedDirection = 'w'; break;
            default:
                System.out.println("That exit is not available");
                return roomNum;
        }

        StringTokenizer exitsT = new StringTokenizer(roomExits, ";");
        while (exitsT.hasMoreTokens()) {
            String exit = exitsT.nextToken().trim().toLowerCase();
            if (exit.length() >= 2 && exit.charAt(0) == requestedDirection) {
                try {
                    int destination = Integer.parseInt(exit.substring(1));
                    if (destination >= 0) {
                        return destination;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore malformed exit entries and continue checking.
                }
            }
        }

        System.out.println("That exit is not available");
        return roomNum;
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
