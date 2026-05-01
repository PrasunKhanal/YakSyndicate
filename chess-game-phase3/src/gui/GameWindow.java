package gui;

import ai.ChessAI;
import model.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

/**
 * Main application window. Owns the BoardPanel, HistoryPanel, menu bar,
 * and coordinates all top-level game events including AI turns.
 */
public class GameWindow extends JFrame {
    private BoardModel model;
    private final BoardSettings settings;
    private BoardPanel boardPanel;
    private HistoryPanel historyPanel;
    private boolean gameOver = false;
    private boolean vsAI = false;
    private ChessAI ai;

    /** Constructs and displays the main game window. */
    public GameWindow() {
        super("Chess — YakSyndicate Phase 3");
        model = new BoardModel(); settings = new BoardSettings();
        buildUI(); buildMenuBar();
        pack(); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); setResizable(false); setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        boardPanel   = new BoardPanel(model, settings, this);
        historyPanel = new HistoryPanel(model, this);
        add(boardPanel, BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);
        historyPanel.refresh();
    }

    private void buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu game = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> onNewGame());

        JMenuItem save = new JMenuItem("Save Game");
        save.addActionListener(e -> onSave());

        JMenuItem load = new JMenuItem("Load Game");
        load.addActionListener(e -> onLoad());

        JCheckBoxMenuItem aiToggle = new JCheckBoxMenuItem("Play vs AI (Black)");
        aiToggle.addActionListener(e -> {
            vsAI = aiToggle.isSelected();
            ai   = vsAI ? new ChessAI(PieceColor.BLACK) : null;
        });

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));

        game.add(newGame); game.add(save); game.add(load);
        game.addSeparator(); game.add(aiToggle);
        game.addSeparator(); game.add(quit);

        JMenu view = new JMenu("View");
        JMenuItem settingsItem = new JMenuItem("Settings...");
        settingsItem.addActionListener(e -> new SettingsDialog(this, settings, () -> {
            pack(); boardPanel.repaint();
        }).setVisible(true));
        view.add(settingsItem);

        mb.add(game); mb.add(view);
        setJMenuBar(mb);
    }

    /**
     * Called by BoardPanel after a human move completes.
     * Refreshes UI, checks for end of game, then triggers AI if enabled.
     */
    public void onMoveExecuted() {
        historyPanel.refresh();
        boardPanel.repaint();
        checkEndOfGame();
        if (!gameOver && vsAI && model.getCurrentTurn() == PieceColor.BLACK) {
            triggerAIMove();
        }
    }

    /** Checks for checkmate or stalemate and shows the appropriate dialog. */
    private void checkEndOfGame() {
        PieceColor turn = model.getCurrentTurn();
        if (MoveValidator.hasNoLegalMoves(model, turn)) {
            gameOver = true;
            if (MoveValidator.isInCheck(model, turn)) {
                String winner = (turn == PieceColor.WHITE) ? "Black" : "White";
                JOptionPane.showMessageDialog(this,
                    winner + " wins by checkmate!", "Checkmate!", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Stalemate — it's a draw!", "Stalemate", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /** Runs the AI move on a background thread so the GUI does not freeze. */
    private void triggerAIMove() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override protected int[] doInBackground() { return ai.getBestMove(model); }
            @Override protected void done() {
                try {
                    int[] move = get();
                    if (move != null) {
                        model.movePiece(move[0], move[1], move[2], move[3]);
                        historyPanel.refresh();
                        boardPanel.repaint();
                        checkEndOfGame();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        worker.execute();
    }

    /** Called by HistoryPanel's Undo button. */
    public void onUndoRequested() {
        if (gameOver) return;
        // Undo twice if playing vs AI so human gets their turn back
        model.undo();
        if (vsAI) model.undo();
        gameOver = false;
        boardPanel.repaint(); historyPanel.refresh();
    }

    private void onNewGame() {
        if (JOptionPane.showConfirmDialog(this, "Start a new game?", "New Game",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            model.reset(); gameOver = false;
            boardPanel.repaint(); historyPanel.refresh();
        }
    }

    private void onSave() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Chess saves (*.chess)", "chess"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String p = fc.getSelectedFile().getAbsolutePath();
            if (!p.endsWith(".chess")) p += ".chess";
            try { model.saveToFile(p); JOptionPane.showMessageDialog(this, "Saved."); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage()); }
        }
    }

    private void onLoad() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Chess saves (*.chess)", "chess"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                model = BoardModel.loadFromFile(fc.getSelectedFile().getAbsolutePath());
                gameOver = false;
                remove(boardPanel); remove(historyPanel);
                boardPanel   = new BoardPanel(model, settings, this);
                historyPanel = new HistoryPanel(model, this);
                add(boardPanel, BorderLayout.CENTER); add(historyPanel, BorderLayout.EAST);
                revalidate(); repaint(); historyPanel.refresh();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage()); }
        }
    }
}
