package org.example.view;

import org.example.entity.enums.Role;
import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.SideBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MainFrame extends JFrame {

    private final Header header;
    private final SideBar sideBar;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private String currentModuleTitle = "Dashboard";

    // ===================== Screen Spec =====================
    private static class ScreenSpec {
        final String cardKey;
        final String title;

        ScreenSpec(String cardKey, String title) {
            this.cardKey = cardKey;
            this.title = title;
        }
    }

    private final Map<String, ScreenSpec> routes = new LinkedHashMap<>();

    public MainFrame(String username, Role role) {
        setTitle("HMS - Minh Châu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header & Sidebar
        header = new Header(username, role);
        sideBar = new SideBar(role);

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setBorder(null);
        sideBarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sideBarScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Background content (giữ nguyên UI)
        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        initRoutes();      // khai báo route: menuKey -> (cardKey,title)
        initScreens();     // add card panels theo routes

        bgContent.add(contentPanel, BorderLayout.CENTER);

        add(new JScrollPane(sideBar), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(bgContent, BorderLayout.CENTER);
        add(new Footer(), BorderLayout.SOUTH);

        initMenuActions(role);
        initHeaderActions();
    }

    // ===================== ROUTES =====================
    private void initRoutes() {
        // Dashboard
        routes.put(SideBar.MENU_DASHBOARD, new ScreenSpec("screen_dashboard", "Dashboard"));

        // Admin-only
        routes.put(SideBar.MENU_ACCOUNTS, new ScreenSpec("screen_accounts", "Tài khoản"));
        routes.put(SideBar.MENU_EMPLOYEES, new ScreenSpec("screen_employees", "Nhân viên"));
        routes.put(SideBar.MENU_CUSTOMERS, new ScreenSpec("screen_customers", "Khách hàng"));

        // Catalog
        routes.put(SideBar.MENU_CATEGORIES, new ScreenSpec("screen_categories", "Danh mục sản phẩm"));
        routes.put(SideBar.MENU_MANUFACTURERS, new ScreenSpec("screen_manufacturers", "Nhà sản xuất"));
        routes.put(SideBar.MENU_PRODUCTS, new ScreenSpec("screen_products", "Sản phẩm"));

        // Inventory
        routes.put(SideBar.MENU_WAREHOUSES, new ScreenSpec("screen_warehouses", "Kho hàng"));
        routes.put(SideBar.MENU_BATCHES, new ScreenSpec("screen_batches", "Lô hàng"));
        routes.put(SideBar.MENU_INVENTORY, new ScreenSpec("screen_inventory", "Tồn kho"));
        routes.put(SideBar.MENU_STOCK_MOVEMENTS, new ScreenSpec("screen_stock_movements", "Xuất/Nhập kho"));

        // Sales/Billing
        routes.put(SideBar.MENU_ORDERS, new ScreenSpec("screen_orders", "Đơn hàng"));
        routes.put(SideBar.MENU_INVOICES, new ScreenSpec("screen_invoices", "Hóa đơn"));
        routes.put(SideBar.MENU_PAYMENTS, new ScreenSpec("screen_payments", "Thanh toán"));

        // Reports
        routes.put(SideBar.MENU_REPORT_SUMMARY, new ScreenSpec("screen_report_summary", "Báo cáo tóm tắt"));
        routes.put(SideBar.MENU_REPORT_DETAIL, new ScreenSpec("screen_report_detail", "Thống kê chi tiết"));

        // Customer
        routes.put(SideBar.MENU_SHOP_PRODUCTS, new ScreenSpec("screen_shop_products", "Sản phẩm"));
        routes.put(SideBar.MENU_MY_ORDERS, new ScreenSpec("screen_my_orders", "Đơn hàng của tôi"));
        routes.put(SideBar.MENU_MY_INVOICES, new ScreenSpec("screen_my_invoices", "Hóa đơn của tôi"));
        routes.put(SideBar.MENU_MY_PAYMENTS, new ScreenSpec("screen_my_payments", "Thanh toán của tôi"));

        // Logout: không phải screen, xử lý riêng
        routes.put(SideBar.MENU_LOGOUT, new ScreenSpec(null, "Đăng xuất"));
    }

    // ===================== SCREENS =====================
    private void initScreens() {
        // Tạo card cho tất cả route có cardKey
        for (ScreenSpec spec : routes.values()) {
            if (spec.cardKey != null) {
                contentPanel.add(createModuleScreen(spec.title), spec.cardKey);
            }
        }

        // Default screen theo role (giữ behavior hợp lý)
        if (header != null) {
            if (isCustomerRole()) {
                showByMenuKey(SideBar.MENU_SHOP_PRODUCTS);
            } else {
                showByMenuKey(SideBar.MENU_DASHBOARD);
            }
        }
    }

    private boolean isCustomerRole() {
        // Header đang giữ role; nếu Header bạn chưa expose role, bạn có thể truyền role vào field riêng.
        // Tạm thời suy luận: nếu sidebar có menu SHOP_PRODUCTS thì coi như customer.
        return sideBar.getMenu(SideBar.MENU_SHOP_PRODUCTS) != null
                && sideBar.getMenu(SideBar.MENU_ACCOUNTS) == null;
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

    // ===================== MENU ACTIONS =====================
    private void initMenuActions(Role role) {
        // Bind tự động theo các nút hiện có trong sidebar
        for (String menuKey : routes.keySet()) {
            JButton btn = sideBar.getMenu(menuKey);
            if (btn == null) continue; // role không có menu này

            if (Objects.equals(menuKey, SideBar.MENU_LOGOUT)) {
                btn.addActionListener(e -> doLogout());
                continue;
            }

            btn.addActionListener(e -> showByMenuKey(menuKey));
        }

        // Default screen đảm bảo đúng role
        if (role == Role.CUSTOMER) {
            showByMenuKey(SideBar.MENU_SHOP_PRODUCTS);
        } else {
            showByMenuKey(SideBar.MENU_DASHBOARD);
        }
    }

    private void doLogout() {
        // TODO: mở lại LoginFrame của bạn nếu có
        // new LoginFrame().setVisible(true);
        dispose();
    }

    // ===================== HEADER ACTIONS =====================
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

    // ===================== ROUTER =====================
    private void showByMenuKey(String menuKey) {
        ScreenSpec spec = routes.get(menuKey);
        if (spec == null || spec.cardKey == null) {
            // fallback
            ScreenSpec fallback = routes.get(SideBar.MENU_DASHBOARD);
            if (fallback != null) {
                showScreen(fallback.cardKey, fallback.title, SideBar.MENU_DASHBOARD);
            }
            return;
        }
        showScreen(spec.cardKey, spec.title, menuKey);
    }

    private void showScreen(String cardKey, String moduleTitle, String activeMenuKey) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(activeMenuKey);
        cardLayout.show(contentPanel, cardKey);
    }

    // ===================== RoundedPanel giữ nguyên UI =====================
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame("john_doe", Role.CUSTOMER);
            mainFrame.setVisible(true);
        });
    }
}
