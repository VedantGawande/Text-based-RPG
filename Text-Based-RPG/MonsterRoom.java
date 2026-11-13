import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MonsterRoom extends Room {

    private static final int MONSTER_MAX_HEALTH = 40;
    private static final int MONSTER_DAMAGE = 8;

    private int monsterHealth = MONSTER_MAX_HEALTH;
    private boolean monsterDefeated;

    public MonsterRoom(String roomName, String objects, int roomNum, String listOfExits) {
        super(roomName, objects, roomNum, listOfExits);
    }

    @Override
    public int playRoom(CustomPlayer player) {
        if (!monsterDefeated) {
            if (!player.isAlive()) {
                System.out.println("You are too weak to fight.");
                player.setContinuePlay(false);
                return getRoomNum();
            }
            playBattle(player);
        }

        if (!player.getContinuePlay() || !player.isAlive()) {
            return getRoomNum();
        }
        return getNextRoom(player);
    }

    @Override
    public int playRoom(Player player) {
        return getNextRoom(player);
    }

    private void playBattle(CustomPlayer player) {
        int poisonDamage = 0;
        int poisonTurns = 0;
        int stunTurns = 0;
        int weakenPower = 0;
        int weakenTurns = 0;
        List<Attack> attacks = player.getAttackSet().getAttacks();

        System.out.println("A monster blocks your way!");
        System.out.println("Monster health: " + monsterHealth + "/" + MONSTER_MAX_HEALTH);

        while (player.isAlive() && monsterHealth > 0 && player.getContinuePlay()) {
            System.out.println();
            System.out.println(player.printHealthPoints());
            System.out.println("Monster health: " + monsterHealth + "/" + MONSTER_MAX_HEALTH);
            for (int i = 0; i < attacks.size(); i++) {
                Attack attack = attacks.get(i);
                System.out.println("  " + (i + 1) + ". " + attack.getName()
                        + " - " + attack.getDamage() + " damage, "
                        + attack.getAccuracy() + "% accuracy");
            }
            System.out.println("Choose an attack number, inventory, use <item>, or quit:");

            String command = GameInput.readLine();
            if (command == null) {
                player.setContinuePlay(false);
                break;
            }

            command = command.trim();
            if (command.equalsIgnoreCase("quit")) {
                player.setContinuePlay(false);
                break;
            }
            if (command.equalsIgnoreCase("inventory")) {
                player.printInventory();
                continue;
            }
            if (command.regionMatches(true, 0, "use ", 0, 4)) {
                if (player.useHealthItem(command.substring(4))) {
                    int[] statuses = takeMonsterTurn(
                            player, poisonDamage, poisonTurns, stunTurns,
                            weakenPower, weakenTurns);
                    poisonTurns = statuses[0];
                    stunTurns = statuses[1];
                    weakenTurns = statuses[2];
                }
                continue;
            }
            if (command.equalsIgnoreCase("use")) {
                System.out.println("Use an item with 'use <item>'.");
                continue;
            }

            int attackIndex = findAttackIndex(command, attacks);
            if (attackIndex < 0) {
                System.out.println("Choose a valid attack number or name.");
                continue;
            }

            Attack attack = attacks.get(attackIndex);
            if (ThreadLocalRandom.current().nextInt(100) < attack.getAccuracy()) {
                monsterHealth = Math.max(0, monsterHealth - attack.getDamage());
                System.out.println("Your " + attack.getName() + " hits for "
                        + attack.getDamage() + " damage.");
                if (monsterHealth > 0) {
                    switch (attack.getEffectType()) {
                        case POISON:
                            poisonDamage = attack.getEffectPower();
                            poisonTurns = attack.getEffectDuration();
                            System.out.println("The monster is poisoned.");
                            break;
                        case STUN:
                            stunTurns = attack.getEffectDuration();
                            System.out.println("The monster is stunned.");
                            break;
                        case WEAKEN:
                            weakenPower = attack.getEffectPower();
                            weakenTurns = attack.getEffectDuration();
                            System.out.println("The monster is weakened.");
                            break;
                        case NONE:
                            break;
                        default:
                            throw new IllegalStateException(
                                    "Unsupported attack effect: " + attack.getEffectType());
                    }
                }
            } else {
                System.out.println("Your " + attack.getName() + " misses.");
            }

            if (monsterHealth > 0) {
                int[] statuses = takeMonsterTurn(
                        player, poisonDamage, poisonTurns, stunTurns,
                        weakenPower, weakenTurns);
                poisonTurns = statuses[0];
                stunTurns = statuses[1];
                weakenTurns = statuses[2];
            }
        }

        if (monsterHealth <= 0) {
            monsterDefeated = true;
            System.out.println("You defeated the monster!");
        } else if (!player.isAlive()) {
            System.out.println("You have been defeated.");
            player.setContinuePlay(false);
        }
    }

    private int[] takeMonsterTurn(CustomPlayer player, int poisonDamage, int poisonTurns,
                                  int stunTurns, int weakenPower, int weakenTurns) {
        if (poisonTurns > 0) {
            monsterHealth = Math.max(0, monsterHealth - poisonDamage);
            poisonTurns--;
            System.out.println("Poison deals " + poisonDamage + " damage to the monster.");
        }
        if (monsterHealth <= 0) {
            return new int[] {poisonTurns, stunTurns, weakenTurns};
        }

        int damage = Math.max(0, MONSTER_DAMAGE - (weakenTurns > 0 ? weakenPower : 0));
        if (weakenTurns > 0) {
            weakenTurns--;
        }
        if (stunTurns > 0) {
            stunTurns--;
            System.out.println("The monster is stunned and cannot attack.");
            return new int[] {poisonTurns, stunTurns, weakenTurns};
        }

        player.takeDamage(damage);
        System.out.println("The monster attacks for " + damage + " damage.");
        return new int[] {poisonTurns, stunTurns, weakenTurns};
    }

    private int findAttackIndex(String command, List<Attack> attacks) {
        try {
            int index = Integer.parseInt(command) - 1;
            return index >= 0 && index < attacks.size() ? index : -1;
        } catch (NumberFormatException ignored) {
            for (int i = 0; i < attacks.size(); i++) {
                if (attacks.get(i).getName().equalsIgnoreCase(command)) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    protected void userPicksUp(Player player) {
        System.out.println("Is there something interesting you'd like to take?");
        String itemToPickUp = GameInput.readLine();
        if (itemToPickUp == null) {
            player.setContinuePlay(false);
            return;
        }
        itemToPickUp = itemToPickUp.trim();

        if (Utilities.isItemInContainer(itemToPickUp, roomObjects)) {
            player.addToInventory(itemToPickUp);
            roomObjects = Utilities.removeFromList(itemToPickUp, roomObjects);
            System.out.println("Item stolen!");
        } else {
            System.out.println("Item was not stolen.");
        }
    }

    @Override
    protected void userDrops(Player player) {
        System.out.println("What item would you like to drop?");
        String itemToDrop = GameInput.readLine();
        if (itemToDrop == null) {
            player.setContinuePlay(false);
            return;
        }
        itemToDrop = itemToDrop.trim();

        if (Utilities.isItemInContainer(itemToDrop, player.getInventory())) {
            player.removeFromInventory(itemToDrop);
            roomObjects = Utilities.addToString(itemToDrop, roomObjects);
            System.out.println("Item dropped.");
        } else {
            System.out.println("You do not have that item.");
        }
    }
}
