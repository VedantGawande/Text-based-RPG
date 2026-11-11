// Author : Timothy Johnson
// Date   : 2/07/2021
// File   : SleepRoom.java
  
import java.util.Scanner;

public class SleepRoom extends Room {

   public SleepRoom(String roomName, String objects, int roomNum, String listOfExits) {
      super(roomName, objects, roomNum, listOfExits);
   
   }
  
  // The playRoom method can be invoked with either
  // a CustomPlayer object OR a Player object. They
  // both invoke the same method, getNextRoom	
   public int playRoom(CustomPlayer player) {
      // inform the player of his/her current room
      System.out.println("You are in the " + getRoomName());
      System.out.println("What would you like to do?");
   
      // variables for player interaction
      String command;
      int newRoomNum = getRoomNum();
      Scanner keyboard = new Scanner(System.in);
      command = keyboard.nextLine();
      
      // switch on the user's command
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
         case "help":
            Utilities.printHelp();
            break;
         case "sleep":
            sleepOnStraw();
            break;
         default:
            System.out.println("Invalid command. Type help for details.");
      }
      return newRoomNum;
   }
       
   // Add Health Point
   public void sleepOnStraw() {
      System.out.println("Ah. Good Idea! Before you lay down...");
      System.out.println(CustomPlayer.printHealthPoints());
      
      // Add 1 health
      CustomPlayer.addHealthPoint(1+5);
      
      System.out.println("You wake after 3 hours, and then...");
      System.out.println(CustomPlayer.printHealthPoints());    
   }
  
   // Override printHelp method and add sleep command
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
      System.out.println("  sleep      - you can only sleep in the SleepRoom");
   }
   
}