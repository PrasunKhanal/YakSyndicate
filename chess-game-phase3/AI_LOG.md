
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
