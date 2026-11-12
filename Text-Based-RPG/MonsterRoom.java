public class MonsterRoom extends Room {

    public MonsterRoom(String roomName, String objects, int roomNum, String listOfExits) {
        super(roomName, objects, roomNum, listOfExits);
    }

    /**
     * Important: GameVersion4 stores rooms in a Room[] and passes a
     * CustomPlayer. This overload is therefore the method selected by Java.
     * The combat developer can add the battle flow here.
     */
    @Override
    public int playRoom(CustomPlayer player) {
        // TODO: Start the monster battle here before/around room interaction.
        return getNextRoom(player);
    }

    @Override
    public int playRoom(Player player) {
        return getNextRoom(player);
    }

    @Override
    protected void userPicksUp(Player player) {
        System.out.println("Is there something interesting you'd like to take?");
        String itemToPickUp = GameInput.readLine();
        if (itemToPickUp == null) {
            player.setContinuePlay(false);
            return;
        }
        itemToPickUp = itemToPickUp.trim();

        if (Utilities.isItemInContainer(itemToPickUp, roomObjects)) {
            player.addToInventory(itemToPickUp);
            roomObjects = Utilities.removeFromList(itemToPickUp, roomObjects);
            System.out.println("Item stolen!");
        } else {
            System.out.println("Item was not stolen.");
        }
    }

    @Override
    protected void userDrops(Player player) {
        System.out.println("What item would you like to drop?");
        String itemToDrop = GameInput.readLine();
        if (itemToDrop == null) {
            player.setContinuePlay(false);
            return;
        }
        itemToDrop = itemToDrop.trim();

        if (Utilities.isItemInContainer(itemToDrop, player.getInventory())) {
            player.removeFromInventory(itemToDrop);
            roomObjects = Utilities.addToString(itemToDrop, roomObjects);
            System.out.println("Item dropped.");
        } else {
            System.out.println("You do not have that item.");
        }
    }
}
