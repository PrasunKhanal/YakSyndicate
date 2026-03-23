package board;

import pieces.*;

/**
 * Represents the chess board as an 8x8 grid.
 * Handles piece placement, movement, and board display.
 */
public class Board {

    /** 8x8 matrix of pieces. Null represents an empty square. */
    private Piece[][] board;

    /**
     * Constructs a Board and sets up pieces in their initial positions.
     */
    public Board() {
        board = new Piece[8][8];
        setupBoard();
    }

    /**
     * Places all pieces in their standard starting positions.
     */
    private void setupBoard() {
        // Black pieces back rank
        board[0][0] = new Rook("black", new Position(0, 0));
        board[0][1] = new Knight("black", new Position(0, 1));
        board[0][2] = new Bishop("black", new Position(0, 2));
        board[0][3] = new Queen("black", new Position(0, 3));
        board[0][4] = new King("black", new Position(0, 4));
        board[0][5] = new Bishop("black", new Position(0, 5));
        board[0][6] = new Knight("black", new Position(0, 6));
        board[0][7] = new Rook("black", new Position(0, 7));

        // Black pawns
        for (int c = 0; c < 8; c++) {
            board[1][c] = new Pawn("black", new Position(1, c));
        }

        // White pawns
        for (int c = 0; c < 8; c++) {
            board[6][c] = new Pawn("white", new Position(6, c));
        }

        // White pieces back rank
        board[7][0] = new Rook("white", new Position(7, 0));
        board[7][1] = new Knight("white", new Position(7, 1));
        board[7][2] = new Bishop("white", new Position(7, 2));
        board[7][3] = new Queen("white", new Position(7, 3));
        board[7][4] = new King("white", new Position(7, 4));
        board[7][5] = new Bishop("white", new Position(7, 5));
        board[7][6] = new Knight("white", new Position(7, 6));
        board[7][7] = new Rook("white", new Position(7, 7));
    }

    /**
     * Returns the piece at a given position.
     * @param position board position
     * @return piece at position, or null if empty
     */
    public Piece getPiece(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    /**
     * Moves a piece from one position to another.
     * Does not validate legality — call before moving.
     * @param from source position
     * @param to destination position
     */
    public void movePiece(Position from, Position to) {
        Piece piece = board[from.getRow()][from.getCol()];
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = null;
        piece.setPosition(to);
    }

    /**
     * Returns the raw 8x8 board array.
     * Used by pieces to calculate possible moves.
     * @return board array
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Checks if the king of the given color is in check.
     * @param color "white" or "black"
     * @return true if the king is in check
     */
    public boolean isCheck(String color) {
        // Find the king
        Position kingPos = null;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor().equals(color) && p instanceof King) {
                    kingPos = new Position(r, c);
                }
            }
        }

        // Check if any opponent piece can reach the king
        String opponent = color.equals("white") ? "black" : "white";
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor().equals(opponent)) {
                    for (Position move : p.possibleMoves(board)) {
                        if (move.equals(kingPos)) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given color is in checkmate.
     * @param color "white" or "black"
     * @return true if checkmated
     */
    public boolean isCheckmate(String color) {
        if (!isCheck(color)) return false;

        // Try every possible move for every piece of this color
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor().equals(color)) {
                    for (Position move : p.possibleMoves(board)) {
                        // Simulate the move
                        Piece captured = board[move.getRow()][move.getCol()];
                        Position original = new Position(r, c);
                        movePiece(original, move);

                        boolean stillInCheck = isCheck(color);

                        // Undo the move
                        movePiece(move, original);
                        board[move.getRow()][move.getCol()] = captured;
                        p.setPosition(original);

                        if (!stillInCheck) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Displays the current board state in the console.
     * Dark squares shown as ##, light squares as empty.
     */
    public void display() {
        System.out.println("   A   B   C   D   E   F   G   H");
        for (int r = 0; r < 8; r++) {
            System.out.print((8 - r) + " ");
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null) {
                    System.out.print(" " + board[r][c].getSymbol() + " ");
                } else {
                    // Dark squares: (r+c) is odd
                    if ((r + c) % 2 != 0) {
                        System.out.print(" ## ");
                    } else {
                        System.out.print("    ");
                    }
                }
            }
            System.out.println();
        }
    }
}
