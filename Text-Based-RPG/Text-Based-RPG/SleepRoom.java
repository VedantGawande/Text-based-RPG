public class SleepRoom extends Room {

    public SleepRoom(String roomName, String objects, int roomNum, String listOfExits) {
        super(roomName, objects, roomNum, listOfExits);
    }

    @Override
    public int playRoom(CustomPlayer player) {
        System.out.println("You are in the " + getRoomName());
        System.out.println("What would you like to do?");

        int newRoomNum = getRoomNum();
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
                printHelp();
                break;
            case "sleep":
                sleepOnStraw(player);
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

    /** Restores one health point, up to the player's maximum health. */
    public void sleepOnStraw(CustomPlayer player) {
        System.out.println("Ah. Good idea! Before you lay down...");
        System.out.println(player.printHealthPoints());

        player.heal(1);

        System.out.println("You wake after 3 hours, and then...");
        System.out.println(player.printHealthPoints());
    }

    public static void printHelp() {
        Utilities.printHelp();
        System.out.println("  sleep      - restore 1 health point (up to maximum health)");
    }
}
