package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bishop chess piece.
 * Moves any number of squares diagonally.
 */
public class Bishop extends Piece {

    /**
     * Constructs a Bishop with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Bishop(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wB" for white, "bB" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wB" : "bB";
    }

    /**
     * Returns all possible moves for the bishop from its current position.
     * Scans in four diagonal directions.
     * Stops at board edge, blocked by own piece, or captures opponent piece.
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        // Four diagonal directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

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
