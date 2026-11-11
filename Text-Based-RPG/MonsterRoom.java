import java.util.Scanner;

public class MonsterRoom extends Room {

   public MonsterRoom(String roomName, String objects, int roomNum, String listOfExits) {
      super(roomName, objects, roomNum, listOfExits);
   
   }
   
   // The playRoom method can be invoked with either
   // a CustomPlayer object OR a Player object. They
   // both invoke the same method, getNextRoom	
   public int playRoom(Player player) {
      int aPlayer = getNextRoom(player);
      return aPlayer;
   }
   
   // transfer item from room to user's inventory
   protected void userPicksUp(Player player) {
      Scanner keyboard = new Scanner(System.in);
      System.out.println("Is there something interesting, youd like to take?");
      String itemToPickUp = keyboard.nextLine();
      if (Utilities.isItemInContainer(itemToPickUp, roomObjects)) {
         player.addToInventory(itemToPickUp);
         roomObjects = Utilities.removeFromList(itemToPickUp, roomObjects);
         System.out.println("Item Stolen!");
      } else {
         System.out.println("Item was not Stolen");
      }
   }

   // transfer item from user's inventory to room   	
   protected void userDrops(Player player) {
      System.out.println("What item would you like to steal?");
      Scanner keyboard = new Scanner(System.in);
      String itemToDrop = keyboard.nextLine();
      if (Utilities.isItemInContainer(itemToDrop, player.getInventory())) {
         player.removeFromInventory(itemToDrop);
         roomObjects = Utilities.addToString(itemToDrop, roomObjects);
         System.out.println("Item Acquired.");
      } else {
         System.out.println("Item not Acquired.");
      }
   }
}