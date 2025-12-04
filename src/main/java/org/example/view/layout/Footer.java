package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Footer extends JPanel {

    public Footer() {
        setPreferredSize(new Dimension(0, 30));
        setBackground(new Color(230, 230, 230));

        JLabel label = new JLabel("© 2025 - Student Manager");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        add(label);
    }
}

