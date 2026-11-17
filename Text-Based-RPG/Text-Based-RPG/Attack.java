import java.util.Objects;

/**
 * Immutable description of an attack. This class describes a move only;
 * the combat controller is responsible for executing it.
 */
public class Attack {

    private final String name;
    private final String description;
    private final int damage;
    private final int accuracy;
    private final AttackEffect effectType;
    private final int effectPower;
    private final int effectDuration;

    public Attack(String name, String description, int damage, int accuracy,
                  AttackEffect effectType, int effectPower, int effectDuration) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attack needs a name");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        if (accuracy < 0 || accuracy > 100) {
            throw new IllegalArgumentException("Accuracy must be between 0 and 100");
        }
        if (effectPower < 0 || effectDuration < 0) {
            throw new IllegalArgumentException("Effect power and duration cannot be negative");
        }

        this.name = name;
        this.description = description == null ? "" : description;
        this.damage = damage;
        this.accuracy = accuracy;
        this.effectType = Objects.requireNonNull(effectType, "Effect type cannot be null");
        this.effectPower = effectPower;
        this.effectDuration = effectDuration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDamage() {
        return damage;
    }

    /** Returns the chance to hit as a percentage from 0 through 100. */
    public int getAccuracy() {
        return accuracy;
    }

    public AttackEffect getEffectType() {
        return effectType;
    }

    public int getEffectPower() {
        return effectPower;
    }

    public int getEffectDuration() {
        return effectDuration;
    }
}
