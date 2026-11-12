import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * Central registry of consumable health items.
 * Add new food or potion definitions here; the inventory itself remains a
 * simple semicolon-separated list of item names.
 */
public final class HealthItemCatalog {

    private static final Map<String, HealthItem> ITEMS = new LinkedHashMap<>();

    static {
        register(new HealthItem("Apple", "A light snack that restores a little health.", 10),
                "1 apple");
        register(new HealthItem("Pumpkin", "A filling vegetable snack.", 12));
        register(new HealthItem("Pizza", "A satisfying meal that restores health.", 18));
        register(new HealthItem("Milk", "A glass of milk that restores a little health.", 8));
        register(new HealthItem("Candy Bar", "A quick sugary snack.", 8));
        register(new HealthItem("Steak", "A hearty meal that restores a good amount of health.", 20));
        register(new HealthItem("Coffee", "A hot drink that restores a small amount of health.", 5));
        register(new HealthItem("Health Potion", "A magical potion that restores substantial health.", 35));
    }

    private HealthItemCatalog() {
        // Utility class; do not instantiate.
    }

    /**
     * Registers an item and any inventory-name aliases for it.
     * This is the extension point for adding more food and potion types.
     */
    public static synchronized void register(HealthItem item, String... aliases) {
        if (item == null) {
            throw new IllegalArgumentException("Health item cannot be null");
        }

        registerName(item.getName(), item);
        if (aliases != null) {
            for (String alias : aliases) {
                registerName(alias, item);
            }
        }
    }

    private static void registerName(String name, HealthItem item) {
        String key = normalize(name);
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Health item names cannot be blank");
        }

        HealthItem existing = ITEMS.get(key);
        if (existing != null && existing != item) {
            throw new IllegalArgumentException("A health item is already registered as: " + name);
        }
        ITEMS.put(key, item);
    }

    /** Finds a registered health item by the name stored in the inventory. */
    public static synchronized HealthItem find(String inventoryItemName) {
        return ITEMS.get(normalize(inventoryItemName));
    }

    /** Returns each registered health item once, without exposing the registry. */
    public static synchronized Collection<HealthItem> getAll() {
        List<HealthItem> uniqueItems =
                new ArrayList<>(new LinkedHashSet<>(ITEMS.values()));
        return Collections.unmodifiableList(uniqueItems);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
