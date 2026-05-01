package ai;

import model.*;
import java.util.*;

/**
 * Chess AI using minimax with alpha-beta pruning.
 * Evaluates positions using material balance and basic positional bonuses.
 * Plays as the color specified at construction time.
 */
public class ChessAI {

    private static final int DEPTH = 3;
    private final PieceColor aiColor;
    private final PieceColor humanColor;

    /**
     * @param aiColor the color this AI plays as
     */
    public ChessAI(PieceColor aiColor) {
        this.aiColor    = aiColor;
        this.humanColor = (aiColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    /**
     * Selects the best move for the AI using minimax with alpha-beta pruning.
     * @param model current board state
     * @return int[4] = {fromRow, fromCol, toRow, toCol}, or null if no moves exist
     */
    public int[] getBestMove(BoardModel model) {
        List<int[]> allMoves = getAllMoves(model, aiColor);
        if (allMoves.isEmpty()) return null;

        // Order: captures first for better pruning
        allMoves.sort((a, b) -> {
            Piece ta = model.getPiece(a[2], a[3]);
            Piece tb = model.getPiece(b[2], b[3]);
            return (tb != null ? tb.getValue() : 0) - (ta != null ? ta.getValue() : 0);
        });

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = allMoves.get(0);

        for (int[] move : allMoves) {
            model.movePiece(move[0], move[1], move[2], move[3]);
            int score = minimax(model, DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            model.undo();
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    /**
     * Minimax with alpha-beta pruning.
     * @param model  current board state
     * @param depth  remaining search depth
     * @param alpha  alpha bound
     * @param beta   beta bound
     * @param maximizing true if it is the AI's turn to maximize
     * @return evaluated score
     */
    private int minimax(BoardModel model, int depth, int alpha, int beta, boolean maximizing) {
        PieceColor turn = maximizing ? aiColor : humanColor;

        if (MoveValidator.hasNoLegalMoves(model, turn)) {
            if (MoveValidator.isInCheck(model, turn))
                return maximizing ? -100000 + depth : 100000 - depth; // checkmate
            return 0; // stalemate
        }
        if (depth == 0) return evaluate(model);

        List<int[]> moves = getAllMoves(model, turn);
        // Capture-first ordering
        moves.sort((a, b) -> {
            Piece ta = model.getPiece(a[2], a[3]);
            Piece tb = model.getPiece(b[2], b[3]);
            return (tb != null ? tb.getValue() : 0) - (ta != null ? ta.getValue() : 0);
        });

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] m : moves) {
                model.movePiece(m[0], m[1], m[2], m[3]);
                best = Math.max(best, minimax(model, depth - 1, alpha, beta, false));
                model.undo();
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] m : moves) {
                model.movePiece(m[0], m[1], m[2], m[3]);
                best = Math.min(best, minimax(model, depth - 1, alpha, beta, true));
                model.undo();
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    /**
     * Material + simple center-control evaluation function.
     * Positive = good for AI, negative = good for human.
     * @param model board to evaluate
     * @return heuristic score
     */
    private int evaluate(BoardModel model) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = model.getPiece(r, c);
                if (p == null) continue;
                int val = p.getValue() + centerBonus(r, c, p.getType());
                if (p.getColor() == aiColor) score += val;
                else                          score -= val;
            }
        }
        return score;
    }

    /** Small positional bonus for center control. */
    private int centerBonus(int r, int c, PieceType type) {
        if (type == PieceType.KING) return 0;
        int dr = Math.abs(r - 3) + Math.abs(r - 4);
        int dc = Math.abs(c - 3) + Math.abs(c - 4);
        return Math.max(0, 10 - (dr + dc) * 2);
    }

    /** Collects all legal moves for the given color. */
    private List<int[]> getAllMoves(BoardModel model, PieceColor color) {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = model.getPiece(r, c);
                if (p == null || p.getColor() != color) continue;
                for (int[] dest : MoveValidator.getLegalMoves(model, r, c))
                    moves.add(new int[]{r, c, dest[0], dest[1]});
            }
        return moves;
    }
}
