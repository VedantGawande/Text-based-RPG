/**
 * Immutable definition of an item that restores player health when consumed.
 * This class describes the item only; CustomPlayer handles using it.
 */
public final class HealthItem {

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHealingAmount() {
        return healingAmount;
    }
}
