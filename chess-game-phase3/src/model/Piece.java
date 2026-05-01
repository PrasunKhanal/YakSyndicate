package model;
import java.io.Serializable;

/**
 * Immutable value object representing one chess piece.
 */
public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    private final PieceType type;
    private final PieceColor color;

    /** @param type piece type  @param color piece color */
    public Piece(PieceType type, PieceColor color) {
        this.type = type; this.color = color;
    }

    /** @return piece type */
    public PieceType getType() { return type; }

    /** @return piece color */
    public PieceColor getColor() { return color; }

    /**
     * Unicode symbol for rendering in Swing.
     * @return single-char Unicode string
     */
    public String getSymbol() {
        if (color == PieceColor.WHITE) {
            switch (type) {
                case KING: return "\u2654"; case QUEEN: return "\u2655";
                case ROOK: return "\u2656"; case BISHOP: return "\u2657";
                case KNIGHT: return "\u2658"; case PAWN: return "\u2659";
            }
        } else {
            switch (type) {
                case KING: return "\u265A"; case QUEEN: return "\u265B";
                case ROOK: return "\u265C"; case BISHOP: return "\u265D";
                case KNIGHT: return "\u265E"; case PAWN: return "\u265F";
            }
        }
        return "?";
    }

    /** Material value used by AI evaluation. */
    public int getValue() {
        switch (type) {
            case PAWN: return 100; case KNIGHT: return 320;
            case BISHOP: return 330; case ROOK: return 500;
            case QUEEN: return 900; case KING: return 20000;
            default: return 0;
        }
    }

    @Override public String toString() { return color + "_" + type; }
}
