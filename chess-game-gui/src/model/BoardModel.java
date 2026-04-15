package model;

import java.io.*;
import java.util.*;

/**
 * Holds the full state of the chess board including pieces, captured pieces,
 * move history, and undo stack. No move validation is enforced (Phase 2 spec).
 */
public class BoardModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Piece[][] board;
    private List<Piece> capturedByWhite;
    private List<Piece> capturedByBlack;
    private List<String> moveHistory;
    private Deque<Piece[][]> undoStack;
    private PieceColor currentTurn;

    /**
     * Constructs a new BoardModel and sets up the starting position.
     */
    public BoardModel() {
        capturedByWhite = new ArrayList<>();
        capturedByBlack = new ArrayList<>();
        moveHistory     = new ArrayList<>();
        undoStack       = new ArrayDeque<>();
        reset();
    }

    /**
     * Resets the board to the standard starting position and clears all history.
     */
    public void reset() {
        board = new Piece[8][8];
        capturedByWhite.clear();
        capturedByBlack.clear();
        moveHistory.clear();
        undoStack.clear();
        currentTurn = PieceColor.WHITE;
        setupPieces();
    }

    /** Places all pieces in their standard starting positions. */
    private void setupPieces() {
        PieceType[] backRow = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
            PieceType.QUEEN, PieceType.KING,
            PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };
        for (int c = 0; c < 8; c++) {
            board[0][c] = new Piece(backRow[c], PieceColor.BLACK);
            board[1][c] = new Piece(PieceType.PAWN, PieceColor.BLACK);
            board[6][c] = new Piece(PieceType.PAWN, PieceColor.WHITE);
            board[7][c] = new Piece(backRow[c], PieceColor.WHITE);
        }
    }

    /**
     * Moves a piece from one square to another, handling captures and recording history.
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     * @return the captured Piece, or null if destination was empty
     */
    public Piece movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        undoStack.push(deepCopy(board));

        Piece moving   = board[fromRow][fromCol];
        Piece captured = board[toRow][toCol];

        if (captured != null) {
            if (moving.getColor() == PieceColor.WHITE)
                capturedByWhite.add(captured);
            else
                capturedByBlack.add(captured);
        }

        board[toRow][toCol]     = moving;
        board[fromRow][fromCol] = null;

        String note = moving.getColor() + " " + moving.getType()
            + ": " + toNotation(fromRow, fromCol)
            + " -> " + toNotation(toRow, toCol)
            + (captured != null ? " x" + captured.getType() : "");
        moveHistory.add(note);

        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return captured;
    }

    /**
     * Undoes the last move, restoring the board and adjusting captured piece lists.
     * @return true if undo succeeded, false if nothing to undo
     */
    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        board = undoStack.pop();
        if (!moveHistory.isEmpty()) {
            String last = moveHistory.remove(moveHistory.size() - 1);
            if (last.contains(" x")) {
                String capturedTypeName = last.substring(last.indexOf(" x") + 2);
                PieceColor mover = last.startsWith("WHITE") ? PieceColor.WHITE : PieceColor.BLACK;
                List<Piece> list = (mover == PieceColor.WHITE) ? capturedByWhite : capturedByBlack;
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (list.get(i).getType().toString().equals(capturedTypeName)) {
                        list.remove(i);
                        break;
                    }
                }
            }
        }
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return true;
    }

    /**
     * Converts row/col indices to chess notation (e.g. row=7, col=4 -> "E1").
     * @param row board row (0=top)
     * @param col board column (0=left)
     * @return algebraic notation string
     */
    public static String toNotation(int row, int col) {
        return String.valueOf((char)('A' + col)) + (8 - row);
    }

    /** @return piece at given square, or null if empty */
    public Piece getPiece(int row, int col) { return board[row][col]; }

    /** @return unmodifiable move history list */
    public List<String> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }

    /** @return pieces captured by white */
    public List<Piece> getCapturedByWhite() { return Collections.unmodifiableList(capturedByWhite); }

    /** @return pieces captured by black */
    public List<Piece> getCapturedByBlack() { return Collections.unmodifiableList(capturedByBlack); }

    /** @return the color whose turn it currently is */
    public PieceColor getCurrentTurn() { return currentTurn; }

    /** @return true if there is at least one move to undo */
    public boolean canUndo() { return !undoStack.isEmpty(); }

    /** Deep copies the board array for undo snapshots. */
    private Piece[][] deepCopy(Piece[][] src) {
        Piece[][] copy = new Piece[8][8];
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                copy[r][c] = src[r][c];
        return copy;
    }

    /**
     * Saves this model to a file using Java serialization.
     * @param path file path to save to
     * @throws IOException if writing fails
     */
    public void saveToFile(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads a BoardModel from a serialized file.
     * @param path file path to load from
     * @return deserialized BoardModel
     * @throws IOException            if reading fails
     * @throws ClassNotFoundException if class is not found
     */
    public static BoardModel loadFromFile(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (BoardModel) ois.readObject();
        }
    }
}
