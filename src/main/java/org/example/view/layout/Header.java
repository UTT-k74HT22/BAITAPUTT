package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {

    private final JLabel moduleLabel;
    private final JLabel userInfoLabel;

    private final JButton btnAdd;
    private final JButton btnEdit;
    private final JButton btnExport;

    public Header(String username, String role) {
        setPreferredSize(new Dimension(0, 70));
        setBackground(new Color(248, 249, 255));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(230, 232, 245))
        );

        // ===== Module title (trái) =====
        moduleLabel = new JLabel("Lễ Tân & Tiếp Đón");
        moduleLabel.setForeground(new Color(72, 69, 130));
        moduleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        moduleLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));

        // ===== Cụm nút + user info (phải) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 24));

        // Hàng nút
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setOpaque(false);

        btnAdd = createPrimaryButton("+ Thêm");
        btnEdit = createGhostButton("Sửa");
        btnExport = createPrimaryButton("↑ Xuất Excel");

        actionRow.add(btnAdd);
        actionRow.add(btnEdit);
        actionRow.add(btnExport);

        // Hàng user info
        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        userRow.setOpaque(false);
        userInfoLabel = new JLabel(username + " (" + role + ")");
        userInfoLabel.setForeground(new Color(120, 122, 150));
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRow.add(userInfoLabel);

        rightPanel.add(actionRow);
        rightPanel.add(userRow);

        add(moduleLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    // ===== Style button =====
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(113, 99, 248));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(113, 99, 248));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(113, 99, 248)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===== API để MainFrame dùng =====
    public void setModuleTitle(String title) {
        moduleLabel.setText(title);
    }

    public void setUserInfo(String username, String role) {
        userInfoLabel.setText(username + " (" + role + ")");
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnEdit() {
        return btnEdit;
    }

    public JButton getBtnExport() {
        return btnExport;
    }
}