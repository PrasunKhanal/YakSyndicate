package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen chess piece.
 * Moves any number of squares in any direction (combines Rook and Bishop).
 */
public class Queen extends Piece {

    /**
     * Constructs a Queen with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Queen(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wQ" for white, "bQ" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wQ" : "bQ";
    }

    /**
     * Returns all possible moves for the queen from its current position.
     * Combines horizontal, vertical, and diagonal sliding movement.
     * Stops at board edge, blocked by own piece, or captures opponent piece.
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        // All 8 directions: horizontal, vertical, and diagonal
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // rook directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // bishop directions
        };

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
