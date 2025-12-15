package org.example.router;

import java.util.LinkedHashMap;
import java.util.Map;
import static org.example.utils.SideBarConstraint.*;

/**
 * Định nghĩa các tuyến đường (routes) trong ứng dụng.
 */
public class AppRoutes {

    public record ScreenSpec(String cardKey, String title) {}

    public static Map<String, ScreenSpec> build() {
        Map<String, ScreenSpec> routes = new LinkedHashMap<>();

        routes.put(MENU_DASHBOARD, new ScreenSpec("screen_dashboard", "Dashboard"));

        routes.put(MENU_ACCOUNTS, new ScreenSpec("screen_accounts", "Tài khoản"));
        routes.put(MENU_EMPLOYEES, new ScreenSpec("screen_employees", "Nhân viên"));
        routes.put(MENU_CUSTOMERS, new ScreenSpec("screen_customers", "Khách hàng"));

        routes.put(MENU_CATEGORIES, new ScreenSpec("screen_categories", "Danh mục sản phẩm"));
        routes.put(MENU_MANUFACTURERS, new ScreenSpec("screen_manufacturers", "Nhà sản xuất"));
        routes.put(MENU_PRODUCTS, new ScreenSpec("screen_products", "Sản phẩm"));

        routes.put(MENU_WAREHOUSES, new ScreenSpec("screen_warehouses", "Kho hàng"));
        routes.put(MENU_BATCHES, new ScreenSpec("screen_batches", "Lô hàng"));
        routes.put(MENU_INVENTORY, new ScreenSpec("screen_inventory", "Tồn kho"));
        routes.put(MENU_STOCK_MOVEMENTS, new ScreenSpec("screen_stock_movements", "Xuất/Nhập kho"));

        routes.put(MENU_ORDERS, new ScreenSpec("screen_orders", "Đơn hàng"));
        routes.put(MENU_INVOICES, new ScreenSpec("screen_invoices", "Hóa đơn"));
        routes.put(MENU_PAYMENTS, new ScreenSpec("screen_payments", "Thanh toán"));

        routes.put(MENU_REPORT_SUMMARY, new ScreenSpec("screen_report_summary", "Báo cáo tóm tắt"));
        routes.put(MENU_REPORT_DETAIL, new ScreenSpec("screen_report_detail", "Thống kê chi tiết"));

        routes.put(MENU_SHOP_PRODUCTS, new ScreenSpec("screen_shop_products", "Sản phẩm"));
        routes.put(MENU_MY_ORDERS, new ScreenSpec("screen_my_orders", "Đơn hàng của tôi"));
        routes.put(MENU_MY_INVOICES, new ScreenSpec("screen_my_invoices", "Hóa đơn của tôi"));
        routes.put(MENU_MY_PAYMENTS, new ScreenSpec("screen_my_payments", "Thanh toán của tôi"));

        routes.put(MENU_LOGOUT, new ScreenSpec(null, "Đăng xuất"));
        return routes;
    }
}
