package org.example.view.layout;

import org.example.entity.enums.Role;
import static org.example.utils.SideBarConstraint.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * SideBar hiển thị menu theo Role:
 * - ADMIN: full quyền
 * - EMPLOYEE: nghiệp vụ vận hành
 * - CUSTOMER: xem/mua hàng + theo dõi đơn/hóa đơn của mình
 */
public class SideBar extends JPanel {

    // Key ổn định để route màn hình (khuyến nghị dùng key thay vì text)    public static final String MENU_LOGOUT = "LOGOUT";

    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
    private String activeKey = null;

    public SideBar(Role role) {
        setOpaque(false);
        setPreferredSize(new Dimension(240, 1000));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 18, 18, 18));

        // ===== Logo / tên hệ thống =====
        JLabel appLabel = new JLabel("HMS - Minh Châu");
        appLabel.setForeground(Color.WHITE);
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(appLabel);
        add(Box.createVerticalStrut(20));

        // ===== Render menu theo role =====
        buildMenus(role);

        // Đẩy logout xuống dưới
        add(Box.createVerticalStrut(12));

        JButton logoutBtn = createMenuButton("Đăng xuất", MENU_LOGOUT);
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        menuButtons.put(MENU_LOGOUT, logoutBtn);
        add(logoutBtn);

        // Set menu active mặc định (tùy theo role)
        String defaultMenu = getDefaultMenu(role);
        setActiveMenu(defaultMenu);
    }

    private void buildMenus(Role role) {
        // ADMIN = full quyền
        if (role == Role.ADMIN) {
            addSection("Tổng quan");
            addMenu("Dashboard", MENU_DASHBOARD);

            addSection("Quản trị hệ thống");
            addMenu("Tài khoản", MENU_ACCOUNTS);
            addMenu("Nhân viên", MENU_EMPLOYEES);
            addMenu("Khách hàng", MENU_CUSTOMERS);

            addSection("Danh mục");
            addMenu("Danh mục sản phẩm", MENU_CATEGORIES);
            addMenu("Nhà sản xuất", MENU_MANUFACTURERS);
            addMenu("Sản phẩm", MENU_PRODUCTS);

            addSection("Kho & Tồn");
            addMenu("Kho hàng", MENU_WAREHOUSES);
            addMenu("Lô hàng", MENU_BATCHES);
            addMenu("Tồn kho", MENU_INVENTORY);
            addMenu("Xuất/Nhập kho", MENU_STOCK_MOVEMENTS);

            addSection("Bán hàng & Thanh toán");
            addMenu("Đơn hàng", MENU_ORDERS);
            addMenu("Hóa đơn", MENU_INVOICES);
            addMenu("Thanh toán", MENU_PAYMENTS);

            addSection("Thống kê");
            addMenu("Báo cáo tóm tắt", MENU_REPORT_SUMMARY);
            addMenu("Thống kê chi tiết", MENU_REPORT_DETAIL);
            return;
        }

        // EMPLOYEE = nghiệp vụ vận hành
        if (role == Role.EMPLOYEE) {
            addSection("Tổng quan");
            addMenu("Dashboard", MENU_DASHBOARD);

            addSection("Danh mục");
            addMenu("Danh mục sản phẩm", MENU_CATEGORIES);
            addMenu("Nhà sản xuất", MENU_MANUFACTURERS);
            addMenu("Sản phẩm", MENU_PRODUCTS);

            addSection("Kho & Tồn");
            addMenu("Kho hàng", MENU_WAREHOUSES);
            addMenu("Lô hàng", MENU_BATCHES);
            addMenu("Tồn kho", MENU_INVENTORY);
            addMenu("Xuất/Nhập kho", MENU_STOCK_MOVEMENTS);

            addSection("Bán hàng & Thanh toán");
            addMenu("Đơn hàng", MENU_ORDERS);
            addMenu("Hóa đơn", MENU_INVOICES);
            addMenu("Thanh toán", MENU_PAYMENTS);

            // Nhân viên vẫn cần tra cứu khách hàng để bán hàng/CSKH
            addSection("Khách hàng");
            addMenu("Tra cứu khách hàng", MENU_CUSTOMERS);

            addSection("Thống kê");
            addMenu("Báo cáo tóm tắt", MENU_REPORT_SUMMARY);
            addMenu("Thống kê chi tiết", MENU_REPORT_DETAIL);
            return;
        }

        // CUSTOMER = mua hàng + theo dõi đơn/hóa đơn của mình
        if (role == Role.CUSTOMER) {
            addSection("Mua hàng");
            addMenu("Sản phẩm", MENU_SHOP_PRODUCTS);

            addSection("Đơn của tôi");
            addMenu("Đơn hàng của tôi", MENU_MY_ORDERS);
            addMenu("Hóa đơn của tôi", MENU_MY_INVOICES);
            addMenu("Thanh toán của tôi", MENU_MY_PAYMENTS);

            addSection("Thống kê");
            addMenu("Báo cáo tóm tắt", MENU_REPORT_SUMMARY); // nếu bạn không muốn customer thấy report -> xóa dòng này
            // hoặc bỏ hẳn report cho customer
        }
    }

    private void addSection(String title) {
        add(Box.createVerticalStrut(14));
        JLabel label = new JLabel(title);
        label.setForeground(new Color(215, 215, 245));
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(6));
    }

    private void addMenu(String text, String key) {
        JButton btn = createMenuButton(text, key);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        menuButtons.put(key, btn);
        add(btn);
        add(Box.createVerticalStrut(6));
    }

    private JButton createMenuButton(String text, String key) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Boolean isActive = (Boolean) getClientProperty("isActive");
                Boolean isHovered = (Boolean) getClientProperty("isHovered");

                if (Boolean.TRUE.equals(isActive)) {
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (Boolean.TRUE.equals(isHovered)) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(190, 40));

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.putClientProperty("isActive", false);
        btn.putClientProperty("isHovered", false);
        btn.putClientProperty("menuKey", key); // để MainFrame lấy key nếu cần

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!key.equals(activeKey)) {
                    btn.putClientProperty("isHovered", true);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!key.equals(activeKey)) {
                    btn.putClientProperty("isHovered", false);
                    btn.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveMenu(key);
                // MainFrame có thể addActionListener/getMenu(key) để switch panel
            }
        });

        return btn;
    }

    /** Đổi trạng thái menu đang chọn */
    public void setActiveMenu(String key) {
        activeKey = key;
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton btn = entry.getValue();
            boolean isActive = entry.getKey().equals(key);

            btn.putClientProperty("isActive", isActive);
            btn.putClientProperty("isHovered", false);

            btn.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
            btn.repaint();
        }
    }

    public JButton getMenu(String key) {
        return menuButtons.get(key);
    }

    public String getActiveKey() {
        return activeKey;
    }

    private String getDefaultMenu(Role role) {
        if (role == null) return MENU_DASHBOARD;
        return switch (role) {
            case ADMIN, EMPLOYEE -> MENU_DASHBOARD;
            case CUSTOMER -> MENU_SHOP_PRODUCTS;
        };
    }

    // Gradient tím → xanh cho sidebar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c1 = new Color(142, 105, 255); // tím
        Color c2 = new Color(83, 163, 255);  // xanh
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}