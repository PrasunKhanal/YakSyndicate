package model;
import java.io.*;
import java.util.*;

/**
 * Core game state: 8x8 board, captured pieces, move history, undo stack,
 * en-passant target, and castling rights.
 * All rule enforcement is delegated to MoveValidator.
 */
public class BoardModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Piece[][] board;
    private PieceColor currentTurn;
    private List<Piece> capturedByWhite;
    private List<Piece> capturedByBlack;
    private List<String> moveHistory;
    private Deque<GameState> undoStack;

    /** En-passant target square [row, col], or null if none. */
    private int[] enPassantTarget;

    /** Castling rights: [whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide] */
    private boolean[] castlingRights;

    /** Constructs a fresh game at the standard starting position. */
    public BoardModel() {
        capturedByWhite = new ArrayList<>();
        capturedByBlack = new ArrayList<>();
        moveHistory     = new ArrayList<>();
        undoStack       = new ArrayDeque<>();
        reset();
    }

    /** Resets the board to the starting position and clears all state. */
    public void reset() {
        board = new Piece[8][8];
        capturedByWhite.clear(); capturedByBlack.clear();
        moveHistory.clear(); undoStack.clear();
        currentTurn    = PieceColor.WHITE;
        enPassantTarget = null;
        castlingRights  = new boolean[]{true, true, true, true};
        setupPieces();
    }

    private void setupPieces() {
        PieceType[] back = { PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                             PieceType.QUEEN, PieceType.KING,
                             PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK };
        for (int c = 0; c < 8; c++) {
            board[0][c] = new Piece(back[c], PieceColor.BLACK);
            board[1][c] = new Piece(PieceType.PAWN, PieceColor.BLACK);
            board[6][c] = new Piece(PieceType.PAWN, PieceColor.WHITE);
            board[7][c] = new Piece(back[c], PieceColor.WHITE);
        }
    }

    /**
     * Executes a move. Caller must ensure the move is legal (via MoveValidator).
     * Handles captures, pawn promotion (auto-queen), en passant, and castling.
     *
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     * @return the captured piece, or null
     */
    public Piece movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        undoStack.push(new GameState(board, currentTurn, enPassantTarget,
                                     castlingRights, capturedByWhite, capturedByBlack,
                                     new ArrayList<>(moveHistory)));

        Piece moving   = board[fromRow][fromCol];
        Piece captured = board[toRow][toCol];
        int[] prevEP   = enPassantTarget;
        enPassantTarget = null;

        // En passant capture
        if (moving.getType() == PieceType.PAWN && prevEP != null
                && toRow == prevEP[0] && toCol == prevEP[1]) {
            int captRow = (moving.getColor() == PieceColor.WHITE) ? toRow + 1 : toRow - 1;
            captured = board[captRow][toCol];
            board[captRow][toCol] = null;
        }

        // Set en passant target on double pawn push
        if (moving.getType() == PieceType.PAWN && Math.abs(toRow - fromRow) == 2) {
            enPassantTarget = new int[]{ (fromRow + toRow) / 2, fromCol };
        }

        // Castling
        if (moving.getType() == PieceType.KING && Math.abs(toCol - fromCol) == 2) {
            if (toCol == 6) { board[fromRow][5] = board[fromRow][7]; board[fromRow][7] = null; }
            else            { board[fromRow][3] = board[fromRow][0]; board[fromRow][0] = null; }
        }

        // Update castling rights
        updateCastlingRights(fromRow, fromCol, moving);

        board[toRow][toCol]     = moving;
        board[fromRow][fromCol] = null;

        // Pawn promotion — auto queen
        if (moving.getType() == PieceType.PAWN && (toRow == 0 || toRow == 7)) {
            board[toRow][toCol] = new Piece(PieceType.QUEEN, moving.getColor());
        }

        if (captured != null) {
            if (moving.getColor() == PieceColor.WHITE) capturedByWhite.add(captured);
            else                                        capturedByBlack.add(captured);
        }

        String note = moving.getColor() + " " + moving.getType()
            + ": " + toNotation(fromRow, fromCol) + "->" + toNotation(toRow, toCol)
            + (captured != null ? " x" + captured.getType() : "");
        moveHistory.add(note);

        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return captured;
    }

    private void updateCastlingRights(int fromRow, int fromCol, Piece p) {
        if (p.getType() == PieceType.KING) {
            if (p.getColor() == PieceColor.WHITE) { castlingRights[0] = false; castlingRights[1] = false; }
            else                                   { castlingRights[2] = false; castlingRights[3] = false; }
        }
        if (p.getType() == PieceType.ROOK) {
            if (fromRow == 7 && fromCol == 7) castlingRights[0] = false;
            if (fromRow == 7 && fromCol == 0) castlingRights[1] = false;
            if (fromRow == 0 && fromCol == 7) castlingRights[2] = false;
            if (fromRow == 0 && fromCol == 0) castlingRights[3] = false;
        }
    }

    /**
     * Reverts the last move using the undo stack.
     * @return true if undo succeeded
     */
    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        GameState s = undoStack.pop();
        board           = s.board;
        currentTurn     = s.turn;
        enPassantTarget = s.enPassantTarget;
        castlingRights  = s.castlingRights;
        capturedByWhite = s.capturedByWhite;
        capturedByBlack = s.capturedByBlack;
        moveHistory     = s.moveHistory;
        return true;
    }

    /** @return piece at (row, col), or null */
    public Piece getPiece(int row, int col) { return board[row][col]; }

    /** @return whose turn it is */
    public PieceColor getCurrentTurn() { return currentTurn; }

    /** @return en-passant target square, or null */
    public int[] getEnPassantTarget() { return enPassantTarget; }

    /** @return castling rights array [wKS, wQS, bKS, bQS] */
    public boolean[] getCastlingRights() { return castlingRights; }

    /** @return unmodifiable move history */
    public List<String> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }

    /** @return pieces captured by white */
    public List<Piece> getCapturedByWhite() { return Collections.unmodifiableList(capturedByWhite); }

    /** @return pieces captured by black */
    public List<Piece> getCapturedByBlack() { return Collections.unmodifiableList(capturedByBlack); }

    /** @return true if undo stack is non-empty */
    public boolean canUndo() { return !undoStack.isEmpty(); }

    /** Converts row/col to algebraic notation. */
    public static String toNotation(int row, int col) {
        return String.valueOf((char)('A' + col)) + (8 - row);
    }

    /** Saves game state to a file via serialization. */
    public void saveToFile(String path) throws IOException {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(path))) { o.writeObject(this); }
    }

    /** Loads game state from a serialized file. */
    public static BoardModel loadFromFile(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream i = new ObjectInputStream(new FileInputStream(path))) { return (BoardModel) i.readObject(); }
    }

    /** Immutable snapshot for undo. */
    private static class GameState implements Serializable {
        final Piece[][] board;
        final PieceColor turn;
        final int[] enPassantTarget;
        final boolean[] castlingRights;
        final List<Piece> capturedByWhite, capturedByBlack;
        final List<String> moveHistory;

        GameState(Piece[][] b, PieceColor t, int[] ep, boolean[] cr,
                  List<Piece> cbw, List<Piece> cbb, List<String> mh) {
            board = new Piece[8][8];
            for (int r = 0; r < 8; r++) board[r] = b[r].clone();
            turn = t;
            enPassantTarget = ep == null ? null : ep.clone();
            castlingRights  = cr.clone();
            capturedByWhite = new ArrayList<>(cbw);
            capturedByBlack = new ArrayList<>(cbb);
            moveHistory     = mh;
        }
    }
}
