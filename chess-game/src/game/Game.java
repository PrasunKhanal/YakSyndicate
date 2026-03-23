package game;

import board.Board;

/**
 * Represents a chess game session.
 * Controls the main game loop, turn alternation, and end conditions.
 */
public class Game {

    /** The chess board for this game. */
    private Board board;

    /** The white player. */
    private Player white;

    /** The black player. */
    private Player black;

    /** The color whose turn it currently is. */
    private String currentTurn;

    /**
     * Constructs a Game with two players and a fresh board.
     */
    public Game() {
        board = new Board();
        white = new Player("white");
        black = new Player("black");
        currentTurn = "white";
    }

    /**
     * Starts and runs the main game loop.
     * Alternates turns, displays the board, checks for checkmate.
     */
    public void play() {
        System.out.println("Welcome to Chess!");
        System.out.println("Format: E2 E4 | Type 'quit' to exit");

        while (true) {
            board.display();

            // Check for checkmate before the turn
            if (board.isCheckmate(currentTurn)) {
                String winner = currentTurn.equals("white") ? "black" : "white";
                System.out.println("Checkmate! " + winner + " wins!");
                break;
            }

            // Warn if in check
            if (board.isCheck(currentTurn)) {
                System.out.println(currentTurn + " is in check!");
            }

            // Get move from current player
            Player current = currentTurn.equals("white") ? white : black;
            current.makeMove(board);

            // Switch turns
            currentTurn = currentTurn.equals("white") ? "black" : "white";
        }

        System.out.println("Game over.");
    }

    /**
     * Entry point — creates and starts a new game.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}
