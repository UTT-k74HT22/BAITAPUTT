package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {

    public Header(String username, String role) {
        setPreferredSize(new Dimension(0, 60));
        setBackground(new Color(30, 40, 60));
        setLayout(new BorderLayout());

        JLabel title = new JLabel(" Student Management System ", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel userInfo = new JLabel(" " + username + " (" + role + ") ", SwingConstants.RIGHT);
        userInfo.setForeground(Color.WHITE);
        userInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(title, BorderLayout.WEST);
        add(userInfo, BorderLayout.EAST);
    }
}
