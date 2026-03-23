package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn chess piece.
 * Moves forward one square, two squares from starting position,
 * and captures diagonally.
 */
public class Pawn extends Piece {

    /**
     * Constructs a Pawn with given color and position.
     * @param color "white" or "black"
     * @param position starting position
     */
    public Pawn(String color, Position position) {
        super(color, position);
    }

    /**
     * Returns symbol for display: "wp" for white, "bp" for black.
     * @return piece symbol
     */
    @Override
    public String getSymbol() {
        return getColor().equals("white") ? "wp" : "bp";
    }

    /**
     * Returns all possible moves for the pawn from its current position.
     * White moves up (decreasing row), black moves down (increasing row).
     * @param board current board state
     * @return list of possible positions
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();
        int direction = getColor().equals("white") ? -1 : 1;
        int startRow = getColor().equals("white") ? 6 : 1;

        // One step forward
        Position oneStep = new Position(row + direction, col);
        if (oneStep.isValid() && board[row + direction][col] == null) {
            moves.add(oneStep);

            // Two steps forward from starting row
            if (row == startRow) {
                Position twoStep = new Position(row + 2 * direction, col);
                if (board[row + 2 * direction][col] == null) {
                    moves.add(twoStep);
                }
            }
        }

        // Diagonal captures
        int[] captureCols = {col - 1, col + 1};
        for (int captureCol : captureCols) {
            Position capture = new Position(row + direction, captureCol);
            if (capture.isValid() && board[row + direction][captureCol] != null
                    && !board[row + direction][captureCol].getColor().equals(getColor())) {
                moves.add(capture);
            }
        }

        return moves;
    }
}
