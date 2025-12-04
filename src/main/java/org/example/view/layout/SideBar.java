package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class SideBar extends JPanel {

    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
    private String activeKey = null;

    public SideBar(String role) {
        setOpaque(false);
        setPreferredSize(new Dimension(240, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 18, 18, 18));

        // ===== Logo / tên hệ thống =====
        JLabel appLabel = new JLabel("NL  Bệnh Viện");
        appLabel.setForeground(Color.WHITE);
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(appLabel);
        add(Box.createVerticalStrut(20));

        // ===== Menu chính =====
        addMenu("Lễ Tân & Tiếp Đón");
        addMenu("Khám Bệnh & Xét Nghiệm");
        addMenu("Nhập Viện & Xếp Giường");
        addMenu("Điều Trị & Phát Thuốc");
        addMenu("Kê Hóa Đơn & Xuất Viện");

        // ===== Nhóm Thống kê =====
        add(Box.createVerticalStrut(14));
        JLabel statsLabel = new JLabel("Thống kê");
        statsLabel.setForeground(new Color(215, 215, 245));
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statsLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(statsLabel);
        add(Box.createVerticalStrut(6));

        addMenu("Báo Cáo Tóm Tắt");
        addMenu("Thống Kê Chi Tiết");
        addMenu("Bác Sĩ");

        // Đẩy logout xuống dưới
        add(Box.createVerticalGlue());

        JButton logoutBtn = createMenuButton("Đăng xuất");
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        menuButtons.put("Đăng xuất", logoutBtn);
        add(logoutBtn);

        // Mặc định active là Lễ Tân & Tiếp Đón
        setActiveMenu("Lễ Tân & Tiếp Đón");
    }

    private void addMenu(String text) {
        JButton btn = createMenuButton(text);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        menuButtons.put(text, btn);
        add(btn);
        add(Box.createVerticalStrut(6));
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(190, 40));

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        // Hover + click -> active
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(activeKey)) {
                    btn.setOpaque(true);
                    btn.setBackground(new Color(255, 255, 255, 40));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!text.equals(activeKey)) {
                    btn.setOpaque(false);
                    btn.setBackground(new Color(0, 0, 0, 0));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveMenu(text);
            }
        });

        return btn;
    }

    /** Đổi trạng thái menu đang chọn (gọi từ MainFrame sau này) */
    public void setActiveMenu(String key) {
        activeKey = key;
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(key)) {
                btn.setOpaque(true);
                btn.setBackground(new Color(255, 255, 255, 80));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                btn.setOpaque(false);
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }
        repaint();
    }

    public JButton getMenu(String name) {
        return menuButtons.get(name);
    }

    // Gradient tím → xanh cho sidebar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Color c1 = new Color(142, 105, 255); // tím
        Color c2 = new Color(83, 163, 255);  // xanh
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
