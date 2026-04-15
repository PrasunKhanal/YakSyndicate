# Chess Game GUI — YakSyndicate
**Submission:** Phase 2 (Second Submission)

## Project: Chess Game GUI

This project is a Swing-based two-player chess GUI, building upon the foundational logic established in the Phase 1 console version.

## Environment & Dependencies

- Java OpenJDK 21 (Debian/Kali Linux 64-bit)
- No external libraries required

Check your Java version:
```bash
java -version
```

## Project Structure

```
chess-game-gui/
├── README.md
├── out/
└── src/
    ├── model/
    │   ├── PieceType.java
    │   ├── PieceColor.java
    │   ├── Piece.java
    │   └── BoardModel.java
    └── gui/
        ├── BoardSettings.java
        ├── BoardPanel.java
        ├── HistoryPanel.java
        ├── SettingsDialog.java
        ├── GameWindow.java
        └── Main.java
```

## How to Compile

```bash
mkdir -p chess-game-gui/out
javac -d chess-game-gui/out chess-game-gui/src/model/*.java chess-game-gui/src/gui/*.java
```

## How to Run

```bash
java -cp chess-game-gui/out gui.Main
```

## Features

### Core (Phase 2 Requirements)

- 8x8 Chessboard: Rendered using Java Swing.
- Unicode Rendering: Pieces displayed using standard Unicode chess symbols.
- Interaction: Full click-to-move and drag-and-drop support.
- Game State: Turn-based movement (White vs Black) with capture logic.
- Endgame: King capture triggers a declaration of the winner via popup.

### Extra Features Implemented

- Menu Bar: Includes New Game, Save Game, Load Game, and Quit functionality.
- Game History Panel:
  - Real-time move logging.
  - Captured pieces tracker.
  - Undo functionality.
- Settings Window:
  - Customizable board color schemes (light/dark squares).
  - Adjustable board UI scaling (Small/Medium/Large).

## Notes

- Validation: This phase does not include strict move validation (ruleset enforcement).
- Endgame: The game terminates when a King is captured.
- Persistence: Save/Load functionality utilizes Java serialization with `.chess` file extensions.

