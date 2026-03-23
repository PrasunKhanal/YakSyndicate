package pieces;

import board.Position;
import java.util.List;

/**
 * Abstract class representing a chess piece.
 * All specific pieces extend this class and implement their own movement rules.
 */
public abstract class Piece {

    /** The color of the piece, either "white" or "black". */
    private String color;

    /** The current position of the piece on the board. */
    private Position position;

    /**
     * Constructs a Piece with a given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Piece(String color, Position position) {
        this.color = color;
        this.position = position;
    }

    /** @return the color of the piece */
    public String getColor() { return color; }

    /** @return the current position of the piece */
    public Position getPosition() { return position; }

    /**
     * Updates the position of the piece.
     * @param position new position
     */
    public void setPosition(Position position) { this.position = position; }

    /**
     * Returns a list of all possible moves from the current position.
     * Each subclass implements this based on its movement rules.
     * @param board the current board state
     * @return list of valid positions this piece can move to
     */
    public abstract List<Position> possibleMoves(Piece[][] board);

    /**
     * Returns the text representation of the piece (e.g., "wp", "bR").
     * @return string symbol
     */
    public abstract String getSymbol();
}
