package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rook chess piece.
 * Moves any number of squares horizontally or vertically.
 */
public class Rook extends Piece {

    /**
     * Constructs a Rook with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Rook(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wR" for white, "bR" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wR" : "bR";
    }

    /**
     * Returns all possible moves for the rook from its current position.
     * Scans in four directions: up, down, left, right.
     * Stops at board edge, blocked by own piece, or captures opponent piece.
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        // Four directions: up, down, left, right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (board[r][c] == null) {
                    moves.add(new Position(r, c));
                } else {
                    if (!board[r][c].getColor().equals(getColor())) {
                        moves.add(new Position(r, c)); // capture
                    }
                    break; // blocked either way
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return moves;
    }
}
