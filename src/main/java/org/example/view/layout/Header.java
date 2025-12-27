package org.example.view.layout;

import org.example.entity.enums.Role;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {

    private final JLabel moduleLabel;
    private final JLabel userInfoLabel;

    public Header(String username, Role role) {
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

        // ===== user info (phải) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));

        userInfoLabel = new JLabel(username + " (" + role + ")");
        userInfoLabel.setForeground(new Color(120, 122, 150));
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Wrapper để căn phải và căn giữa dọc
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(userInfoLabel, BorderLayout.EAST);

        rightPanel.add(wrapper, BorderLayout.CENTER);

        add(moduleLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    // ===== Style button =====
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(113, 99, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); // 🎯 Bo góc 12px

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);    // quan trọng để vẽ custom
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }


    private JButton createGhostButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền trắng
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Viền tím
                g2.setColor(new Color(113, 99, 248));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(new Color(113, 99, 248));
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);  // bắt buộc để dùng custom paint
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16)); // padding
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
}