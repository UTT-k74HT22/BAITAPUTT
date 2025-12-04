package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SideBar extends JPanel {

    private Map<String, JButton> menuButtons = new LinkedHashMap<>();

    public SideBar(String role) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(40, 45, 55));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        addMenu("Dashboard", gbc);
        addMenu("Quản lý sinh viên", gbc);

        if ("ADMIN".equals(role)) {
            addMenu("Quản lý tài khoản", gbc);
            addMenu("Cài đặt hệ thống", gbc);
        }

        gbc.weighty = 1;
        add(Box.createVerticalGlue(), gbc);

        addMenu("Đăng xuất", gbc);
    }

    private void addMenu(String text, GridBagConstraints gbc) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 45, 55));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(60, 65, 75));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 45, 55));
            }
        });

        menuButtons.put(text, btn);

        add(btn, gbc);
    }

    public JButton getMenu(String name) {
        return menuButtons.get(name);
    }
}
