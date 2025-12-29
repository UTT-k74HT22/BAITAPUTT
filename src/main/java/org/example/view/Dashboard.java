package org.example.view;

import org.example.controller.DashboardController;
import org.example.dto.DashboardDTO;
import org.example.router.ScreenNavigator;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import org.example.view.component.ClickableStatCard;
import static org.example.utils.SideBarConstraint.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Dashboard – ERP Style (Final)
 */
public class Dashboard extends JPanel {

    private final DashboardController controller;

    // ===== Value labels =====
    private final JLabel lblTotalUsers       = value();
    private final JLabel lblTotalEmployees   = value();
    private final JLabel lblTotalCustomers   = value();

    private final JLabel lblTotalProducts    = value();
    private final JLabel lblLowStockItems    = value();
    private final JLabel lblExpiredBatches   = value();

    private final JLabel lblTodayOrders      = value();
    private final JLabel lblPendingOrders    = value();
    private final JLabel lblTodayRevenue     = value();

    private JButton refreshBtn;
    private final JLabel statusLabel = new JLabel(" ");

    private final NumberFormat currencyFmt =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public Dashboard() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        controller = new DashboardController(this);
    }

    // ======================================================
    // HEADER
    // ======================================================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UiTheme.TEXT);

        JLabel subtitle = new JLabel("Tổng quan hệ thống & hoạt động hôm nay");
        subtitle.setFont(UiTheme.FONT_BASE);
        subtitle.setForeground(new Color(140, 140, 140));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshBtn.setBackground(UiTheme.PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        refreshBtn.addActionListener(e -> controller.loadData());

        statusLabel.setFont(UiTheme.FONT_BASE);
        statusLabel.setForeground(new Color(150, 150, 150));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(statusLabel);
        right.add(refreshBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ======================================================
    // CONTENT
    // ======================================================
    private JPanel buildContent() {
        JPanel grid = new JPanel(new GridLayout(3, 3, 18, 18));
        grid.setOpaque(false);

        // USERS
        grid.add(card("Người dùng", lblTotalUsers, UiTheme.INFO,
                () -> ScreenNavigator.showByMenu(MENU_ACCOUNTS)));

        grid.add(card("Nhân viên", lblTotalEmployees, UiTheme.SUCCESS,
                () -> ScreenNavigator.showByMenu(MENU_EMPLOYEE)));

        grid.add(card("Khách hàng", lblTotalCustomers, UiTheme.WARNING,
                () -> ScreenNavigator.showByMenu(MENU_CUSTOMER)));

        // PRODUCTS
        grid.add(card("Sản phẩm", lblTotalProducts, UiTheme.PURPLE,
                () -> ScreenNavigator.showByMenu(MENU_PRODUCTS)));

        grid.add(card("Tồn kho thấp", lblLowStockItems, UiTheme.ORANGE,
                () -> ScreenNavigator.showByMenu(MENU_INVENTORY)));

        grid.add(card("Lô hết hạn", lblExpiredBatches, UiTheme.DANGER,
                () -> ScreenNavigator.showByMenu(MENU_BATCHES)));

        // ORDERS
        grid.add(card("Đơn hôm nay", lblTodayOrders, UiTheme.DARK,
                () -> ScreenNavigator.showByMenu(MENU_ORDERS)));

        grid.add(card("Đang xử lý", lblPendingOrders, UiTheme.WARNING,
                () -> ScreenNavigator.showByMenu(MENU_ORDERS)));

        grid.add(card("Doanh thu hôm nay", lblTodayRevenue, UiTheme.SUCCESS,
                () -> ScreenNavigator.showByMenu(MENU_REPORT_SUMMARY)));

        JPanel wrapper = UiFactory.cardPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(grid, BorderLayout.CENTER);

        return wrapper;
    }

    private ClickableStatCard card(String title, JLabel value, Color accent, Runnable action) {
        return new ClickableStatCard(title, value, accent, action);
    }

    private static JLabel value() {
        JLabel l = new JLabel("0");
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(120, 120, 120));
        return l;
    }

    // ======================================================
    // RENDER
    // ======================================================
    public void render(DashboardDTO dto) {
        if (dto == null) return;

        lblTotalUsers.setText(String.valueOf(dto.getTotalUsers()));
        lblTotalEmployees.setText(String.valueOf(dto.getTotalEmployees()));
        lblTotalCustomers.setText(String.valueOf(dto.getTotalCustomers()));

        lblTotalProducts.setText(String.valueOf(dto.getTotalProducts()));

        lblLowStockItems.setText(String.valueOf(dto.getLowStockItems()));
        lblLowStockItems.setForeground(
                dto.getLowStockItems() > 0 ? UiTheme.ORANGE : UiTheme.SUCCESS
        );

        lblExpiredBatches.setText(String.valueOf(dto.getExpiredBatches()));
        lblExpiredBatches.setForeground(
                dto.getExpiredBatches() > 0 ? UiTheme.DANGER : UiTheme.SUCCESS
        );

        lblTodayOrders.setText(String.valueOf(dto.getTodayOrders()));

        lblPendingOrders.setText(String.valueOf(dto.getPendingOrders()));
        lblPendingOrders.setForeground(
                dto.getPendingOrders() > 0 ? UiTheme.WARNING : UiTheme.SUCCESS
        );

        BigDecimal rev = dto.getTodayRevenue() != null
                ? dto.getTodayRevenue()
                : BigDecimal.ZERO;
        lblTodayRevenue.setText(currencyFmt.format(rev));
        lblTodayRevenue.setForeground(UiTheme.SUCCESS.darker());

    }

    // ======================================================
    // LOADING
    // ======================================================
    public void setLoading(boolean loading) {
        SwingUtilities.invokeLater(() -> {
            refreshBtn.setEnabled(!loading);
            statusLabel.setText(loading ? "Đang tải dữ liệu..." : " ");
        });
    }
}
