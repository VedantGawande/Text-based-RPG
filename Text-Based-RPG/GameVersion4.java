
public class GameVersion4 {

   public static void main(String args[]) {
   
      // designate how many rooms are in the game
      final int NUM_ROOMS = 9;
   
      // declare array of rooms
      Room[] gameBoard = new Room[NUM_ROOMS];
   
      // populate the room array, using the superclass Room
      gameBoard[0] = new Room("First Room", "1 apple;pumpkin", 0, "e3;w2;s1;n4");
      gameBoard[1] = new Room("Lab Room", "computer;monitor", 1, "n0;s8");
      gameBoard[2] = new Room("Green Room", "2 big dogs;", 2, "e0;w7;n5");
      
      gameBoard[3] = new Room("Kitchen", "pizza;milk", 3, "w0;n7;s6");
      gameBoard[4] = new Room("Game Room", "giant tv;barking Germany Shephard;2 kids playing", 4, "e6;w5;s0");
      gameBoard[5] = new Room("Den Room", "meowing kitten;", 5, "e4;s2");
      gameBoard[6] = new Room("BedRoom", "bed;office desk;", 6, "w4;n3");
      gameBoard[7] = new MonsterRoom("Scary Nightmare", "knife;some blood;", 7, "e2;s3");
      gameBoard[8] = new SleepRoom("Sleep Room", "pillow;warm fireplace;", 8, "n1");
   
      // keep track of which room player is in 
      int playerRoomNumber = 0;
   
      // create player object, using the superclass Player
      CustomPlayer player = new CustomPlayer("John", playerRoomNumber, "candy bar;steak;coffee", 5);
   
      // play the game, as long as player object has field continuePlay set to true
      while (player.getContinuePlay() == true) {
         playerRoomNumber =
                gameBoard[playerRoomNumber].playRoom(player);
      }
   }
}