package gui;

import model.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * The main application window containing the board panel, history panel,
 * menu bar, and all top-level game event handling.
 */
public class GameWindow extends JFrame {

    private BoardModel model;
    private final BoardSettings settings;
    private BoardPanel boardPanel;
    private HistoryPanel historyPanel;
    private boolean gameOver = false;

    /**
     * Constructs and displays the main game window.
     */
    public GameWindow() {
        super("Chess Game — YakSyndicate");
        model    = new BoardModel();
        settings = new BoardSettings();
        buildUI();
        buildMenuBar();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    /** Assembles the board and history panels into the frame. */
    private void buildUI() {
        setLayout(new BorderLayout());
        boardPanel   = new BoardPanel(model, settings, this);
        historyPanel = new HistoryPanel(model, this);
        add(boardPanel,   BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);
        historyPanel.refresh();
    }

    /** Builds the menu bar with Game and View menus. */
    private void buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        // ── Game menu ─────────────────────────────────────────
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> onNewGame());

        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(e -> onSaveGame());

        JMenuItem loadGame = new JMenuItem("Load Game");
        loadGame.addActionListener(e -> onLoadGame());

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(saveGame);
        gameMenu.add(loadGame);
        gameMenu.addSeparator();
        gameMenu.add(quit);

        // ── View menu ─────────────────────────────────────────
        JMenu viewMenu = new JMenu("View");

        JMenuItem settingsItem = new JMenuItem("Settings...");
        settingsItem.addActionListener(e -> onOpenSettings());
        viewMenu.add(settingsItem);

        mb.add(gameMenu);
        mb.add(viewMenu);
        setJMenuBar(mb);
    }

    // ── Callbacks called by BoardPanel ────────────────────────────────────────

    /**
     * Called after every successful move. Refreshes the history panel.
     */
    public void onMoveExecuted() {
        historyPanel.refresh();
    }

    /**
     * Called when a King is captured. Declares the winner and ends the game.
     * @param capturedKingColor the color of the King that was captured
     */
    public void onKingCaptured(PieceColor capturedKingColor) {
        gameOver = true;
        String winner = (capturedKingColor == PieceColor.WHITE) ? "Black" : "White";
        JOptionPane.showMessageDialog(
            this,
            winner + " wins by capturing the King!\nThank you for playing.",
            "Game Over — " + winner + " Wins!",
            JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }

    /**
     * Called when the Undo button is pressed. Reverts the last move.
     */
    public void onUndoRequested() {
        if (gameOver) return;
        if (!model.undo()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
        boardPanel.repaint();
        historyPanel.refresh();
    }

    // ── Menu Handlers ─────────────────────────────────────────────────────────

    /** Starts a new game after confirmation. */
    private void onNewGame() {
        int result = JOptionPane.showConfirmDialog(
            this, "Start a new game? Current progress will be lost.",
            "New Game", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            model.reset();
            gameOver = false;
            boardPanel.repaint();
            historyPanel.refresh();
        }
    }

    /** Saves the current game state to a file chosen by the user. */
    private void onSaveGame() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Game");
        fc.setFileFilter(new FileNameExtensionFilter("Chess Save Files (*.chess)", "chess"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".chess")) path += ".chess";
            try {
                model.saveToFile(path);
                JOptionPane.showMessageDialog(this, "Game saved successfully.", "Save Game", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Loads a previously saved game from a file chosen by the user. */
    private void onLoadGame() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load Game");
        fc.setFileFilter(new FileNameExtensionFilter("Chess Save Files (*.chess)", "chess"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                model = BoardModel.loadFromFile(fc.getSelectedFile().getAbsolutePath());
                gameOver = false;
                remove(boardPanel);
                remove(historyPanel);
                boardPanel   = new BoardPanel(model, settings, this);
                historyPanel = new HistoryPanel(model, this);
                add(boardPanel,   BorderLayout.CENTER);
                add(historyPanel, BorderLayout.EAST);
                revalidate();
                repaint();
                historyPanel.refresh();
                JOptionPane.showMessageDialog(this, "Game loaded successfully.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Opens the Settings dialog. */
    private void onOpenSettings() {
        new SettingsDialog(this, settings, () -> {
            boardPanel.setPreferredSize(boardPanel.getPreferredSize());
            pack();
            boardPanel.repaint();
        }).setVisible(true);
    }
}
