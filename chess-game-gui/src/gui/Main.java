package gui;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Chess Game GUI application.
 * Launches the GameWindow on the Swing Event Dispatch Thread.
 */
public class Main {
    /**
     * Main method. Starts the GUI.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameWindow::new);
    }
}
