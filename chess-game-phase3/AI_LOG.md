## Session 1 — Project scaffold and design
**What was asked:** How to structure Phase 3 to integrate Phase 1 backend with Phase 2 GUI cleanly without rewriting everything.
**Suggestions adopted:** Separate `model/` package handles all rules and state; `gui/` package only reads from model and calls `model.movePiece()`. GUI never computes chess logic directly.
**Changes made by us:** Decided to keep Phase 1 piece classes and extend them rather than replace them. Chose to add a dedicated `ai/` package to keep AI logic fully separated.

---

## Session 2 — Move validation architecture
**What was asked:** Best way to generate legal moves per piece type in Java, including check detection.
**Suggestions adopted:** `MoveValidator.java` as a static utility class that takes a `BoardModel` and returns `List<int[]>` of legal destination squares for a given piece.
**Changes made by us:** Added pin detection manually after AI suggestion missed discovered check edge case. Kept the method signature simple so `BoardPanel` can call it directly on click.

---

## Session 3 — Check and checkmate detection
**What was asked:** How to detect checkmate vs stalemate efficiently without slowing the GUI.
**Suggestions adopted:** After every move, call `isInCheck()` then iterate all pieces of the current player — if no piece has any legal moves, it is either checkmate (in check) or stalemate (not in check).
**Changes made by us:** Wrapped detection inside `GameWindow.checkEndOfGame()` and called it after both human and AI moves. Added a red King highlight in `BoardPanel.paintComponent()` for visual check feedback.

---

## Session 4 — AI opponent design
**What was asked:** How to implement a chess AI in Java suitable for a class project — not too complex but genuinely plays chess.
**Suggestions adopted:** Minimax with alpha-beta pruning at depth 3. Material evaluation function using standard piece values. Move ordering heuristic: captures first for better pruning efficiency.
**Changes made by us:** Reduced default depth to 3 as a balance between speed and strength. Added a center control bonus to the evaluation function ourselves. Wrote `Piece.getValue()` manually to feed material scores into the AI.

---

## Session 5 — GUI integration and legal move highlighting
**What was asked:** How to show legal move highlights in the Swing board panel without coupling GUI to rule logic.
**Suggestions adopted:** `BoardPanel` calls `MoveValidator.getLegalMoves(model, row, col)` on click, stores the result in a local list, and colors those squares inside `paintComponent()`.
**Changes made by us:** Changed highlight style to semi-transparent blue dots for empty squares and a red ring outline for capturable enemy pieces, instead of full square fills, for better readability during play.


---

## Session 6 — Threading for AI opponent
**What was asked:** How to run AI move computation without freezing the Swing GUI.
**Suggestions adopted:** SwingWorker<int[], Void> — doInBackground() runs minimax, done() applies the move on the EDT.
**Changes made by us:** Added null check on AI result in case of stalemate/no moves.

---

## Session 7 — Undo behavior with AI opponent
**What was asked:** When playing vs AI, undo should revert both the AI's move and the human's move so the human gets their turn back.
**Suggestions adopted:** Call model.undo() twice when vsAI is true.
**Changes made by us:** Added the conditional double-undo in GameWindow.onUndoRequested().
