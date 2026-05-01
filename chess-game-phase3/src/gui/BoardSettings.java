package gui;
import java.awt.Color;
import java.io.Serializable;

/** User-configurable board appearance. */
public class BoardSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    public Color lightSquare = new Color(240, 217, 181);
    public Color darkSquare  = new Color(181, 136, 99);
    public int   squareSize  = 80;
    public float pieceFontSize = 48f;
}
