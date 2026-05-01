package model;
import java.util.*;

/**
 * Static utility that generates all legal moves for a piece on a given board.
 * A move is legal if it does not leave the moving player's King in check.
 * This class enforces all standard chess rules including castling, en passant,
 * pawn promotion, check, and checkmate detection.
 */
public class MoveValidator {

    /**
     * Returns all legal destination squares for the piece at (row, col).
     * @param model the current board state
     * @param row   piece row
     * @param col   piece column
     * @return list of [row, col] pairs representing legal destinations
     */
    public static List<int[]> getLegalMoves(BoardModel model, int row, int col) {
        Piece piece = model.getPiece(row, col);
        if (piece == null) return Collections.emptyList();

        List<int[]> candidates = getPseudoLegalMoves(model, row, col);
        List<int[]> legal = new ArrayList<>();

        for (int[] dest : candidates) {
            if (!moveLeavesKingInCheck(model, row, col, dest[0], dest[1])) {
                legal.add(dest);
            }
        }
        return legal;
    }

    /**
     * Generates pseudo-legal moves (ignores check) for routing per piece type.
     */
    private static List<int[]> getPseudoLegalMoves(BoardModel model, int row, int col) {
        Piece piece = model.getPiece(row, col);
        switch (piece.getType()) {
            case PAWN:   return pawnMoves(model, row, col, piece.getColor());
            case ROOK:   return slidingMoves(model, row, col, piece.getColor(),
                             new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
            case BISHOP: return slidingMoves(model, row, col, piece.getColor(),
                             new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
            case QUEEN:  return slidingMoves(model, row, col, piece.getColor(),
                             new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}});
            case KNIGHT: return knightMoves(model, row, col, piece.getColor());
            case KING:   return kingMoves(model, row, col, piece.getColor());
            default:     return Collections.emptyList();
        }
    }

    private static List<int[]> pawnMoves(BoardModel model, int row, int col, PieceColor color) {
        List<int[]> moves = new ArrayList<>();
        int dir      = (color == PieceColor.WHITE) ? -1 : 1;
        int startRow = (color == PieceColor.WHITE) ? 6 : 1;

        // One step forward
        if (inBounds(row + dir, col) && model.getPiece(row + dir, col) == null) {
            moves.add(new int[]{row + dir, col});
            // Two steps from start
            if (row == startRow && model.getPiece(row + 2 * dir, col) == null)
                moves.add(new int[]{row + 2 * dir, col});
        }
        // Diagonal captures
        for (int dc : new int[]{-1, 1}) {
            int nr = row + dir, nc = col + dc;
            if (!inBounds(nr, nc)) continue;
            Piece target = model.getPiece(nr, nc);
            if (target != null && target.getColor() != color) moves.add(new int[]{nr, nc});
            // En passant
            int[] ep = model.getEnPassantTarget();
            if (ep != null && nr == ep[0] && nc == ep[1]) moves.add(new int[]{nr, nc});
        }
        return moves;
    }

    private static List<int[]> slidingMoves(BoardModel model, int row, int col,
                                             PieceColor color, int[][] dirs) {
        List<int[]> moves = new ArrayList<>();
        for (int[] d : dirs) {
            int r = row + d[0], c = col + d[1];
            while (inBounds(r, c)) {
                Piece t = model.getPiece(r, c);
                if (t == null) { moves.add(new int[]{r, c}); }
                else {
                    if (t.getColor() != color) moves.add(new int[]{r, c});
                    break;
                }
                r += d[0]; c += d[1];
            }
        }
        return moves;
    }

