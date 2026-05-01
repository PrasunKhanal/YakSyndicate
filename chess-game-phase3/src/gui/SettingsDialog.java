package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for customizing board colors and square size.
 */
public class SettingsDialog extends JDialog {
    private final BoardSettings settings;
    private final Runnable onApply;
    private JButton lightBtn, darkBtn;
    private JComboBox<String> sizeCombo;

    /**
     * @param owner    parent frame
     * @param settings shared settings object
     * @param onApply  called after Apply is clicked
     */
    public SettingsDialog(Frame owner, BoardSettings settings, Runnable onApply) {
        super(owner, "Settings", true);
        this.settings = settings; this.onApply = onApply;
        buildUI(); pack(); setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        p.add(new JLabel("Light Square:")); lightBtn = new JButton("  ");
        lightBtn.setBackground(settings.lightSquare);
        lightBtn.addActionListener(e -> { Color c = JColorChooser.showDialog(this, "Light Square", settings.lightSquare); if (c != null) { settings.lightSquare = c; lightBtn.setBackground(c); } });
        p.add(lightBtn);

        p.add(new JLabel("Dark Square:")); darkBtn = new JButton("  ");
        darkBtn.setBackground(settings.darkSquare);
        darkBtn.addActionListener(e -> { Color c = JColorChooser.showDialog(this, "Dark Square", settings.darkSquare); if (c != null) { settings.darkSquare = c; darkBtn.setBackground(c); } });
        p.add(darkBtn);

        p.add(new JLabel("Board Size:"));
        sizeCombo = new JComboBox<>(new String[]{"Small (60px)", "Medium (80px)", "Large (100px)"});
        sizeCombo.setSelectedIndex(settings.squareSize == 60 ? 0 : settings.squareSize == 100 ? 2 : 1);
        p.add(sizeCombo);

        add(p, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            switch (sizeCombo.getSelectedIndex()) {
                case 0: settings.squareSize = 60; settings.pieceFontSize = 36f; break;
                case 2: settings.squareSize = 100; settings.pieceFontSize = 60f; break;
                default: settings.squareSize = 80; settings.pieceFontSize = 48f;
            }
            onApply.run(); dispose();
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        btns.add(cancel); btns.add(apply);
        add(btns, BorderLayout.SOUTH);
    }
}
