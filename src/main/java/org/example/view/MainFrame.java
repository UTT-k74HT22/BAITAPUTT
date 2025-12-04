package org.example.view;

import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.SideBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Header header;
    private final SideBar sideBar;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private String currentModuleTitle = "Lễ Tân & Tiếp Đón";

    // Card keys
    private static final String SCREEN_RECEPTION = "reception";
    private static final String SCREEN_EXAM = "exam";
    private static final String SCREEN_ADMISSION = "admission";
    private static final String SCREEN_TREATMENT = "treatment";
    private static final String SCREEN_BILLING = "billing";
    private static final String SCREEN_SUMMARY = "summary";
    private static final String SCREEN_DETAIL = "detail";
    private static final String SCREEN_DOCTOR = "doctor";

    public MainFrame(String username, String role) {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header & Sidebar
        header = new Header(username, role);
        sideBar = new SideBar(role);

        // Background content
        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        initScreens();
        bgContent.add(contentPanel, BorderLayout.CENTER);

        Footer footer = new Footer();

        add(sideBar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(bgContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        initMenuActions();
        initHeaderActions();
    }

    // ===== Tạo các màn hình mock =====
    private void initScreens() {
        contentPanel.add(createModuleScreen("Khu vực Lễ Tân & Tiếp Đón"), SCREEN_RECEPTION);
        contentPanel.add(createModuleScreen("Khu vực Khám Bệnh & Xét Nghiệm"), SCREEN_EXAM);
        contentPanel.add(createModuleScreen("Khu vực Nhập Viện & Xếp Giường"), SCREEN_ADMISSION);
        contentPanel.add(createModuleScreen("Khu vực Điều Trị & Phát Thuốc"), SCREEN_TREATMENT);
        contentPanel.add(createModuleScreen("Khu vực Kê Hóa Đơn & Xuất Viện"), SCREEN_BILLING);
        contentPanel.add(createModuleScreen("Báo Cáo Tóm Tắt"), SCREEN_SUMMARY);
        contentPanel.add(createModuleScreen("Thống Kê Chi Tiết"), SCREEN_DETAIL);
        contentPanel.add(createModuleScreen("Quản lý Bác Sĩ"), SCREEN_DOCTOR);

        // Default
        showScreen(SCREEN_RECEPTION, "Lễ Tân & Tiếp Đón");
    }

    private JPanel createModuleScreen(String title) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 22, 22));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(120, 120, 160));

        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    // ===== Gán action cho sidebar =====
    private void initMenuActions() {

        JButton btn;

        btn = sideBar.getMenu("Lễ Tân & Tiếp Đón");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_RECEPTION, "Lễ Tân & Tiếp Đón"));
        }

        btn = sideBar.getMenu("Khám Bệnh & Xét Nghiệm");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_EXAM, "Khám Bệnh & Xét Nghiệm"));
        }

        btn = sideBar.getMenu("Nhập Viện & Xếp Giường");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_ADMISSION, "Nhập Viện & Xếp Giường"));
        }

        btn = sideBar.getMenu("Điều Trị & Phát Thuốc");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_TREATMENT, "Điều Trị & Phát Thuốc"));
        }

        btn = sideBar.getMenu("Kê Hóa Đơn & Xuất Viện");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_BILLING, "Kê Hóa Đơn & Xuất Viện"));
        }

        btn = sideBar.getMenu("Báo Cáo Tóm Tắt");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_SUMMARY, "Báo Cáo Tóm Tắt"));
        }

        btn = sideBar.getMenu("Thống Kê Chi Tiết");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_DETAIL, "Thống Kê Chi Tiết"));
        }

        btn = sideBar.getMenu("Bác Sĩ");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_DOCTOR, "Bác Sĩ"));
        }

        // Logout
        btn = sideBar.getMenu("Đăng xuất");
        if (btn != null) {
            btn.addActionListener(e -> {
                // TODO: mở lại màn Login của bạn
                // new LoginFrame().setVisible(true);
                dispose();
            });
        }
    }

    // ===== Hành vi nút Thêm/Sửa/Export trên header (tạm mock) =====
    private void initHeaderActions() {
        header.getBtnAdd().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thực hiện chức năng [Thêm] tại module: " + currentModuleTitle,
                        "Action", JOptionPane.INFORMATION_MESSAGE));

        header.getBtnEdit().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thực hiện chức năng [Sửa] tại module: " + currentModuleTitle,
                        "Action", JOptionPane.INFORMATION_MESSAGE));

        header.getBtnExport().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thực hiện chức năng [Xuất Excel] tại module: " + currentModuleTitle,
                        "Action", JOptionPane.INFORMATION_MESSAGE));
    }

    // ===== Đổi màn hình + cập nhật header + sidebar =====
    private void showScreen(String cardKey, String moduleTitle) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(moduleTitle);
        cardLayout.show(contentPanel, cardKey);
    }

    // Panel bo tròn có bóng nhẹ dùng cho mọi màn hình
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // bóng nhẹ
            graphics.setColor(new Color(0, 0, 0, 18));
            graphics.fillRoundRect(4, 4, width - 8, height - 8,
                    arcs.width, arcs.height);

            // thân panel
            graphics.setColor(backgroundColor);
            graphics.fillRoundRect(0, 0, width - 8, height - 8,
                    arcs.width, arcs.height);
        }
    }

    // Test nhanh (chạy riêng, không qua login)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame("admin", "ADMIN").setVisible(true)
        );
    }
}