    private static List<int[]> knightMoves(BoardModel model, int row, int col, PieceColor color) {
        List<int[]> moves = new ArrayList<>();
        int[][] jumps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] j : jumps) {
            int r = row + j[0], c = col + j[1];
            if (inBounds(r, c)) {
                Piece t = model.getPiece(r, c);
                if (t == null || t.getColor() != color) moves.add(new int[]{r, c});
            }
        }
        return moves;
    }

    private static List<int[]> kingMoves(BoardModel model, int row, int col, PieceColor color) {
        List<int[]> moves = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc;
                if (inBounds(r, c)) {
                    Piece t = model.getPiece(r, c);
                    if (t == null || t.getColor() != color) moves.add(new int[]{r, c});
                }
            }
        // Castling
        boolean[] cr = model.getCastlingRights();
        int kSide = (color == PieceColor.WHITE) ? 0 : 2;
        int qSide = kSide + 1;
        // King-side
        if (cr[kSide] && model.getPiece(row, 5) == null && model.getPiece(row, 6) == null
                && !isSquareAttacked(model, row, 4, color)
                && !isSquareAttacked(model, row, 5, color)
                && !isSquareAttacked(model, row, 6, color))
            moves.add(new int[]{row, 6});
        // Queen-side
        if (cr[qSide] && model.getPiece(row, 3) == null && model.getPiece(row, 2) == null
                && model.getPiece(row, 1) == null
                && !isSquareAttacked(model, row, 4, color)
                && !isSquareAttacked(model, row, 3, color)
                && !isSquareAttacked(model, row, 2, color))
            moves.add(new int[]{row, 2});
        return moves;
    }

    /**
     * Simulates a move and checks if it leaves the moving player's King in check.
     */
    private static boolean moveLeavesKingInCheck(BoardModel model,
            int fromRow, int fromCol, int toRow, int toCol) {
        Piece moving = model.getPiece(fromRow, fromCol);
        Piece[][] sim = simulateMove(model, fromRow, fromCol, toRow, toCol);
        return isKingInCheck(sim, moving.getColor());
    }

    private static Piece[][] simulateMove(BoardModel model,
            int fromRow, int fromCol, int toRow, int toCol) {
        Piece[][] sim = new Piece[8][8];
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                sim[r][c] = model.getPiece(r, c);
        // En passant removal
        Piece moving = sim[fromRow][fromCol];
        if (moving != null && moving.getType() == PieceType.PAWN) {
            int[] ep = model.getEnPassantTarget();
            if (ep != null && toRow == ep[0] && toCol == ep[1]) {
                int captRow = (moving.getColor() == PieceColor.WHITE) ? toRow + 1 : toRow - 1;
                sim[captRow][toCol] = null;
            }
        }
        sim[toRow][toCol]     = sim[fromRow][fromCol];
        sim[fromRow][fromCol] = null;
        return sim;
    }

    private static boolean isKingInCheck(Piece[][] board, PieceColor color) {
        int kr = -1, kc = -1;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != null && board[r][c].getType() == PieceType.KING
                        && board[r][c].getColor() == color) { kr = r; kc = c; }
        if (kr < 0) return true; // king not found = treated as in check
        return isSquareAttackedOnBoard(board, kr, kc, color);
    }

    /**
     * Checks if a square is attacked by any opponent piece (on model).
     */
    public static boolean isSquareAttacked(BoardModel model, int row, int col, PieceColor friendlyColor) {
        Piece[][] board = new Piece[8][8];
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = model.getPiece(r, c);
        return isSquareAttackedOnBoard(board, row, col, friendlyColor);
    }

    private static boolean isSquareAttackedOnBoard(Piece[][] board, int row, int col, PieceColor friendly) {
        PieceColor enemy = (friendly == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        // Check knight attacks
        int[][] knightJumps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] j : knightJumps) {
            int r = row + j[0], c = col + j[1];
            if (inBounds(r, c) && board[r][c] != null
                    && board[r][c].getColor() == enemy
                    && board[r][c].getType() == PieceType.KNIGHT) return true;
        }
        // Check sliding attacks (rook/queen on straights, bishop/queen on diagonals)
        int[][][] dirs = {
            {{1,0},{-1,0},{0,1},{0,-1}},
            {{1,1},{1,-1},{-1,1},{-1,-1}}
        };
        PieceType[][] sliders = {
            {PieceType.ROOK, PieceType.QUEEN},
            {PieceType.BISHOP, PieceType.QUEEN}
        };
        for (int i = 0; i < 2; i++) {
            for (int[] d : dirs[i]) {
                int r = row + d[0], c = col + d[1];
                while (inBounds(r, c)) {
                    Piece t = board[r][c];
                    if (t != null) {
                        if (t.getColor() == enemy && (t.getType() == sliders[i][0]
                                || t.getType() == sliders[i][1])) return true;
                        break;
                    }
                    r += d[0]; c += d[1];
                }
            }
        }
        // Check pawn attacks
        int pawnDir = (friendly == PieceColor.WHITE) ? -1 : 1;
        for (int dc : new int[]{-1, 1}) {
            int r = row + pawnDir, c = col + dc;
            if (inBounds(r, c) && board[r][c] != null
                    && board[r][c].getColor() == enemy
                    && board[r][c].getType() == PieceType.PAWN) return true;
        }
        // Check king attacks
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc;
                if (inBounds(r, c) && board[r][c] != null
                        && board[r][c].getColor() == enemy
                        && board[r][c].getType() == PieceType.KING) return true;
            }
        return false;
    }

    /**
     * Returns true if the given color is currently in check.
     * @param model board state
     * @param color color to check
     * @return true if in check
     */
    public static boolean isInCheck(BoardModel model, PieceColor color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = model.getPiece(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color)
                    return isSquareAttacked(model, r, c, color);
            }
        return false;
    }

    /**
     * Returns true if the given color has no legal moves (checkmate or stalemate).
     * @param model board state
     * @param color color to check
     * @return true if no legal moves exist
     */
    public static boolean hasNoLegalMoves(BoardModel model, PieceColor color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = model.getPiece(r, c);
                if (p != null && p.getColor() == color
                        && !getLegalMoves(model, r, c).isEmpty()) return false;
            }
        return true;
    }

    private static boolean inBounds(int r, int c) { return r >= 0 && r < 8 && c >= 0 && c < 8; }
}
