package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight chess piece.
 * Moves in an L-shape: two squares in one direction, one square perpendicular.
 */
public class Knight extends Piece {

    /**
     * Constructs a Knight with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Knight(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wN" for white, "bN" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wN" : "bN";
    }

    /**
     * Returns all possible moves for the knight from its current position.
     * Knights jump in L-shapes and can leap over other pieces.
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        // All 8 possible L-shape jumps
        int[][] jumps = {
            {-2, -1}, {-2, 1},
            {-1, -2}, {-1, 2},
            {1, -2},  {1, 2},
            {2, -1},  {2, 1}
        };

        for (int[] jump : jumps) {
            int r = row + jump[0];
            int c = col + jump[1];
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
