import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Holds the attacks currently available to one player. */
public class PlayerAttackSet {

    private final List<Attack> attacks;

    public PlayerAttackSet() {
        attacks = new ArrayList<>();

        attacks.add(new Attack(
                "Quick Strike", "A fast and reliable attack.",
                8, 95, AttackEffect.NONE, 0, 0));

        attacks.add(new Attack(
                "Heavy Blow", "A powerful but less accurate attack.",
                15, 75, AttackEffect.NONE, 0, 0));

        attacks.add(new Attack(
                "Poison Jab", "Poisons the opponent for two turns.",
                5, 85, AttackEffect.POISON, 3, 2));

        attacks.add(new Attack(
                "Stunning Blow", "Attempts to prevent the opponent's next turn.",
                6, 70, AttackEffect.STUN, 0, 1));
    }

    /** Returns a read-only view so callers cannot bypass this class's API. */
    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(attacks);
    }

    public Attack getAttack(int index) {
        return attacks.get(index);
    }

    public int size() {
        return attacks.size();
    }

    /** Adds an attack, for example when a move is unlocked. */
    public void addAttack(Attack attack) {
        attacks.add(Objects.requireNonNull(attack, "Attack cannot be null"));
    }
}
