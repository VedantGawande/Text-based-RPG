// import statements

import java.util.StringTokenizer;
import java.util.Scanner;

// The superclass with fields and methods
// for a generic game room
public class Room {

   // fields
   public String roomObjects;
   private String roomName;
   private String roomExits;
   private int roomNum;

   // The playRoom method can be invoked with either
   // a CustomPlayer object OR a Player object. They
   // both invoke the same method, getNextRoom	
   public int playRoom(CustomPlayer player) {
      return getNextRoom(player);
   }

   public int playRoom(Player player) {
      return getNextRoom(player);
   }

   public int getNextRoom(Player player) {
   
      // inform the player of his/her current room
      System.out.println("You are in the " + roomName);
      System.out.println("What would you like to do?");
   
      // variables for player interaction
      String command;
      int newRoomNum = roomNum;
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
         default:
            System.out.println("Invalid command. Type help for details.");
      }
      return newRoomNum;
   }

   // transfer item from room to user's inventory
   protected void userPicksUp(Player player) {
      System.out.println("What would you like to pick up?");
      Scanner keyboard = new Scanner(System.in);
      String itemToPickUp = keyboard.nextLine();
      if (Utilities.isItemInContainer(itemToPickUp, roomObjects)) {
         player.addToInventory(itemToPickUp);
         roomObjects = Utilities.removeFromList(itemToPickUp, roomObjects);
      } else {
         System.out.println("That item is not in the room");
      }
   }

   // transfer item from user's inventory to room   	
   protected void userDrops(Player player) {
      System.out.println("What would you like to drop?");
      Scanner keyboard = new Scanner(System.in);
      String itemToDrop = keyboard.nextLine();
      if (Utilities.isItemInContainer(itemToDrop, player.getInventory())) {
         player.removeFromInventory(itemToDrop);
         roomObjects = Utilities.addToString(itemToDrop, roomObjects);
      } else {
         System.out.println("You cannot drop something that you don't have");
      }
   }

   // constructor   
   public Room(String roomName, String objects, int roomNum, String listOfExits) {
      this.roomName = roomName;
      this.roomObjects = objects;
      this.roomNum = roomNum;
      this.roomExits = listOfExits;
   }

   // getter function
   public String getRoomName() {
      return roomName;
   }

   // getter function
   public String getRoomObjects() {
      return roomObjects;
   }

   // getter function
   public int getRoomNum() {
      return roomNum;
   }

   // add an item to room's inventory
   public void addToObjects(String item) {
      roomObjects = Utilities.addToString(item, roomObjects);
   }

   // remove item from rooms's inventory
   public void removeFromObjects(String item) {
      roomObjects = Utilities.removeFromList(item, roomObjects);
   }

   // determine new room number if user proceeds through an exit
   public int leaveRoom(String command) {
   
      StringTokenizer exitsT = new StringTokenizer(roomExits, ";");
      int exitRoom = -1;
   
      if (command.equals("go west")) {
         while (exitsT.hasMoreTokens()) {
            String nextToken = exitsT.nextToken();
            if (nextToken.substring(0, 1).equals("w")) {
               exitRoom = Integer.parseInt(nextToken.substring(1));
            }
         }
      } else if (command.equals("go east")) {
         while (exitsT.hasMoreTokens()) {
            String nextToken = exitsT.nextToken();
            if (nextToken.substring(0, 1).equals("e")) {
               exitRoom = Integer.parseInt(nextToken.substring(1));
            }
         }
      } else if (command.equals("go north")) {
         while (exitsT.hasMoreTokens()) {
            String nextToken = exitsT.nextToken();
            if (nextToken.substring(0, 1).equals("n")) {
               exitRoom = Integer.parseInt(nextToken.substring(1));
            }
         }
      } else if (command.equals("go south")) {
         while (exitsT.hasMoreTokens()) {
            String nextToken = exitsT.nextToken();
            if (nextToken.substring(0, 1).equals("s")) {
               exitRoom = Integer.parseInt(nextToken.substring(1));
            }
         }
      
      }
      if (exitRoom == -1) {
         System.out.println("That exit is not available");
         return roomNum;
      } else {
         return exitRoom;
      }
   }

   // getter function to list items in room
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

   // getter function to get list of exits
   public void printListOfExits() {
   
      StringTokenizer exitsT = new StringTokenizer(roomExits, ";");
      while (exitsT.hasMoreTokens()) {
         String nextToken = exitsT.nextToken();
         if (nextToken.substring(0, 1).equals("n")) {
            System.out.println("There is an exit to the north");
         } else if (nextToken.substring(0, 1).equals("e")) {
            System.out.println("There is an exit to the east");
         } else if (nextToken.substring(0, 1).equals("s")) {
            System.out.println("There is an exit to the south");
         } else if (nextToken.substring(0, 1).equals("w")) {
            System.out.println("There is an exit to the west");
         }
      }
   }
}