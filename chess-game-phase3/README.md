# Chess Game Phase 3 — YakSyndicate
**Submission:** Phase 3 (Final Submission)

## Project: Chess Game — Full Integration

This project is the final integrated version of the YakSyndicate chess game, combining the Phase 1 backend rule engine with the Phase 2 Swing GUI into a fully functional, rule-enforcing chess application.

## Environment & Dependencies
- Java OpenJDK 21 (Debian/Kali Linux 64-bit)
- No external libraries required

Check your Java version:
```bash
java -version
```

## Project Structure
chess-game-phase3/
├── README.md
├── AI_LOG.md
├── out/
└── src/
├── model/
│   ├── PieceType.java
│   ├── PieceColor.java
│   ├── Piece.java
│   ├── BoardModel.java
│   └── MoveValidator.java
├── ai/
│   └── ChessAI.java
└── gui/
├── BoardSettings.java
├── BoardPanel.java
├── HistoryPanel.java
├── SettingsDialog.java
├── GameWindow.java
└── Main.java

## How to Compile
```bash
mkdir -p chess-game-phase3/out
javac -d chess-game-phase3/out chess-game-phase3/src/model/*.java chess-game-phase3/src/ai/*.java chess-game-phase3/src/gui/*.java
```

## How to Run
```bash
java -cp chess-game-phase3/out gui.Main
```

## How to Play
- Click a piece to select it — legal squares are highlighted in blue dots
- Click a highlighted square to move, or drag and drop directly
- Illegal moves are rejected automatically
- Red highlight on the King indicates check
- White always goes first

## Features

### Core (Phase 3 Requirements)
- Full Rule Enforcement: All piece movement rules enforced via `MoveValidator.java`.
- Check Detection: King is highlighted in red when in check.
- Checkmate Detection: Game ends with a winner popup when no legal moves remain under check.
- Stalemate Detection: Draw is declared when no legal moves exist outside of check.
- En Passant: Fully implemented for pawn captures.
- Castling: Both king-side and queen-side castling supported with rights tracking.
- Pawn Promotion: Pawns auto-promote to Queen on reaching the back rank.
- Consistent State: GUI updates correctly after every move, reflecting the true backend game state.

---

## ⭐ Extra Credit Features

### EC-B — AI Chess Opponent *(Overall Grade Extra Credit)*
- A computer opponent implemented using the **Minimax algorithm with Alpha-Beta Pruning** at depth 3.
- Enable it from **Game → Play vs AI (Black)** in the menu bar.
- The AI evaluates positions using material balance and center control bonuses.
- Move ordering (captures first) is applied for better pruning efficiency.
- AI runs on a background thread via `SwingWorker` so the GUI never freezes.
- Undo correctly reverts both the AI's move and the human's move together.

### EC-A — Git Process & Documentation Quality *(Project Portion Extra Credit)*
- Incremental commits reflecting real development stages: model → validator → AI → GUI → compile → docs.
- Descriptive commit messages with `[AI-assisted]` tags noting what was suggested vs written manually.
- `AI_LOG.md` documents all 7 AI consultation sessions with prompts, adopted suggestions, and manual changes.
- README professionally formatted with clear run instructions and feature breakdown.

---

## Notes
- Persistence: Save/Load uses Java serialization with `.chess` file extension via **Game → Save/Load Game**.
- Settings: Board colors and size are customizable via **View → Settings**.
- History Panel: Displays live move log, captured pieces, and an Undo button on the right side.
- Undo vs AI: When playing against the AI, Undo reverts two moves to restore the human's turn.
