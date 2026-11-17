import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/** Swing graphical interface for the same game world used by GameVersionFinal. */
public class GameGUI {

    private static final Color BACKGROUND = new Color(20, 24, 34);
    private static final Color PANEL = new Color(31, 38, 52);
    private static final Color PANEL_LIGHT = new Color(42, 51, 69);
    private static final Color TEXT = new Color(235, 239, 247);
    private static final Color MUTED = new Color(166, 177, 196);
    private static final Color ACCENT = new Color(110, 168, 255);
    private static final Color GOOD = new Color(91, 196, 139);
    private static final Color DANGER = new Color(235, 104, 112);
    private static final Color BUTTON_TEXT = new Color(250, 252, 255);
    private static final Color DISABLED_BUTTON_TEXT = new Color(205, 216, 232);
    private static final Color DISABLED_BUTTON_BACKGROUND = new Color(53, 63, 80);

    private JFrame frame;
    private JLabel roomTitle;
    private JLabel roomSubtitle;
    private JLabel healthText;
    private JLabel monsterHealthText;
    private JProgressBar healthBar;
    private JProgressBar monsterHealthBar;
    private JTextArea roomDescription;
    private JTextArea eventLog;
    private DefaultListModel<String> roomItemsModel;
    private DefaultListModel<String> inventoryModel;
    private JList<String> roomItemsList;
    private JList<String> inventoryList;
    private JPanel navigationPanel;
    private JPanel roomActionsPanel;
    private JPanel battlePanel;
    private JPanel attackButtonsPanel;
    private JComboBox<String> battleItemCombo;
    private JButton useBattleItemButton;
    private JButton restButton;
    private JButton pickUpButton;
    private JButton dropButton;
    private JButton useItemButton;
    private JButton restartButton;

