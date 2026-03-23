package game;

import board.Board;
import board.Position;
import pieces.Piece;
import java.util.Scanner;

/**
 * Represents a chess player.
 * Handles move input and validation from the console.
 */
public class Player {

    /** The color of this player, either "white" or "black". */
    private String color;

    /** Scanner for reading player input from console. */
    private Scanner scanner;

    /**
     * Constructs a Player with a given color.
     * @param color "white" or "black"
     */
    public Player(String color) {
        this.color = color;
        this.scanner = new Scanner(System.in);
    }

    /** @return the color of this player */
    public String getColor() { return color; }

    /**
     * Prompts the player to enter a move and attempts to execute it on the board.
     * Keeps prompting until a valid move is entered.
     * @param board the current board
     */
    public void makeMove(Board board) {
        while (true) {
            System.out.print(color + "'s move (e.g. E2 E4): ");
            String input = scanner.nextLine().trim().toUpperCase();

            // Validate format
            if (!isValidFormat(input)) {
                System.out.println("Invalid format. Use format like 'E2 E4'.");
                continue;
            }

            String[] parts = input.split("\\s+");
            Position from = new Position(parts[0]);
            Position to = new Position(parts[1]);

            // Check bounds
            if (!from.isValid() || !to.isValid()) {
                System.out.println("Position out of bounds. Try again.");
                continue;
            }

            // Check there is a piece at from
            Piece piece = board.getPiece(from);
            if (piece == null) {
                System.out.println("No piece at " + parts[0] + ". Try again.");
                continue;
            }

            // Check the piece belongs to this player
            if (!piece.getColor().equals(color)) {
                System.out.println("That is not your piece. Try again.");
                continue;
            }

            // Check the move is in the piece's possible moves
            if (!piece.possibleMoves(board.getBoard()).contains(to)) {
                System.out.println("Invalid move for that piece. Try again.");
                continue;
            }

            // Execute the move
            board.movePiece(from, to);
            break;
        }
    }

    /**
     * Validates that input matches the expected chess move format (e.g. "E2 E4").
     * @param input raw input string
     * @return true if format is valid
     */
    private boolean isValidFormat(String input) {
        return input.matches("[A-H][1-8]\\s+[A-H][1-8]");
    }
}
