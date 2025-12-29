package org.example.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickableStatCard extends JPanel {

    private final Runnable onClick;

    private final Color bgNormal = Color.WHITE;
    private final Color bgHover = new Color(245, 247, 250);

    public ClickableStatCard(
            String title,
            JLabel valueLabel,
            Color accent,
            Runnable onClick
    ) {
        this.onClick = onClick;

        setLayout(new BorderLayout());
        setBackground(bgNormal);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(3, 0));
        accentBar.setBackground(accent);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(120, 120, 120));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(new Color(45, 45, 45));

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        center.add(titleLbl, BorderLayout.NORTH);
        center.add(valueLabel, BorderLayout.CENTER);

        add(accentBar, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installMouseEffect();
    }

    private void installMouseEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(bgHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(bgNormal);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
    }
}
