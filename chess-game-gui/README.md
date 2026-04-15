# Chess Game GUI — YakSyndicate

A Swing-based two-player chess GUI (Phase 2) built on top of the Phase 1 console version.

## Environment & Dependencies
- Java OpenJDK 21 (Linux 64-bit)
- No external libraries required

Check Java version:
java -version

## Project Structure
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

## How to Compile
mkdir -p chess-game-gui/out
javac -d chess-game-gui/out chess-game-gui/src/model/*.java chess-game-gui/src/gui/*.java

## How to Run
java -cp chess-game-gui/out gui.Main

## Features

### Core (Phase 2 Requirements)
- 8x8 chessboard rendered using Java Swing
- Pieces displayed using Unicode chess symbols
- Click-to-move and drag-and-drop support
- Turn-based movement (White vs Black)
- Capture logic (pieces removed when captured)
- King capture triggers endgame popup

### Extra Features Implemented
- Menu Bar (New Game, Save Game, Load Game, Quit)
- Game History Panel:
  - Move log displayed in real time
  - Captured pieces tracked and displayed
  - Undo functionality
- Settings Window:
  - Customize board colors (light/dark squares)
  - Adjustable board size (small/medium/large)

## Notes
- No strict move validation (per Phase 2 scope)
- Game ends when a King is captured
- Save/Load uses Java serialization (.chess files)

