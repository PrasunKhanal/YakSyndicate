package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog allowing players to customize board colors and square size.
 * Changes are applied in real time when the Apply button is clicked.
 */
public class SettingsDialog extends JDialog {

    private final BoardSettings settings;
    private final Runnable onApply;

    private JButton lightColorBtn;
    private JButton darkColorBtn;
    private JComboBox<String> sizeCombo;

    /**
     * Constructs the settings dialog.
     * @param owner    the parent frame
     * @param settings the shared settings object to modify
     * @param onApply  callback invoked when the user clicks Apply
     */
    public SettingsDialog(Frame owner, BoardSettings settings, Runnable onApply) {
        super(owner, "Settings", true);
        this.settings = settings;
        this.onApply  = onApply;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    /** Builds and lays out all UI components. */
    private void buildUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Light square color
        panel.add(new JLabel("Light Square Color:"));
        lightColorBtn = new JButton("  ");
        lightColorBtn.setBackground(settings.lightSquare);
        lightColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Choose Light Square Color", settings.lightSquare);
            if (c != null) { settings.lightSquare = c; lightColorBtn.setBackground(c); }
        });
        panel.add(lightColorBtn);

        // Dark square color
        panel.add(new JLabel("Dark Square Color:"));
        darkColorBtn = new JButton("  ");
        darkColorBtn.setBackground(settings.darkSquare);
        darkColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Choose Dark Square Color", settings.darkSquare);
            if (c != null) { settings.darkSquare = c; darkColorBtn.setBackground(c); }
        });
        panel.add(darkColorBtn);

        // Board size
        panel.add(new JLabel("Board Size:"));
        sizeCombo = new JComboBox<>(new String[]{"Small (60px)", "Medium (80px)", "Large (100px)"});
        sizeCombo.setSelectedIndex(settings.squareSize == 60 ? 0 : settings.squareSize == 100 ? 2 : 1);
        panel.add(sizeCombo);

        add(panel, BorderLayout.CENTER);

        // Apply / Cancel buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            applySettings();
            onApply.run();
            dispose();
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        btns.add(cancel);
        btns.add(apply);
        add(btns, BorderLayout.SOUTH);
    }

    /**
     * Reads the current dialog values and writes them into the settings object.
     */
    private void applySettings() {
        switch (sizeCombo.getSelectedIndex()) {
            case 0: settings.squareSize = 60; settings.pieceFontSize = 36f; break;
            case 2: settings.squareSize = 100; settings.pieceFontSize = 60f; break;
            default: settings.squareSize = 80; settings.pieceFontSize = 48f; break;
        }
    }
}
