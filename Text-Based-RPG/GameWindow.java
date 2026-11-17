import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/** Swing interface for playing the existing command-driven game. */
public final class GameWindow {

    private static final AttributeSet OUTPUT_STYLE = createTextStyle(
            new Color(0xE2E8F0), false);
    private static final AttributeSet INPUT_STYLE = createTextStyle(
            new Color(0x5EEAD4), true);

    private final JFrame frame = new JFrame("House Adventure");
    private final JTextPane gameLog = new JTextPane();
    private final JTextField commandField = new JTextField();
    private final JLabel promptLabel = new JLabel("Starting game...");
    private final JPanel roomActions = new JPanel(new GridLayout(0, 4, 8, 8));
    private final JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel combatActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
    private final JButton sendButton = new JButton("Send");
    private final PipedOutputStream commandOutput;
    private final ExecutorService gameThread = Executors.newSingleThreadExecutor();
    private final StringBuilder outputTail = new StringBuilder();

    private GameWindow(Runnable game) throws IOException {
        PipedInputStream gameInput = new PipedInputStream(8192);
        commandOutput = new PipedOutputStream(gameInput);

        System.setIn(gameInput);
        PrintStream guiOutput = new PrintStream(new GameLogOutput(), true, StandardCharsets.UTF_8);
        System.setOut(guiOutput);
        System.setErr(guiOutput);

        buildWindow();
        frame.setVisible(true);
        gameThread.submit(() -> {
            try {
                game.run();
            } finally {
                gameThread.shutdown();
                SwingUtilities.invokeLater(() -> {
                    promptLabel.setText("Game over. Close this window to exit.");
                    setActionsEnabled(false);
                });
            }
        });
    }

    public static void launch(Runnable game) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameWindow(game);
            } catch (IOException exception) {
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "Could not start the game window: " + exception.getMessage(),
                        "Game startup failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void buildWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new java.awt.Dimension(760, 560));
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);

        gameLog.setEditable(false);
        gameLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        gameLog.setBackground(new Color(0x101820));
        gameLog.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane gameLogScroll = new JScrollPane(gameLog);
        gameLogScroll.setBorder(BorderFactory.createTitledBorder(
                "Game log — cyan text is your input"));
        frame.add(gameLogScroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(8, 8));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel actionSection = new JPanel(new BorderLayout(4, 4));
        actionSection.add(roomActions, BorderLayout.CENTER);
        addAction("Look", "look");
        addAction("Inventory", "inventory");
        addAction("Exits", "exits");
        addAction("Pick up", "pick up");
        addAction("Drop", "drop");
        addAction("Use item", "use");
        addAction("Sleep", "sleep");
        addAction("Help", "help");

        combatActions.setBorder(BorderFactory.createTitledBorder("Combat (or type a move below)"));
        combatActions.setVisible(false);
        actionSection.add(combatActions, BorderLayout.SOUTH);
        controls.add(actionSection, BorderLayout.CENTER);

        navigation.setBorder(BorderFactory.createTitledBorder("Move"));
        addNavigation(navigation, "North", "go north");
        addNavigation(navigation, "East", "go east");
        addNavigation(navigation, "South", "go south");
        addNavigation(navigation, "West", "go west");
        controls.add(navigation, BorderLayout.NORTH);

        JPanel commandRow = new JPanel(new BorderLayout(8, 4));
        commandRow.add(promptLabel, BorderLayout.NORTH);
        commandRow.add(commandField, BorderLayout.CENTER);
        sendButton.addActionListener(event -> submitCommand(commandField.getText()));
        commandRow.add(sendButton, BorderLayout.EAST);
        commandField.addActionListener(event -> submitCommand(commandField.getText()));
        controls.add(commandRow, BorderLayout.SOUTH);
        frame.add(controls, BorderLayout.SOUTH);

        addCombatActions();
        setActionsEnabled(false);
    }

    private void addAction(String label, String command) {
        JButton button = new JButton(label);
        button.addActionListener(event -> submitCommand(command));
        roomActions.add(button);
    }

    private void addNavigation(JPanel panel, String label, String command) {
        JButton button = new JButton(label);
        button.addActionListener(event -> submitCommand(command));
        panel.add(button);
    }

    private void addCombatActions() {
        for (Attack attack : new PlayerAttackSet().getAttacks()) {
            JButton button = new JButton(attack.getName());
            button.setToolTipText(attack.getDamage() + " damage, "
                    + attack.getAccuracy() + "% accuracy");
            button.addActionListener(event -> submitCommand(attack.getName()));
            combatActions.add(button);
        }
    }

    private void submitCommand(String command) {
        String text = command == null ? "" : command.trim();
        if (text.isEmpty()) {
            return;
        }

        try {
            commandOutput.write((text + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            commandOutput.flush();
            appendToLog("> " + text + System.lineSeparator(), INPUT_STYLE);
            commandField.setText("");
            promptLabel.setText("Waiting for the game...");
            setActionsEnabled(false);
        } catch (IOException exception) {
            promptLabel.setText("Could not send the command: " + exception.getMessage());
        }
    }

    private void setActionsEnabled(boolean enabled) {
        for (java.awt.Component component : roomActions.getComponents()) {
            component.setEnabled(enabled);
        }
        for (java.awt.Component component : navigation.getComponents()) {
            component.setEnabled(enabled);
        }
        for (java.awt.Component component : combatActions.getComponents()) {
            component.setEnabled(enabled);
        }
        commandField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            appendToLog(text, OUTPUT_STYLE);
            outputTail.append(text);
            String recentOutput = outputTail.toString();
            if (recentOutput.contains("Choose an attack number")) {
                roomActions.setVisible(false);
                navigation.setVisible(false);
                combatActions.setVisible(true);
                promptLabel.setText("Choose a move, or enter inventory, use <item>, or quit.");
                setActionsEnabled(true);
                outputTail.setLength(0);
            } else if (recentOutput.contains("What would you like to do?")) {
                roomActions.setVisible(true);
                navigation.setVisible(true);
                combatActions.setVisible(false);
                promptLabel.setText("Choose an action or enter a command.");
                setActionsEnabled(true);
                outputTail.setLength(0);
            } else if (recentOutput.contains("What would you like to pick up?")
                    || recentOutput.contains("What item would you like to drop?")
                    || recentOutput.contains("What would you like to drop?")
                    || recentOutput.contains("Which item would you like to use?")) {
                promptLabel.setText("Enter the requested item name, then press Enter.");
                commandField.setEnabled(true);
                sendButton.setEnabled(true);
                outputTail.setLength(0);
            } else if (outputTail.length() > 256) {
                outputTail.delete(0, outputTail.length() - 256);
            }
        });
    }

    private void appendToLog(String text, AttributeSet style) {
        StyledDocument document = gameLog.getStyledDocument();
        try {
            document.insertString(document.getLength(), text, style);
            gameLog.setCaretPosition(document.getLength());
        } catch (BadLocationException exception) {
            throw new IllegalStateException("Could not update the game log.", exception);
        }
    }

    private static AttributeSet createTextStyle(Color foreground, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, foreground);
        StyleConstants.setBold(style, bold);
        return style;
    }

    private final class GameLogOutput extends OutputStream {
        @Override
        public void write(int value) {
            appendOutput(new String(new byte[] {(byte) value}, StandardCharsets.UTF_8));
        }

        @Override
        public void write(byte[] buffer, int offset, int length) {
            appendOutput(new String(buffer, offset, length, StandardCharsets.UTF_8));
        }
    }
}
