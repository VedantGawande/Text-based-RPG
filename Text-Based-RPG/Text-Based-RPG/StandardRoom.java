/**
 * A normal room with the standard exploration commands and no special event
 * when the player enters it.
 */
public class StandardRoom extends Room {

    public StandardRoom(String roomName, String objects, int roomNum,
                        String listOfExits) {
        super(roomName, objects, roomNum, listOfExits);
    }

    @Override
    public int playRoom(CustomPlayer player) {
        return getNextRoom(player);
    }
}
