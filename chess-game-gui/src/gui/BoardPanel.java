package gui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Renders the 8x8 chess board and handles mouse interaction.
 * Supports both click-to-move and drag-and-drop piece movement.
 * No move validation is enforced per Phase 2 requirements.
 */
public class BoardPanel extends JPanel {

    private final BoardModel model;
    private final BoardSettings settings;
    private final GameWindow parent;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Piece dragPiece = null;
    private int dragX, dragY;
    private int dragFromRow = -1, dragFromCol = -1;

    /**
     * Constructs the board panel.
     * @param model    the game state model
     * @param settings appearance settings
     * @param parent   the enclosing GameWindow
     */
    public BoardPanel(BoardModel model, BoardSettings settings, GameWindow parent) {
        this.model    = model;
        this.settings = settings;
        this.parent   = parent;

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { handlePress(e); }
            @Override public void mouseReleased(MouseEvent e) { handleRelease(e); }
            @Override public void mouseDragged(MouseEvent e)  { handleDrag(e); }
            @Override public void mouseClicked(MouseEvent e)  { handleClick(e); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    public Dimension getPreferredSize() {
        int s = settings.squareSize;
        return new Dimension(s * 8, s * 8);
    }

    private void handlePress(MouseEvent e) {
        int col = e.getX() / settings.squareSize;
        int row = e.getY() / settings.squareSize;
        if (!inBounds(row, col)) return;
        Piece p = model.getPiece(row, col);
        if (p != null && p.getColor() == model.getCurrentTurn()) {
            dragPiece   = p;
            dragFromRow = row;
            dragFromCol = col;
            dragX       = e.getX();
            dragY       = e.getY();
        }
        repaint();
    }

    private void handleRelease(MouseEvent e) {
        int col = e.getX() / settings.squareSize;
        int row = e.getY() / settings.squareSize;
        if (dragPiece != null && inBounds(row, col)) {
            if (row != dragFromRow || col != dragFromCol) {
                executeMove(dragFromRow, dragFromCol, row, col);
                selectedRow = -1;
                selectedCol = -1;
            }
        }
        dragPiece   = null;
        dragFromRow = -1;
        dragFromCol = -1;
        repaint();
    }

    private void handleDrag(MouseEvent e) {
        if (dragPiece != null) {
            dragX = e.getX();
            dragY = e.getY();
            repaint();
        }
    }

    /**
     * Handles click-to-move selection and destination picking.
     * @param e the mouse event
     */
    public void handleClick(MouseEvent e) {
        int col = e.getX() / settings.squareSize;
        int row = e.getY() / settings.squareSize;
        if (!inBounds(row, col)) return;

        if (selectedRow >= 0) {
            if (row != selectedRow || col != selectedCol) {
                executeMove(selectedRow, selectedCol, row, col);
            }
            selectedRow = -1;
            selectedCol = -1;
        } else {
            Piece p = model.getPiece(row, col);
            if (p != null && p.getColor() == model.getCurrentTurn()) {
                selectedRow = row;
                selectedCol = col;
            }
        }
        repaint();
    }

    /**
     * Executes a move and triggers endgame check if a King was captured.
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     */
    private void executeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece captured = model.movePiece(fromRow, fromCol, toRow, toCol);
        parent.onMoveExecuted();
        if (captured != null && captured.getType() == PieceType.KING) {
            parent.onKingCaptured(captured.getColor());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int s = settings.squareSize;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boolean light = (r + c) % 2 == 0;
                g2.setColor(light ? settings.lightSquare : settings.darkSquare);
                g2.fillRect(c * s, r * s, s, s);
                if (r == selectedRow && c == selectedCol) {
                    g2.setColor(new Color(0, 255, 0, 80));
                    g2.fillRect(c * s, r * s, s, s);
                }
            }
        }

        // Rank and file labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < 8; i++) {
            g2.setColor((i % 2 == 0) ? settings.darkSquare : settings.lightSquare);
            g2.drawString(String.valueOf(8 - i), 3, i * s + 13);
            g2.setColor((i % 2 == 0) ? settings.lightSquare : settings.darkSquare);
            g2.drawString(String.valueOf((char)('A' + i)), i * s + s - 12, 8 * s - 3);
        }

        // Pieces
        Font pieceFont = new Font("Serif", Font.PLAIN, 1).deriveFont(settings.pieceFontSize);
        g2.setFont(pieceFont);
        FontMetrics fm = g2.getFontMetrics();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r == dragFromRow && c == dragFromCol && dragPiece != null) continue;
                Piece p = model.getPiece(r, c);
                if (p == null) continue;
                drawPiece(g2, p, c * s, r * s, s, fm);
            }
        }

        if (dragPiece != null) {
            drawPiece(g2, dragPiece, dragX - s / 2, dragY - s / 2, s, fm);
        }
    }

    /**
     * Draws a piece symbol centered within a square.
     * @param g2    graphics context
     * @param piece the piece to draw
     * @param x     left edge pixel
     * @param y     top edge pixel
     * @param size  square size in pixels
     * @param fm    font metrics
     */
    private void drawPiece(Graphics2D g2, Piece piece, int x, int y, int size, FontMetrics fm) {
        String sym = piece.getSymbol();
        int tx = x + (size - fm.stringWidth(sym)) / 2;
        int ty = y + (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawString(sym, tx + 2, ty + 2);
        g2.setColor(piece.getColor() == PieceColor.WHITE
                    ? new Color(255, 255, 240) : new Color(30, 30, 30));
        g2.drawString(sym, tx, ty);
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
