// import statements

import java.util.StringTokenizer;

// class for a generic player
public class Player {

   // fields
   private int roomNum;
   private String name;
   private String playerInventory;
   private boolean continuePlay;

   // constructor
   public Player(String name, int room, String inventory) {
      this.name = name;
      roomNum = room;
      continuePlay = true;
      playerInventory = inventory;
   }

   // getter method to return player's inventory
   public String getInventory() {
      return playerInventory;
   }

   // getter function to get listing of inventor
   public void printInventory() {
      StringTokenizer strT = new StringTokenizer(playerInventory, ";");
      System.out.println(name + "'s items: ");
      int numItems = 0;
      while (strT.hasMoreTokens()) {
         System.out.println("  " + strT.nextToken());
         numItems++;
      }
      System.out.println("  A total of " + numItems + " item(s)");
   }

   // setter function
   public void setContinuePlay(boolean continuePlay) {
      this.continuePlay = continuePlay;
   }

   // getter function
   public boolean getContinuePlay() {
      return continuePlay;
   }

   // getter function
   public String getName() {
      return name;
   }

   // add an item to user's inventory
   public void addToInventory(String item) {
      playerInventory = Utilities.addToString(item, playerInventory);
   }

   // remove item from user's inventory
   public void removeFromInventory(String item) {
      playerInventory = Utilities.removeFromList(item, playerInventory);
   }
}