/**
 * Immutable definition of an item that restores player health when consumed.
 * This class describes the item only; CustomPlayer handles using it.
 */
public final class HealthItem implements Consumable {

    private final String name;
    private final String description;
    private final int healingAmount;

    public HealthItem(String name, String description, int healingAmount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A health item needs a name");
        }
        if (healingAmount <= 0) {
            throw new IllegalArgumentException("Healing amount must be positive");
        }

        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.healingAmount = healingAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getHealingAmount() {
        return healingAmount;
    }

    /** Applies this health item's effect without managing inventory removal. */
    @Override
    public boolean consume(CustomPlayer player) {
        if (player == null || !player.isAlive()
                || player.getHealth() >= player.getMaxHealth()) {
            return false;
        }

        player.heal(healingAmount);
        return true;
    }
}
