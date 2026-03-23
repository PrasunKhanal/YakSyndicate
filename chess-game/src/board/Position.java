package board;

/**
 * Represents a position on the chess board.
 * Handles conversion between chess notation (e.g., "E2") and internal 0-based indices.
 */
public class Position {

    /** Row index (0-7), where 0 is rank 8 and 7 is rank 1. */
    private int row;

    /** Column index (0-7), where 0 is file A and 7 is file H. */
    private int col;

    /**
     * Constructs a Position from row and column indices.
     * @param row row index (0-7)
     * @param col column index (0-7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructs a Position from chess notation (e.g., "E2").
     * @param notation chess notation string
     */
    public Position(String notation) {
        notation = notation.trim().toUpperCase();
        this.col = notation.charAt(0) - 'A';
        this.row = 8 - Character.getNumericValue(notation.charAt(1));
    }

    /** @return row index (0-7) */
    public int getRow() { return row; }

    /** @return column index (0-7) */
    public int getCol() { return col; }

    /**
     * Checks if this position is within board bounds.
     * @return true if valid
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Converts position back to chess notation (e.g., "E2").
     * @return chess notation string
     */
    @Override
    public String toString() {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /** Checks equality by row and column. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }
}
