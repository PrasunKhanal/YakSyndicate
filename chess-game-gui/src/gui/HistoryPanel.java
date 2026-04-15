package gui;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Side panel displaying move history, captured pieces, and an Undo button.
 * Updates in real time after every move.
 */
public class HistoryPanel extends JPanel {

    private final BoardModel model;
    private final GameWindow parent;

    private final JTextArea moveList;
    private final JLabel capturedWhiteLabel;
    private final JLabel capturedBlackLabel;
    private final JLabel turnLabel;

    /**
     * Constructs the history panel.
     * @param model  the game state model
     * @param parent the enclosing GameWindow (needed for undo callback)
     */
    public HistoryPanel(BoardModel model, GameWindow parent) {
        this.model  = model;
        this.parent = parent;

        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(new Color(40, 40, 40));

        // Turn indicator
        turnLabel = new JLabel("WHITE's turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setBorder(new EmptyBorder(4, 0, 8, 0));
        add(turnLabel, BorderLayout.NORTH);

        // Center: move history + captured pieces
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(40, 40, 40));

        JLabel histTitle = new JLabel("Move History");
        histTitle.setForeground(new Color(200, 180, 120));
        histTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        center.add(histTitle);

        moveList = new JTextArea();
        moveList.setEditable(false);
        moveList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        moveList.setBackground(new Color(25, 25, 25));
        moveList.setForeground(new Color(200, 200, 200));
        moveList.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(moveList);
        scroll.setPreferredSize(new Dimension(200, 300));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        center.add(scroll);

        center.add(Box.createVerticalStrut(10));

        JLabel capTitle = new JLabel("Captured Pieces");
        capTitle.setForeground(new Color(200, 180, 120));
        capTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        center.add(capTitle);

        capturedWhiteLabel = new JLabel("White captured: ");
        capturedWhiteLabel.setForeground(Color.LIGHT_GRAY);
        capturedWhiteLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        center.add(capturedWhiteLabel);

        capturedBlackLabel = new JLabel("Black captured: ");
        capturedBlackLabel.setForeground(Color.LIGHT_GRAY);
        capturedBlackLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        center.add(capturedBlackLabel);

        add(center, BorderLayout.CENTER);

        // Undo button
        JButton undoBtn = new JButton("Undo");
        undoBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        undoBtn.setBackground(new Color(180, 80, 80));
        undoBtn.setForeground(Color.WHITE);
        undoBtn.setFocusPainted(false);
        undoBtn.addActionListener(e -> parent.onUndoRequested());
        add(undoBtn, BorderLayout.SOUTH);
    }

    /**
     * Refreshes all displayed information from the current model state.
     * Should be called after every move or undo.
     */
    public void refresh() {
        // Turn label
        turnLabel.setText(model.getCurrentTurn() + "'s turn");

        // Move history
        List<String> history = model.getMoveHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(i + 1).append(". ").append(history.get(i)).append("\n");
        }
        moveList.setText(sb.toString());
        moveList.setCaretPosition(moveList.getDocument().getLength());

        // Captured pieces
        StringBuilder wb = new StringBuilder("White captured: ");
        for (Piece p : model.getCapturedByWhite()) wb.append(p.getSymbol());
        capturedWhiteLabel.setText(wb.toString());

        StringBuilder bb = new StringBuilder("Black captured: ");
        for (Piece p : model.getCapturedByBlack()) bb.append(p.getSymbol());
        capturedBlackLabel.setText(bb.toString());
    }
}
