/**
 * Contract for an inventory item that can be consumed by a player.
 * Different consumable types can provide their own use behavior.
 */
public interface Consumable {

    String getName();

    String getDescription();

    /**
     * Applies the item's effect to the player.
     *
     * @return true if the item effect was applied; false otherwise
     */
    boolean consume(CustomPlayer player);
}
