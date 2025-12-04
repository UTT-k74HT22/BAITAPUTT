package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Footer extends JPanel {

    public Footer() {
        setPreferredSize(new Dimension(0, 32));
        setBackground(new Color(248, 249, 255));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(230, 232, 245))
        );

        JLabel label = new JLabel("© 2025 - Hospital Management System");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(150, 150, 180));

        add(label, BorderLayout.WEST);
    }
}
