import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Holds the state and rules for one monster encounter.
 * This class has no console or GUI code, so both interfaces can use it.
 */
public class MonsterBattle {

    private static final int MONSTER_MAX_HEALTH = 40;
    private static final List<Attack> MONSTER_ATTACKS = List.of(
            new Attack("Claw Swipe", "The monster slashes with its sharp claws.",
                    6, 100, AttackEffect.NONE, 0, 0),
            new Attack("Poison Fang", "The monster bites and leaves venom in the wound.",
                    5, 90, AttackEffect.POISON, 3, 2),
            new Attack("Stunning Slam", "The monster crashes into you and tries to stun you.",
                    8, 80, AttackEffect.STUN, 0, 1),
            new Attack("Weakening Roar", "The monster lets out a roar that weakens your next strikes.",
                    7, 85, AttackEffect.WEAKEN, 3, 2));

    private int monsterHealth = MONSTER_MAX_HEALTH;
    private int monsterPoisonDamage;
    private int monsterPoisonTurns;
    private int monsterStunTurns;
    private int monsterWeakenPower;
    private int monsterWeakenTurns;

    private int playerPoisonDamage;
    private int playerPoisonTurns;
    private int playerStunTurns;
    private int playerWeakenPower;
    private int playerWeakenTurns;

    public int getMonsterHealth() {
        return monsterHealth;
    }

    public int getMonsterMaxHealth() {
        return MONSTER_MAX_HEALTH;
    }

    public boolean isDefeated() {
        return monsterHealth <= 0;
    }

    public List<Attack> getMonsterAttacks() {
        return MONSTER_ATTACKS;
    }

    /** Resolves one player attack and, when appropriate, the monster's response. */
    public List<String> playerAttack(CustomPlayer player, Attack attack) {
        List<String> events = new ArrayList<>();
        if (player == null || attack == null || isDefeated() || !player.isAlive()) {
            events.add("The battle cannot continue.");
            return events;
        }

        if (!applyPlayerTurnStart(player, events)) {
            finishIfNeeded(player, events);
            return events;
        }

        if (playerStunTurns > 0) {
            playerStunTurns--;
            events.add("You are stunned and lose your turn!");
            takeMonsterTurn(player, events);
            finishIfNeeded(player, events);
            return events;
        }

        int damage = attack.getDamage();
        if (playerWeakenTurns > 0) {
            damage = Math.max(0, damage - playerWeakenPower);
            playerWeakenTurns--;
            if (playerWeakenTurns == 0) {
                playerWeakenPower = 0;
            }
        }

        if (rollHits(attack.getAccuracy())) {
            monsterHealth = Math.max(0, monsterHealth - damage);
            events.add("Your " + attack.getName() + " hits for " + damage + " damage.");
            if (!isDefeated()) {
                applyEffectToMonster(attack, events);
            }
        } else {
            events.add("Your " + attack.getName() + " misses.");
        }

        if (!isDefeated()) {
            takeMonsterTurn(player, events);
        }
        finishIfNeeded(player, events);
        return events;
    }

    /**
     * Uses one inventory health item as the player's action. An invalid use
     * does not consume the turn or cause a monster response.
     */
    public List<String> useHealthItem(CustomPlayer player, String itemName) {
        List<String> events = new ArrayList<>();
        if (player == null || itemName == null || itemName.isBlank()
                || isDefeated() || !player.isAlive()) {
            events.add("That item cannot be used right now.");
            return events;
        }

        if (!Utilities.isItemInContainer(itemName, player.getInventory())) {
            events.add("You do not have " + itemName + " in your inventory.");
            return events;
        }
        if (HealthItemCatalog.find(itemName) == null) {
            events.add(itemName + " cannot be used to restore health.");
            return events;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            events.add("Your health is already full. Save that item for later.");
            return events;
        }

        // Poison ticks at the start of the player's turn, before the item effect.
        if (!applyPlayerTurnStart(player, events)) {
            finishIfNeeded(player, events);
            return events;
        }
        if (playerStunTurns > 0) {
            playerStunTurns--;
            events.add("You are stunned and cannot use an item this turn!");
            takeMonsterTurn(player, events);
            finishIfNeeded(player, events);
            return events;
        }

        int before = player.getHealth();
        if (!player.useHealthItemQuietly(itemName)) {
            events.add("You could not use " + itemName + ".");
            return events;
        }
        events.add("You used " + itemName + " and restored "
                + (player.getHealth() - before) + " health point(s).");

        if (!isDefeated() && player.isAlive()) {
            takeMonsterTurn(player, events);
        }
        finishIfNeeded(player, events);
        return events;
    }

