package model;

import java.io.Serializable;

/**
 * Represents a single chess piece with a type and color.
 */
public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    private final PieceType type;
    private final PieceColor color;

    /**
     * Constructs a Piece with the given type and color.
     * @param type  the piece type
     * @param color the piece color
     */
    public Piece(PieceType type, PieceColor color) {
        this.type = type;
        this.color = color;
    }

    /** @return the type of this piece */
    public PieceType getType() { return type; }

    /** @return the color of this piece */
    public PieceColor getColor() { return color; }

    /**
     * Returns the Unicode symbol for this piece.
     * @return Unicode chess symbol
     */
    public String getSymbol() {
        if (color == PieceColor.WHITE) {
            switch (type) {
                case KING:   return "\u2654";
                case QUEEN:  return "\u2655";
                case ROOK:   return "\u2656";
                case BISHOP: return "\u2657";
                case KNIGHT: return "\u2658";
                case PAWN:   return "\u2659";
            }
        } else {
            switch (type) {
                case KING:   return "\u265A";
                case QUEEN:  return "\u265B";
                case ROOK:   return "\u265C";
                case BISHOP: return "\u265D";
                case KNIGHT: return "\u265E";
                case PAWN:   return "\u265F";
            }
        }
        return "?";
    }

    @Override
    public String toString() { return color + " " + type; }
}
