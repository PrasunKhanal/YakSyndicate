package gui;

import java.awt.Color;
import java.io.Serializable;

/**
 * Stores user-configurable appearance settings for the chess board and pieces.
 */
public class BoardSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Color of the light squares. */
    public Color lightSquare = new Color(240, 217, 181);

    /** Color of the dark squares. */
    public Color darkSquare = new Color(181, 136, 99);

    /** Pixel size of each square. */
    public int squareSize = 80;

    /** Font size used to render piece Unicode symbols. */
    public float pieceFontSize = 48f;
}
