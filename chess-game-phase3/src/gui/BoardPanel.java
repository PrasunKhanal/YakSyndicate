package gui;

import model.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Swing component that renders the chess board and handles mouse input.
 * Displays legal move highlights, check highlight, and supports both
 * click-to-move and drag-and-drop. All move legality is checked via MoveValidator.
 */
public class BoardPanel extends JPanel {
    private final BoardModel model;
    private final BoardSettings settings;
    private final GameWindow parent;

    private int selectedRow = -1, selectedCol = -1;
    private List<int[]> legalDestinations = new ArrayList<>();

    private Piece dragPiece = null;
    private int dragX, dragY, dragFromRow = -1, dragFromCol = -1;

    /**
     * @param model    game state
     * @param settings appearance settings
     * @param parent   enclosing GameWindow
     */
    public BoardPanel(BoardModel model, BoardSettings settings, GameWindow parent) {
        this.model = model; this.settings = settings; this.parent = parent;
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { handlePress(e); }
            @Override public void mouseReleased(MouseEvent e) { handleRelease(e); }
            @Override public void mouseDragged(MouseEvent e)  { handleDrag(e); }
            @Override public void mouseClicked(MouseEvent e)  { handleClick(e); }
        };
        addMouseListener(ma); addMouseMotionListener(ma);
    }

    @Override public Dimension getPreferredSize() {
        int s = settings.squareSize; return new Dimension(s * 8, s * 8);
    }

    private void handlePress(MouseEvent e) {
        int col = e.getX() / settings.squareSize, row = e.getY() / settings.squareSize;
        if (!inBounds(row, col)) return;
        Piece p = model.getPiece(row, col);
        if (p != null && p.getColor() == model.getCurrentTurn()) {
            dragPiece = p; dragFromRow = row; dragFromCol = col;
            dragX = e.getX(); dragY = e.getY();
        }
    }

    private void handleRelease(MouseEvent e) {
        int col = e.getX() / settings.squareSize, row = e.getY() / settings.squareSize;
        if (dragPiece != null && inBounds(row, col) && (row != dragFromRow || col != dragFromCol)) {
            tryMove(dragFromRow, dragFromCol, row, col);
            selectedRow = -1; selectedCol = -1; legalDestinations.clear();
        }
        dragPiece = null; dragFromRow = -1; dragFromCol = -1;
        repaint();
    }

    private void handleDrag(MouseEvent e) {
        if (dragPiece != null) { dragX = e.getX(); dragY = e.getY(); repaint(); }
    }

    private void handleClick(MouseEvent e) {
        int col = e.getX() / settings.squareSize, row = e.getY() / settings.squareSize;
        if (!inBounds(row, col)) return;
        if (selectedRow >= 0) {
            if (row != selectedRow || col != selectedCol) {
                tryMove(selectedRow, selectedCol, row, col);
            }
            selectedRow = -1; selectedCol = -1; legalDestinations.clear();
        } else {
            Piece p = model.getPiece(row, col);
            if (p != null && p.getColor() == model.getCurrentTurn()) {
                selectedRow = row; selectedCol = col;
                legalDestinations = MoveValidator.getLegalMoves(model, row, col);
            }
        }
        repaint();
    }

    /**
     * Attempts a move only if it appears in the legal moves list.
     * @param fr from row  @param fc from col  @param tr to row  @param tc to col
     */
    private void tryMove(int fr, int fc, int tr, int tc) {
        List<int[]> legal = MoveValidator.getLegalMoves(model, fr, fc);
        boolean allowed = false;
        for (int[] d : legal) if (d[0] == tr && d[1] == tc) { allowed = true; break; }
        if (!allowed) return;
        model.movePiece(fr, fc, tr, tc);
        parent.onMoveExecuted();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int s = settings.squareSize;

        // Draw squares
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            g2.setColor(((r + c) % 2 == 0) ? settings.lightSquare : settings.darkSquare);
            g2.fillRect(c * s, r * s, s, s);
        }

        // Check highlight
        PieceColor turn = model.getCurrentTurn();
        if (MoveValidator.isInCheck(model, turn)) {
            for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
                Piece p = model.getPiece(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == turn) {
                    g2.setColor(new Color(255, 0, 0, 100));
                    g2.fillRect(c * s, r * s, s, s);
                }
            }
        }

        // Selected square
        if (selectedRow >= 0) {
            g2.setColor(new Color(0, 200, 100, 80));
            g2.fillRect(selectedCol * s, selectedRow * s, s, s);
        }

        // Legal move dots
        g2.setColor(new Color(0, 100, 255, 100));
        for (int[] d : legalDestinations) {
            int cx = d[1] * s + s / 2, cy = d[0] * s + s / 2, r = s / 5;
            if (model.getPiece(d[0], d[1]) != null) {
                g2.setColor(new Color(255, 80, 80, 120));
                g2.drawOval(d[1] * s + 2, d[0] * s + 2, s - 4, s - 4);
                g2.setColor(new Color(0, 100, 255, 100));
            } else {
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            }
        }

        // Labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < 8; i++) {
            g2.setColor((i % 2 == 0) ? settings.darkSquare : settings.lightSquare);
            g2.drawString(String.valueOf(8 - i), 3, i * s + 13);
            g2.setColor((i % 2 == 0) ? settings.lightSquare : settings.darkSquare);
            g2.drawString(String.valueOf((char)('A' + i)), i * s + s - 12, 8 * s - 3);
        }

        // Pieces
        Font pf = new Font("Serif", Font.PLAIN, 1).deriveFont(settings.pieceFontSize);
        g2.setFont(pf); FontMetrics fm = g2.getFontMetrics();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            if (r == dragFromRow && c == dragFromCol && dragPiece != null) continue;
            Piece p = model.getPiece(r, c);
            if (p != null) drawPiece(g2, p, c * s, r * s, s, fm);
        }
        if (dragPiece != null) drawPiece(g2, dragPiece, dragX - s / 2, dragY - s / 2, s, fm);
    }

    private void drawPiece(Graphics2D g2, Piece p, int x, int y, int s, FontMetrics fm) {
        String sym = p.getSymbol();
        int tx = x + (s - fm.stringWidth(sym)) / 2;
        int ty = y + (s + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(new Color(0, 0, 0, 60)); g2.drawString(sym, tx + 2, ty + 2);
        g2.setColor(p.getColor() == PieceColor.WHITE ? new Color(255, 255, 240) : new Color(20, 20, 20));
        g2.drawString(sym, tx, ty);
    }

    private boolean inBounds(int r, int c) { return r >= 0 && r < 8 && c >= 0 && c < 8; }
}
