package gui;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Side panel showing move history, captured pieces, turn indicator, and Undo button.
 */
public class HistoryPanel extends JPanel {
    private final BoardModel model;
    private final GameWindow parent;
    private final JTextArea moveList;
    private final JLabel capturedWhite, capturedBlack, turnLabel;

    /**
     * @param model  board state
     * @param parent enclosing window for undo callback
     */
    public HistoryPanel(BoardModel model, GameWindow parent) {
        this.model = model; this.parent = parent;
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(230, 0));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(new Color(40, 40, 40));

        turnLabel = new JLabel("WHITE's turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setBorder(new EmptyBorder(4, 0, 8, 0));
        add(turnLabel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(40, 40, 40));

        JLabel hl = new JLabel("Move History");
        hl.setForeground(new Color(200, 180, 120));
        hl.setFont(new Font("SansSerif", Font.BOLD, 12));
        center.add(hl);

        moveList = new JTextArea();
        moveList.setEditable(false);
        moveList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        moveList.setBackground(new Color(25, 25, 25));
        moveList.setForeground(new Color(200, 200, 200));
        moveList.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(moveList);
        scroll.setPreferredSize(new Dimension(210, 320));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        center.add(scroll);

        center.add(Box.createVerticalStrut(10));

        JLabel cl = new JLabel("Captured Pieces");
        cl.setForeground(new Color(200, 180, 120));
        cl.setFont(new Font("SansSerif", Font.BOLD, 12));
        center.add(cl);

        capturedWhite = new JLabel("White captured: ");
        capturedWhite.setForeground(Color.LIGHT_GRAY);
        capturedWhite.setFont(new Font("Serif", Font.PLAIN, 16));
        center.add(capturedWhite);

        capturedBlack = new JLabel("Black captured: ");
        capturedBlack.setForeground(Color.LIGHT_GRAY);
        capturedBlack.setFont(new Font("Serif", Font.PLAIN, 16));
        center.add(capturedBlack);

        add(center, BorderLayout.CENTER);

        JButton undo = new JButton("Undo");
        undo.setFont(new Font("SansSerif", Font.BOLD, 13));
        undo.setBackground(new Color(180, 80, 80));
        undo.setForeground(Color.WHITE);
        undo.setFocusPainted(false);
        undo.addActionListener(e -> parent.onUndoRequested());
        add(undo, BorderLayout.SOUTH);
    }

    /** Refreshes all displayed data from the current model state. */
    public void refresh() {
        turnLabel.setText(model.getCurrentTurn() + "'s turn");
        List<String> hist = model.getMoveHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hist.size(); i++) sb.append(i + 1).append(". ").append(hist.get(i)).append("\n");
        moveList.setText(sb.toString());
        moveList.setCaretPosition(moveList.getDocument().getLength());

        StringBuilder wb = new StringBuilder("White captured: ");
        for (Piece p : model.getCapturedByWhite()) wb.append(p.getSymbol());
        capturedWhite.setText(wb.toString());

        StringBuilder bb = new StringBuilder("Black captured: ");
        for (Piece p : model.getCapturedByBlack()) bb.append(p.getSymbol());
        capturedBlack.setText(bb.toString());
    }
}
