# Implementation notes — Text-Based RPG

## Changes in this copy

### Player health
- `healthPoints` is now an instance field, not a static field. Each `CustomPlayer` owns its own health.
- `getHealth()`, `getMaxHealth()`, `takeDamage(int)`, `heal(int)`, and `isAlive()` form the health API for the combat and item developers.
- Damage cannot reduce health below zero. Healing cannot exceed maximum health.
- The existing four-argument `CustomPlayer` constructor is preserved. It treats the supplied starting health as the maximum health too. When the team decides on a different maximum, use the five-argument constructor, for example `new CustomPlayer("Player", 0, "", 50, 100)`.
- `addHealthPoint`, `removeHealthPoint`, `resetHealthPoints`, and `printHealthPoints` are now instance methods with working behavior. The sleep room uses `player.heal(1)`.


### Health items
- Added `HealthItem.java`, an immutable definition containing an item's display name, description, and healing amount.
- Added `HealthItemCatalog.java`, which maps inventory item names (case-insensitively) to health-item definitions. It includes Apple (+10), Pumpkin (+12), Pizza (+18), Milk (+8), Candy Bar (+8), Steak (+20), Coffee (+5), and Health Potion (+35). The room's existing `1 apple` label is registered as an alias for Apple.
- Added `CustomPlayer.useHealthItem(String)`. It checks that the player owns the item, recognizes it as a health item, and is not already defeated or at full health. It then restores health up to the maximum and consumes exactly one copy. Failed use attempts do not consume the item.
- Added `use` and `use <item>` commands to normal rooms and the Sleep Room. The command is available in `MonsterRoom` through its normal room interaction flow.
- Added a Health Potion to the Kitchen in `GameVersionFinal`. This entry point now starts the player at 50/100 HP so food and potion healing can be observed and tested. The four-argument `CustomPlayer` constructor remains backward compatible.
- `HealthItemCatalog.register(new HealthItem(...), "alias")` is the extension point for adding more consumable food or potion items.
- Inventory item matching/removal is now case-insensitive, while still removing only one matching occurrence. This makes item commands more forgiving without changing the semicolon-separated inventory format.

### Console input
- `GameInput` owns the only `Scanner` for `System.in`.
- `Room`, `MonsterRoom`, and `SleepRoom` use `GameInput.readLine()` instead of creating their own scanners.
- The shared scanner is deliberately not closed by room classes; closing it would close `System.in` for the entire game.
- End-of-input is handled by stopping the game instead of throwing an exception.

### Related correctness fixes
- `MonsterRoom` now overrides `playRoom(CustomPlayer)`. This matters because `GameVersionFinal` passes a `CustomPlayer` through a `Room[]`; the old `playRoom(Player)` overload was not selected in that situation.
- `MonsterRoom.playRoom(CustomPlayer)` starts the existing turn-based monster battle before returning to normal room interaction after victory.
- `Utilities.removeFromList` removes only the first matching inventory item, so duplicate items can be held and used individually.
- Item list appending avoids adding an unnecessary separator when the list is empty or already ends in `;`.
- Room exit parsing handles malformed entries more safely.
- The Sleep Room help command now prints the sleep-specific help text.
- `roomObjects` is `protected` rather than `public`, allowing specialized rooms to access it without exposing it to every class.

### Player attacks
- Added `Attack.java`, `AttackEffect.java`, and `PlayerAttackSet.java`.
- Each `CustomPlayer` owns a `PlayerAttackSet` accessed through `getAttackSet()`.
- `Attack` is immutable and describes a move; the combat controller is responsible for hit rolls, applying damage, and applying status effects.
- `PlayerAttackSet.getAttacks()` returns a read-only list view. Add or unlock moves through `addAttack()`.
- `MonsterRoom.playRoom(CustomPlayer)` now runs a turn-based battle before normal room interaction. The monster has 40 health and cycles through several different attack patterns, including direct attacks and status-based strikes. Players can choose an attack by number or name, or use a health item during their turn. Attack accuracy and damage are honored; poison, stun, and weaken apply for their configured duration. A defeated monster stays defeated while the room instance remains in use.

## Integration contracts

The combat controller can use:

```java
PlayerAttackSet moves = player.getAttackSet();
Attack move = moves.getAttack(choice - 1);
int damage = move.getDamage();
int accuracy = move.getAccuracy();
AttackEffect effect = move.getEffectType();

monster.takeDamage(damage); // Monster should expose an equivalent health API.
player.takeDamage(damage);
player.heal(healingAmount);
player.isAlive();
```

`AttackEffect` values currently are `NONE`, `STUN`, `POISON`, and `WEAKEN`. The combat team should agree on duration semantics (recommended: duration counts the affected character's turns) before implementing status effects.

## Build and run

From this directory:

```bash
javac *.java
java GameVersionFinal
```

Use `GameVersionFinal` as the project entry point.

## Verification performed

- All Java source files compile cleanly with `javac -Xlint:all`.
- `GameVersionFinal` accepts multiple consecutive commands in one input stream.
- Smoke tests verified independent player health, damage/healing bounds, death state, health reset, duplicate-item removal, access to the four default player attacks, read-only attack listing, and the `MonsterRoom.playRoom(CustomPlayer)` overload.
- Health-item tests verified successful use of food and potion items, health capping at maximum, item consumption, case-insensitive item names, duplicate item handling, refusal to consume at full health, and refusal to use non-health inventory objects.
- The source tree contains exactly one `new Scanner(System.in)`, in `GameInput.java`.


## OOP requirements: abstract class and interface

### Abstract class — `Room`
- `Room` is now declared `abstract` and declares `playRoom(CustomPlayer)` as an abstract method.
- Shared state and common room behavior remain in `Room`: room name/number/exits, objects, navigation, inventory actions, and the normal command loop.
- Added `StandardRoom`, which implements `playRoom(CustomPlayer)` by delegating to `getNextRoom(player)`.
- `MonsterRoom` and `SleepRoom` already provide their own `playRoom(CustomPlayer)` implementations and continue to extend `Room`.
- `GameVersionFinal` still stores rooms in a `Room[]`, but ordinary rooms are now instantiated as `StandardRoom`. The room array and method call use polymorphism to dispatch to the correct room implementation.

### Interface — `Consumable`
- Added `Consumable`, which defines `getName()`, `getDescription()`, and `consume(CustomPlayer)`.
- `HealthItem` implements `Consumable`; its `consume` method applies the item's healing effect through `CustomPlayer.heal()` and reports whether it could be consumed.
- `CustomPlayer.useHealthItem` now refers to the found item through the `Consumable` interface when applying the effect. Inventory ownership checks, full-health/dead-player checks, inventory removal, and messages remain in `CustomPlayer`.
- This keeps the current food and potion behavior while allowing future consumable item classes to implement the same interface.

### Behavior-preservation checks
- Normal-room commands (`look`, `inventory`, and `quit`) were smoke-tested through `GameVersionFinal`.
- The Monster Room entry and existing turn-based attack flow were smoke-tested.
- A structural test verified that `Room` is abstract, all three concrete room types extend it, `HealthItem` implements `Consumable`, and healing through the interface works.
