/** Console entry point and shared factory for the final RPG game world. */
public class GameVersionFinal {

    public static Room[] createGameBoard() {
        final int numRooms = 9;
        Room[] gameBoard = new Room[numRooms];

        gameBoard[0] = new StandardRoom("First Room", "1 apple;pumpkin", 0, "e3;w2;s1;n4");
        gameBoard[1] = new StandardRoom("Lab Room", "computer;monitor", 1, "n0;s8");
        gameBoard[2] = new StandardRoom("Green Room", "2 big dogs;", 2, "e0;w7;n5");
        gameBoard[3] = new StandardRoom("Kitchen", "pizza;milk;health potion", 3, "w0;n7;s6");
        gameBoard[4] = new StandardRoom("Game Room", "giant tv;barking Germany Shephard;2 kids playing", 4, "e6;w5;s0");
        gameBoard[5] = new StandardRoom("Den Room", "meowing kitten;", 5, "e4;s2");
        gameBoard[6] = new StandardRoom("BedRoom", "bed;office desk;", 6, "w4;n3");
        gameBoard[7] = new MonsterRoom("Scary Nightmare", "knife;some blood;", 7, "e2;s3");
        gameBoard[8] = new SleepRoom("Sleep Room", "pillow;warm fireplace;", 8, "n1");
        return gameBoard;
    }

    public static CustomPlayer createPlayer() {
        return new CustomPlayer("Player", 0, "candy bar;steak;coffee", 50, 100);
    }

    public static void main(String[] args) {
        Room[] gameBoard = createGameBoard();
        CustomPlayer player = createPlayer();
        int playerRoomNumber = 0;

        while (player.getContinuePlay()) {
            playerRoomNumber = gameBoard[playerRoomNumber].playRoom(player);
        }
    }
}
