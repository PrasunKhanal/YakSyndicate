package gui;
import javax.swing.SwingUtilities;

/**
 * Entry point. Launches GameWindow on the Event Dispatch Thread.
 */
public class Main {
    /** @param args unused */
    public static void main(String[] args) { SwingUtilities.invokeLater(GameWindow::new); }
}
