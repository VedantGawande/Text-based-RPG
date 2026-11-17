import java.util.List;

/** A room that starts and owns one persistent monster encounter. */
public class MonsterRoom extends Room {

    private final MonsterBattle battle = new MonsterBattle();

    public MonsterRoom(String roomName, String objects, int roomNum, String listOfExits) {
        super(roomName, objects, roomNum, listOfExits);
    }

    @Override
    public int playRoom(CustomPlayer player) {
        if (!battle.isDefeated()) {
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

    /** Retained for compatibility with code holding only a Player reference. */
    @Override
    public int playRoom(Player player) {
        return getNextRoom(player);
    }

    public int getMonsterHealth() {
        return battle.getMonsterHealth();
    }

    public int getMonsterMaxHealth() {
        return battle.getMonsterMaxHealth();
    }

    public boolean isMonsterDefeated() {
        return battle.isDefeated();
    }

    /** Used by the GUI and any future interface to resolve a player move. */
    public List<String> performPlayerAttack(CustomPlayer player, int attackIndex) {
        if (attackIndex < 0 || attackIndex >= player.getAttackSet().size()) {
            return List.of("Choose a valid attack.");
        }
        return battle.playerAttack(player, player.getAttackSet().getAttack(attackIndex));
    }

    /** Used by the GUI and CLI battle to consume a healing item during combat. */
    public List<String> useItemDuringBattle(CustomPlayer player, String itemName) {
        return battle.useHealthItem(player, itemName);
    }

    private void playBattle(CustomPlayer player) {
        System.out.println("A monster blocks your way!");
        System.out.println("Monster health: " + battle.getMonsterHealth()
                + "/" + battle.getMonsterMaxHealth());

        while (player.isAlive() && !battle.isDefeated() && player.getContinuePlay()) {
            System.out.println();
            System.out.println(player.printHealthPoints());
            System.out.println("Monster health: " + battle.getMonsterHealth()
                    + "/" + battle.getMonsterMaxHealth());

            List<Attack> attacks = player.getAttackSet().getAttacks();
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
                printEvents(useItemDuringBattle(player, command.substring(4)));
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
            printEvents(performPlayerAttack(player, attackIndex));
        }

        if (!player.isAlive()) {
            player.setContinuePlay(false);
        }
    }

    private void printEvents(List<String> events) {
        for (String event : events) {
            System.out.println(event);
        }
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

}