    private boolean applyPlayerTurnStart(CustomPlayer player, List<String> events) {
        if (playerPoisonTurns > 0) {
            player.takeDamage(playerPoisonDamage);
            playerPoisonTurns--;
            events.add("Poison deals " + playerPoisonDamage + " damage to you.");
            if (playerPoisonTurns == 0) {
                playerPoisonDamage = 0;
            }
        }
        return player.isAlive();
    }

    private void takeMonsterTurn(CustomPlayer player, List<String> events) {
        if (!player.isAlive() || isDefeated()) {
            return;
        }

        if (monsterPoisonTurns > 0) {
            monsterHealth = Math.max(0, monsterHealth - monsterPoisonDamage);
            monsterPoisonTurns--;
            events.add("Poison deals " + monsterPoisonDamage + " damage to the monster.");
            if (monsterPoisonTurns == 0) {
                monsterPoisonDamage = 0;
            }
        }
        if (isDefeated()) {
            return;
        }

        if (monsterStunTurns > 0) {
            monsterStunTurns--;
            events.add("The monster is stunned and cannot attack.");
            return;
        }

        Attack monsterAttack = MONSTER_ATTACKS.get(
                ThreadLocalRandom.current().nextInt(MONSTER_ATTACKS.size()));
        int damage = monsterAttack.getDamage();
        if (monsterWeakenTurns > 0) {
            damage = Math.max(0, damage - monsterWeakenPower);
            monsterWeakenTurns--;
            if (monsterWeakenTurns == 0) {
                monsterWeakenPower = 0;
            }
        }

        if (rollHits(monsterAttack.getAccuracy())) {
            player.takeDamage(damage);
            events.add(monsterAttack.getDescription());
            events.add("The monster uses " + monsterAttack.getName()
                    + " for " + damage + " damage.");
            applyEffectToPlayer(monsterAttack, events);
        } else {
            events.add("The monster's " + monsterAttack.getName() + " misses.");
        }
    }

    private void applyEffectToMonster(Attack attack, List<String> events) {
        switch (attack.getEffectType()) {
            case NONE:
                break;
            case POISON:
                monsterPoisonDamage = attack.getEffectPower();
                monsterPoisonTurns = attack.getEffectDuration();
                events.add("The monster is poisoned.");
                break;
            case STUN:
                monsterStunTurns = attack.getEffectDuration();
                events.add("The monster is stunned.");
                break;
            case WEAKEN:
                monsterWeakenPower = attack.getEffectPower();
                monsterWeakenTurns = attack.getEffectDuration();
                events.add("The monster is weakened.");
                break;
            default:
                throw new IllegalStateException("Unsupported attack effect: " + attack.getEffectType());
        }
    }

    private void applyEffectToPlayer(Attack attack, List<String> events) {
        switch (attack.getEffectType()) {
            case NONE:
                break;
            case POISON:
                playerPoisonDamage = attack.getEffectPower();
                playerPoisonTurns = attack.getEffectDuration();
                events.add("You are poisoned for " + playerPoisonTurns + " of your turns.");
                break;
            case STUN:
                playerStunTurns = attack.getEffectDuration();
                events.add("You are stunned and may lose your next turn.");
                break;
            case WEAKEN:
                playerWeakenPower = attack.getEffectPower();
                playerWeakenTurns = attack.getEffectDuration();
                events.add("Your attacks are weakened for " + playerWeakenTurns + " turns.");
                break;
            default:
                throw new IllegalStateException("Unsupported attack effect: " + attack.getEffectType());
        }
    }

    private boolean rollHits(int accuracy) {
        return ThreadLocalRandom.current().nextInt(100) < accuracy;
    }

    private void finishIfNeeded(CustomPlayer player, List<String> events) {
        if (isDefeated()) {
            events.add("You defeated the monster!");
        } else if (!player.isAlive()) {
            events.add("You have been defeated.");
        }
    }
}