    private Room[] gameBoard;
    private CustomPlayer player;
    private int currentRoomNumber;
    private boolean gameOver;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            new GameGUI().show();
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing's default look and feel is an acceptable fallback.
        }

        // Keep disabled button labels readable on every supported look and feel.
        UIManager.put("Button.disabledText", DISABLED_BUTTON_TEXT);
    }

    private GameGUI() {
        startNewGame();
        buildWindow();
        refreshView();
        appendLog("Welcome, " + player.getName() + ". Explore the rooms, collect supplies, and survive the nightmare.");
        appendLog("Tip: collect food in the Kitchen and visit the Sleep Room to recover health.");
    }

    private void startNewGame() {
        gameBoard = GameVersionFinal.createGameBoard();
        player = GameVersionFinal.createPlayer();
        currentRoomNumber = 0;
        gameOver = false;
    }

    private void show() {
        frame.setVisible(true);
    }

    private void buildWindow() {
        frame = new JFrame("Text-Based RPG | Nightmare House");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(980, 700));
        frame.setSize(1180, 790);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);
        frame.setLayout(new BorderLayout(12, 12));

        frame.add(buildHeader(), BorderLayout.NORTH);
        frame.add(buildMainContent(), BorderLayout.CENTER);
        frame.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 8));
        header.setBackground(PANEL);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBox = new JPanel(new BorderLayout(4, 4));
        titleBox.setOpaque(false);
        JLabel gameTitle = new JLabel("NIGHTMARE HOUSE");
        gameTitle.setForeground(TEXT);
        gameTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        JLabel gameSubTitle = new JLabel("A small room-exploration RPG");
        gameSubTitle.setForeground(MUTED);
        gameSubTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        titleBox.add(gameTitle, BorderLayout.NORTH);
        titleBox.add(gameSubTitle, BorderLayout.SOUTH);
        header.add(titleBox, BorderLayout.WEST);

        JPanel healthBox = new JPanel(new BorderLayout(8, 5));
        healthBox.setOpaque(false);
        healthText = new JLabel();
        healthText.setForeground(TEXT);
        healthText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        healthBar = new JProgressBar(0, player.getMaxHealth());
        healthBar.setStringPainted(false);
        healthBar.setPreferredSize(new Dimension(270, 16));
        healthBar.setForeground(GOOD);
        healthBar.setBackground(PANEL_LIGHT);
        healthBox.add(healthText, BorderLayout.NORTH);
        healthBox.add(healthBar, BorderLayout.SOUTH);
        header.add(healthBox, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildMainContent() {
        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setOpaque(false);
        left.add(buildRoomPanel(), BorderLayout.CENTER);
        left.add(buildActionPanel(), BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.add(buildInventoryPanel(), BorderLayout.NORTH);
        right.add(buildLogPanel(), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.65);
        split.setBorder(null);
        split.setOpaque(false);
        split.setDividerSize(8);
        return split;
    }

    private JPanel buildRoomPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JPanel heading = new JPanel(new BorderLayout(4, 4));
        heading.setOpaque(false);
        roomTitle = new JLabel("Room");
        roomTitle.setForeground(TEXT);
        roomTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
        roomSubtitle = new JLabel("Room description");
        roomSubtitle.setForeground(MUTED);
        roomSubtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        heading.add(roomTitle, BorderLayout.NORTH);
        heading.add(roomSubtitle, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);

        roomDescription = new JTextArea(5, 25);
        roomDescription.setEditable(false);
        roomDescription.setLineWrap(true);
        roomDescription.setWrapStyleWord(true);
        roomDescription.setForeground(TEXT);
        roomDescription.setBackground(PANEL_LIGHT);
        roomDescription.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        roomDescription.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel objectsPanel = new JPanel(new BorderLayout(6, 6));
        objectsPanel.setOpaque(false);
        objectsPanel.add(sectionLabel("OBJECTS IN THIS ROOM"), BorderLayout.NORTH);
        roomItemsModel = new DefaultListModel<>();
        roomItemsList = new JList<>(roomItemsModel);
        styleList(roomItemsList);
        JScrollPane scroll = new JScrollPane(roomItemsList);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_LIGHT));
        objectsPanel.add(scroll, BorderLayout.CENTER);
        objectsPanel.setPreferredSize(new Dimension(220, 190));

        JPanel roomMiddle = new JPanel(new BorderLayout(10, 10));
        roomMiddle.setOpaque(false);
        roomMiddle.add(roomDescription, BorderLayout.NORTH);
        roomMiddle.add(objectsPanel, BorderLayout.CENTER);
        panel.add(roomMiddle, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);
        wrapper.add(buildNavigationPanel(), BorderLayout.WEST);

        JPanel rightActions = new JPanel(new BorderLayout(8, 8));
        rightActions.setOpaque(false);
        roomActionsPanel = buildRoomActionsPanel();
        battlePanel = buildBattlePanel();
        rightActions.add(roomActionsPanel, BorderLayout.CENTER);
        rightActions.add(battlePanel, BorderLayout.SOUTH);
        wrapper.add(rightActions, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildNavigationPanel() {
        navigationPanel = cardPanel();
        navigationPanel.setLayout(new BorderLayout(6, 6));
        navigationPanel.add(sectionLabel("MOVE"), BorderLayout.NORTH);

        JPanel directions = new JPanel(new GridLayout(2, 3, 5, 5));
        directions.setOpaque(false);
        JButton north = makeButton("↑ North", ACCENT);
        JButton west = makeButton("← West", PANEL_LIGHT);
        JButton east = makeButton("East →", PANEL_LIGHT);
        JButton south = makeButton("↓ South", ACCENT);
        directions.add(new JLabel());
        directions.add(north);
        directions.add(new JLabel());
        directions.add(west);
        directions.add(south);
        directions.add(east);
        north.addActionListener(event -> {
            if (isNavigationActionEnabled(north)) move("north");
        });
        south.addActionListener(event -> {
            if (isNavigationActionEnabled(south)) move("south");
        });
        east.addActionListener(event -> {
            if (isNavigationActionEnabled(east)) move("east");
        });
        west.addActionListener(event -> {
            if (isNavigationActionEnabled(west)) move("west");
        });
        directions.setPreferredSize(new Dimension(290, 88));
        navigationPanel.add(directions, BorderLayout.CENTER);
        return navigationPanel;
    }

    private JPanel buildRoomActionsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new GridLayout(0, 2, 7, 7));
        pickUpButton = makeButton("Pick up selected", PANEL_LIGHT);
        dropButton = makeButton("Drop selected", PANEL_LIGHT);
        useItemButton = makeButton("Use health item", PANEL_LIGHT);
        restButton = makeButton("Rest", GOOD);
        pickUpButton.addActionListener(event -> pickUpSelectedItem());
        dropButton.addActionListener(event -> dropSelectedItem());
        useItemButton.addActionListener(event -> useHealthItemOutsideCombat());
        restButton.addActionListener(event -> rest());
        panel.add(pickUpButton);
        panel.add(dropButton);
        panel.add(useItemButton);
        panel.add(restButton);
        return panel;
    }

    private JPanel buildBattlePanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout(6, 5));
        top.setOpaque(false);
        JLabel monsterTitle = new JLabel("THE MONSTER");
        monsterTitle.setForeground(DANGER);
        monsterTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        monsterHealthText = new JLabel();
        monsterHealthText.setForeground(TEXT);
        monsterHealthBar = new JProgressBar();
        monsterHealthBar.setStringPainted(false);
        monsterHealthBar.setForeground(DANGER);
        monsterHealthBar.setBackground(PANEL_LIGHT);
        top.add(monsterTitle, BorderLayout.WEST);
        top.add(monsterHealthText, BorderLayout.EAST);
        top.add(monsterHealthBar, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        attackButtonsPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        attackButtonsPanel.setOpaque(false);
        panel.add(attackButtonsPanel, BorderLayout.CENTER);

        JPanel itemUse = new JPanel(new BorderLayout(6, 0));
        itemUse.setOpaque(false);
        battleItemCombo = new JComboBox<>();
        battleItemCombo.setBackground(PANEL_LIGHT);
        battleItemCombo.setForeground(TEXT);
        useBattleItemButton = makeButton("Use item", GOOD);
        useBattleItemButton.addActionListener(event -> useHealthItemInCombat());
        itemUse.add(battleItemCombo, BorderLayout.CENTER);
        itemUse.add(useBattleItemButton, BorderLayout.EAST);
        panel.add(itemUse, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInventoryPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(6, 6));
        panel.add(sectionLabel("YOUR INVENTORY"), BorderLayout.NORTH);
        inventoryModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryModel);
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList(inventoryList);
        inventoryList.setVisibleRowCount(5);
        JScrollPane scroll = new JScrollPane(inventoryList);
        scroll.setPreferredSize(new Dimension(270, 135));
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(6, 6));
        panel.add(sectionLabel("ADVENTURE LOG"), BorderLayout.NORTH);
        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setLineWrap(true);
        eventLog.setWrapStyleWord(true);
        eventLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        eventLog.setForeground(TEXT);
        eventLog.setBackground(PANEL_LIGHT);
        eventLog.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(eventLog);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(8, 0));
        footer.setBackground(BACKGROUND);
        footer.setBorder(new EmptyBorder(0, 12, 12, 12));
        JLabel hint = new JLabel("Explore · collect items · manage health · defeat the monster");
        hint.setForeground(MUTED);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        restartButton = makeButton("Restart game", PANEL_LIGHT);
        restartButton.addActionListener(event -> restartGame());
        footer.add(hint, BorderLayout.WEST);
        footer.add(restartButton, BorderLayout.EAST);
        return footer;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        return label;
    }

    private JButton makeButton(String text, Color background) {
        JButton button = new JButton(text);

        // Use Swing's basic button delegate instead of native button rendering.
        // Some operating-system look and feels ignore custom foreground colours,
        // which made navigation and attack labels difficult to read.
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setBackground(background);
        button.setForeground(BUTTON_TEXT);
        button.putClientProperty("rpg.baseBackground", background);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setHorizontalAlignment(JButton.CENTER);
        button.setMargin(new Insets(9, 10, 9, 10));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 89, 111)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return button;
    }

    private void styleList(JList<String> list) {
        list.setBackground(PANEL_LIGHT);
        list.setForeground(TEXT);
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        list.setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    private void refreshView() {
        if (frame == null) {
            return;
        }

        Room room = gameBoard[currentRoomNumber];
        roomTitle.setText(room.getRoomName());
        roomSubtitle.setText("Room " + (currentRoomNumber + 1) + " of " + gameBoard.length);
        roomDescription.setText(roomDescriptionFor(room));

        roomItemsModel.clear();
        for (String item : room.getRoomItems()) {
            roomItemsModel.addElement(item);
        }
        inventoryModel.clear();
        for (String item : player.getInventoryItems()) {
            inventoryModel.addElement(item);
        }

        healthText.setText("HEALTH  " + player.getHealth() + " / " + player.getMaxHealth());
        healthBar.setMaximum(player.getMaxHealth());
        healthBar.setValue(player.getHealth());
        healthBar.setForeground(player.getHealth() <= 25 ? DANGER : GOOD);

        Map<String, Integer> exits = room.getAvailableExits();
        for (java.awt.Component component : navigationPanel.getComponents()) {
            if (component instanceof JPanel) {
                for (java.awt.Component child : ((JPanel) component).getComponents()) {
                    if (child instanceof JButton) {
                        JButton button = (JButton) child;
                        String text = button.getText().toLowerCase();
                        if (text.contains("north")) setNavigationButtonState(button, exits.containsKey("north"));
                        if (text.contains("south")) setNavigationButtonState(button, exits.containsKey("south"));
                        if (text.contains("east")) setNavigationButtonState(button, exits.containsKey("east"));
                        if (text.contains("west")) setNavigationButtonState(button, exits.containsKey("west"));
                    }
                }
            }
        }

        boolean inMonsterBattle = room instanceof MonsterRoom
                && !((MonsterRoom) room).isMonsterDefeated() && player.isAlive();
        battlePanel.setVisible(inMonsterBattle);
        roomActionsPanel.setVisible(!inMonsterBattle && player.isAlive());
        navigationPanel.setEnabled(!inMonsterBattle && player.isAlive());
        setNavigationButtonsEnabled(!inMonsterBattle && player.isAlive());

        if (room instanceof MonsterRoom) {
            MonsterRoom monsterRoom = (MonsterRoom) room;
            monsterHealthText.setText(monsterRoom.getMonsterHealth()
                    + " / " + monsterRoom.getMonsterMaxHealth() + " HP");
            monsterHealthBar.setMaximum(monsterRoom.getMonsterMaxHealth());
            monsterHealthBar.setValue(monsterRoom.getMonsterHealth());
            rebuildAttackButtons(monsterRoom);
            refreshBattleItems();
        }

        boolean isSleepRoom = room instanceof SleepRoom;
        restButton.setVisible(isSleepRoom && !inMonsterBattle && player.isAlive());
        pickUpButton.setEnabled(!room.getRoomItems().isEmpty());
        dropButton.setEnabled(!player.getInventoryItems().isEmpty());
        useItemButton.setEnabled(hasUsableHealthItem());

        if (!player.isAlive()) {
            gameOver = true;
            roomSubtitle.setText("GAME OVER — you have been defeated");
            appendLog("GAME OVER. Restart to play again.");
        }

        frame.revalidate();
        frame.repaint();
    }

    private void setNavigationButtonsEnabled(boolean enabled) {
        for (java.awt.Component component : navigationPanel.getComponents()) {
            if (component instanceof JPanel) {
                for (java.awt.Component child : ((JPanel) component).getComponents()) {
                    if (child instanceof JButton) {
                        JButton button = (JButton) child;
                        String text = button.getText().toLowerCase();
                        if (text.contains("north") || text.contains("south")
                                || text.contains("east") || text.contains("west")) {
                            setNavigationButtonState(button, enabled && directionExists(text));
                        }
                    }
                }
            }
        }
    }


    private void setNavigationButtonState(JButton button, boolean enabled) {
        // Keep the Swing button enabled so the look and feel does not replace
        // the label with its low-contrast native disabled text colour. Movement
        // is still blocked by the action guard in each navigation listener.
        button.setEnabled(true);
        button.setFocusable(enabled);
        button.putClientProperty("rpg.actionEnabled", enabled);

        if (enabled) {
            Object baseBackground = button.getClientProperty("rpg.baseBackground");
            if (baseBackground instanceof Color) {
                button.setBackground((Color) baseBackground);
            }
            button.setForeground(BUTTON_TEXT);
            button.setToolTipText("Move in this direction");
        } else {
            // Inactive exits remain readable, but have a muted appearance.
            button.setBackground(DISABLED_BUTTON_BACKGROUND);
            button.setForeground(DISABLED_BUTTON_TEXT);
            button.setToolTipText("Movement is not available in this direction or during combat");
        }
    }

    private boolean isNavigationActionEnabled(JButton button) {
        return Boolean.TRUE.equals(button.getClientProperty("rpg.actionEnabled"));
    }

    private boolean directionExists(String buttonText) {
        Map<String, Integer> exits = gameBoard[currentRoomNumber].getAvailableExits();
        if (buttonText.contains("north")) return exits.containsKey("north");
        if (buttonText.contains("south")) return exits.containsKey("south");
        if (buttonText.contains("east")) return exits.containsKey("east");
        if (buttonText.contains("west")) return exits.containsKey("west");
        return false;
    }

    private void rebuildAttackButtons(MonsterRoom monsterRoom) {
        attackButtonsPanel.removeAll();
        List<Attack> attacks = player.getAttackSet().getAttacks();
        for (int i = 0; i < attacks.size(); i++) {
            final int attackIndex = i;
            Attack attack = attacks.get(i);
            String label = attack.getName() + "  ·  " + attack.getDamage()
                    + " dmg  ·  " + attack.getAccuracy() + "%";
            JButton button = makeButton(label, PANEL_LIGHT);
            button.setToolTipText(attack.getDescription());
            button.addActionListener(event -> {
                if (!gameOver) {
                    appendEvents(monsterRoom.performPlayerAttack(player, attackIndex));
                    refreshView();
                }
            });
            attackButtonsPanel.add(button);
        }
        attackButtonsPanel.revalidate();
        attackButtonsPanel.repaint();
    }

    private void refreshBattleItems() {
        battleItemCombo.removeAllItems();
        for (String item : player.getInventoryItems()) {
            if (HealthItemCatalog.find(item) != null) {
                battleItemCombo.addItem(item);
            }
        }
        boolean hasItems = battleItemCombo.getItemCount() > 0;
        battleItemCombo.setEnabled(hasItems);
        useBattleItemButton.setEnabled(hasItems && player.isAlive());
    }

    private boolean hasUsableHealthItem() {
        if (player.getHealth() >= player.getMaxHealth()) {
            return false;
        }
        for (String item : player.getInventoryItems()) {
            if (HealthItemCatalog.find(item) != null) {
                return true;
            }
        }
        return false;
    }

    private String roomDescriptionFor(Room room) {
        if (room instanceof MonsterRoom && !((MonsterRoom) room).isMonsterDefeated()) {
            return "A hostile creature blocks the way. Defeat it before continuing through this room.";
        }
        if (room instanceof SleepRoom) {
            return "A quiet place to rest. Resting restores 1 health point, up to your maximum health.";
        }
        switch (room.getRoomName()) {
            case "Kitchen": return "A kitchen with supplies that may help you recover. Search the room for food and a health potion.";
            case "Lab Room": return "A quiet laboratory. Look around and collect anything useful.";
            case "First Room": return "Your journey begins here. Explore the connected rooms and gather supplies.";
            default: return "Explore the room, inspect its objects, and use the exits to continue.";
        }
    }

    private void move(String direction) {
        if (gameOver) return;
        Room room = gameBoard[currentRoomNumber];
        Integer destination = room.getAvailableExits().get(direction);
        if (destination == null || destination < 0 || destination >= gameBoard.length) {
            appendLog("There is no exit to the " + direction + ".");
            return;
        }
        currentRoomNumber = destination;
        appendLog("You moved " + direction + " to " + gameBoard[currentRoomNumber].getRoomName() + ".");
        if (gameBoard[currentRoomNumber] instanceof MonsterRoom
                && !((MonsterRoom) gameBoard[currentRoomNumber]).isMonsterDefeated()) {
            appendLog("A monster blocks your way! Choose an attack or use a health item.");
        }
        refreshView();
    }

    private void pickUpSelectedItem() {
        if (gameOver) return;
        String item = roomItemsList.getSelectedValue();
        if (item == null) {
            appendLog("Select an item in the room first.");
            return;
        }
        if (gameBoard[currentRoomNumber].pickUpItem(player, item)) {
            appendLog("Picked up: " + item + ".");
        } else {
            appendLog("That item could not be picked up.");
        }
        refreshView();
    }

    private void dropSelectedItem() {
        if (gameOver) return;
        String item = inventoryList.getSelectedValue();
        if (item == null) {
            appendLog("Select an inventory item first.");
            return;
        }
        if (gameBoard[currentRoomNumber].dropItem(player, item)) {
            appendLog("Dropped: " + item + ".");
        } else {
            appendLog("That item could not be dropped.");
        }
        refreshView();
    }

    private void useHealthItemOutsideCombat() {
        if (gameOver) return;
        String[] items = usableHealthItems();
        if (items.length == 0) {
            appendLog("You do not have a usable health item, or your health is already full.");
            return;
        }
        String selected = (String) JOptionPane.showInputDialog(frame,
                "Choose a food item or potion:", "Use health item",
                JOptionPane.PLAIN_MESSAGE, null, items, items[0]);
        if (selected == null) return;

        int before = player.getHealth();
        if (player.useHealthItemQuietly(selected)) {
            appendLog("Used " + selected + "; restored " + (player.getHealth() - before) + " HP.");
        } else {
            appendLog("Could not use " + selected + ".");
        }
        refreshView();
    }

    private void useHealthItemInCombat() {
        if (gameOver || !(gameBoard[currentRoomNumber] instanceof MonsterRoom)) return;
        String selected = (String) battleItemCombo.getSelectedItem();
        if (selected == null) return;
        MonsterRoom monsterRoom = (MonsterRoom) gameBoard[currentRoomNumber];
        appendEvents(monsterRoom.useItemDuringBattle(player, selected));
        refreshView();
    }

    private String[] usableHealthItems() {
        return player.getInventoryItems().stream()
                .filter(item -> HealthItemCatalog.find(item) != null)
                .toArray(String[]::new);
    }

    private void rest() {
        if (gameOver || !(gameBoard[currentRoomNumber] instanceof SleepRoom)) return;
        int restored = ((SleepRoom) gameBoard[currentRoomNumber]).rest(player);
        appendLog("You rested by the fireplace and restored " + restored + " HP.");
        refreshView();
    }

    private void appendEvents(List<String> events) {
        for (String event : events) {
            appendLog(event);
        }
        if (!player.isAlive()) {
            gameOver = true;
        }
    }

    private void appendLog(String message) {
        if (eventLog == null) return;
        eventLog.append("• " + message + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    private void restartGame() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Start a new game? Your current progress will be lost.",
                "Restart game", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        startNewGame();
        eventLog.setText("");
        appendLog("A new adventure begins.");
        refreshView();
    }
}
