package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King chess piece.
 * Moves exactly one square in any direction.
 */
public class King extends Piece {

    /**
     * Constructs a King with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public King(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wK" for white, "bK" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wK" : "bK";
    }

    /**
     * Returns all possible moves for the king from its current position.
     * King moves one square in any of the 8 directions.
     * Cannot move to a square occupied by its own piece.
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        // All 8 surrounding squares
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            Position target = new Position(r, c);

            if (target.isValid()) {
                if (board[r][c] == null || !board[r][c].getColor().equals(getColor())) {
                    moves.add(target);
                }
            }
        }

        return moves;
    }
}
