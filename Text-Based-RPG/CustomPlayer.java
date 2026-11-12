import java.util.StringTokenizer;

/** Player variant with health and a set of combat moves. */
public class CustomPlayer extends Player {

    private int healthPoints;
    private final int maxHealthPoints;
    private final PlayerAttackSet attackSet;

    /**
     * Backwards-compatible constructor: the starting health is also the
     * maximum health. Use the five-argument constructor to specify a
     * different maximum.
     */
    public CustomPlayer(String name, int room, String inventory, int health) {
        this(name, room, inventory, health, health);
    }

    public CustomPlayer(String name, int room, String inventory,
                        int startingHealth, int maxHealth) {
        super(name, room, inventory);

        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Maximum health must be positive");
        }
        if (startingHealth < 0 || startingHealth > maxHealth) {
            throw new IllegalArgumentException(
                    "Starting health must be between 0 and maximum health");
        }

        this.maxHealthPoints = maxHealth;
        this.healthPoints = startingHealth;
        this.attackSet = new PlayerAttackSet();
    }

    public int getHealth() {
        return healthPoints;
    }

    public int getMaxHealth() {
        return maxHealthPoints;
    }

    /** Reduces health by damage, never allowing health to fall below zero. */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        healthPoints = Math.max(0, healthPoints - damage);
    }

    /** Restores health, never allowing it to exceed the maximum. */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Healing amount cannot be negative");
        }
        healthPoints = (int) Math.min((long) maxHealthPoints, (long) healthPoints + amount);
    }

    public boolean isAlive() {
        return healthPoints > 0;
    }

    /** Sets health to a specific valid value, useful for resets/testing. */
    public void resetHealthPoints(int health) {
        if (health < 0 || health > maxHealthPoints) {
            throw new IllegalArgumentException("Health must be between 0 and maximum health");
        }
        healthPoints = health;
    }

    // Compatibility aliases for the names used by the original project.
    public void addHealthPoint(int amount) {
        heal(amount);
    }

    public void removeHealthPoint(int amount) {
        takeDamage(amount);
    }

    public String printHealthPoints() {
        return "Health: " + healthPoints + "/" + maxHealthPoints;
    }

    public PlayerAttackSet getAttackSet() {
        return attackSet;
    }

    /**
     * Consumes one health item from the player's inventory.
     * The item is not consumed when it is unknown, not owned, or health is full.
     *
     * @param inventoryItemName the exact item name shown in the inventory
     * @return true if an item was successfully consumed
     */
    public boolean useHealthItem(String inventoryItemName) {
        if (inventoryItemName == null || inventoryItemName.isBlank()) {
            System.out.println("Please specify an item to use.");
            return false;
        }

        String itemName = inventoryItemName.trim();
        if (!Utilities.isItemInContainer(itemName, getInventory())) {
            System.out.println("You do not have " + itemName + " in your inventory.");
            return false;
        }

        HealthItem healthItem = HealthItemCatalog.find(itemName);
        if (healthItem == null) {
            System.out.println(itemName + " cannot be consumed to restore health.");
            return false;
        }

        if (!isAlive()) {
            System.out.println("You cannot use an item while you are defeated.");
            return false;
        }

        if (healthPoints >= maxHealthPoints) {
            System.out.println("Your health is already full. Save "
                    + healthItem.getName() + " for later.");
            return false;
        }

        int healthBefore = healthPoints;
        heal(healthItem.getHealingAmount());
        int actualHealing = healthPoints - healthBefore;

        // Consume exactly one copy, and only after healing succeeds.
        removeFromInventory(itemName);

        System.out.println("You used " + healthItem.getName() + " and restored "
                + actualHealing + " health point(s).");
        System.out.println(printHealthPoints());
        return true;
    }

    @Override
    public void printInventory() {
        System.out.println("  " + printHealthPoints());
        StringTokenizer strT = new StringTokenizer(getInventory(), ";");
        System.out.println(getName() + "'s items: ");
        int numItems = 0;
        while (strT.hasMoreTokens()) {
            System.out.println("  " + strT.nextToken());
            numItems++;
        }
        System.out.println("  A total of " + numItems + " item(s)");
    }
}
