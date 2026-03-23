# Chess Game — YakSyndicate

A console-based two-player chess game written in Java.

## Environment & Dependencies

- Java OpenJDK 21 (Debian/Kali Linux 64-bit)
- No external libraries required

Check your Java version:
```bash
java -version
```

## Project Structure

```
chess-game/
├── README.md
├── out/                        ← compiled class files
└── src/
    ├── board/
    │   ├── Board.java          ← 8x8 board, move execution, check/checkmate
    │   └── Position.java       ← row/col representation, notation conversion
    ├── pieces/
    │   ├── Piece.java          ← abstract base class for all pieces
    │   ├── Pawn.java
    │   ├── Rook.java
    │   ├── Knight.java
    │   ├── Bishop.java
    │   ├── Queen.java
    │   └── King.java
    ├── game/
    │   ├── Game.java           ← main game loop, turn management
    │   └── Player.java         ← input handling, move validation
    └── utils/
        └── Utils.java          ← utility helpers (reserved for future use)
```

## How to Compile

From the root of the repository:
```bash
mkdir -p chess-game/out
javac -d chess-game/out chess-game/src/board/Position.java chess-game/src/board/Board.java chess-game/src/pieces/Piece.java chess-game/src/pieces/Pawn.java chess-game/src/pieces/Rook.java chess-game/src/pieces/Knight.java chess-game/src/pieces/Bishop.java chess-game/src/pieces/Queen.java chess-game/src/pieces/King.java chess-game/src/game/Player.java chess-game/src/game/Game.java
```

## How to Run

```bash
java -cp chess-game/out game.Game
```

## How to Play

- Two players take turns entering moves in the console
- Move format: `E2 E4` (from square, space, to square)
- Letters A-H represent columns, numbers 1-8 represent rows
- White always goes first

### Example moves
```
E2 E4   — move pawn from E2 to E4
D1 H5   — move queen from D1 to H5
B1 C3   — move knight from B1 to C3
```

## Sample Board Display

```
   A   B   C   D   E   F   G   H
8  bR  bN  bB  bQ  bK  bB  bN  bR
7  bp  bp  bp  bp  bp  bp  bp  bp
6      ##      ##      ##      ##
5  ##      ##      ##      ##
4      ##      ##      ##      ##
3  ##      ##      ##      ##
2  wp  wp  wp  wp  wp  wp  wp  wp
1  wR  wN  wB  wQ  wK  wB  wN  wR
```

## Features

### Implemented
- [x] 8x8 board with correct initial piece setup
- [x] Console board display with dark/light squares
- [x] All 6 piece types with correct movement rules
- [x] Turn-based gameplay alternating white and black
- [x] Move input in standard chess notation (E2 E4)
- [x] Input validation with clear error messages
- [x] Capture detection
- [x] Check detection
- [x] Checkmate detection

### Not Implemented
- [ ] Castling (O-O and O-O-O)
- [ ] Pawn promotion
- [ ] Stalemate detection
- [ ] Draw conditions
- [ ] Move history / undo

## Known Limitations

- No stalemate check — game may loop if neither side can checkmate
- Pawn promotion not yet supported — pawn stays a pawn on the back rank
