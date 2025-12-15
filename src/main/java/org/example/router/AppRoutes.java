package org.example.router;

import org.example.view.layout.SideBar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Định nghĩa các tuyến đường (routes) trong ứng dụng.
 */
public class AppRoutes {

    public record ScreenSpec(String cardKey, String title) {}

    public static Map<String, ScreenSpec> build() {
        Map<String, ScreenSpec> routes = new LinkedHashMap<>();

        routes.put(SideBar.MENU_DASHBOARD, new ScreenSpec("screen_dashboard", "Dashboard"));

        routes.put(SideBar.MENU_ACCOUNTS, new ScreenSpec("screen_accounts", "Tài khoản"));
        routes.put(SideBar.MENU_EMPLOYEES, new ScreenSpec("screen_employees", "Nhân viên"));
        routes.put(SideBar.MENU_CUSTOMERS, new ScreenSpec("screen_customers", "Khách hàng"));

        routes.put(SideBar.MENU_CATEGORIES, new ScreenSpec("screen_categories", "Danh mục sản phẩm"));
        routes.put(SideBar.MENU_MANUFACTURERS, new ScreenSpec("screen_manufacturers", "Nhà sản xuất"));
        routes.put(SideBar.MENU_PRODUCTS, new ScreenSpec("screen_products", "Sản phẩm"));

        routes.put(SideBar.MENU_WAREHOUSES, new ScreenSpec("screen_warehouses", "Kho hàng"));
        routes.put(SideBar.MENU_BATCHES, new ScreenSpec("screen_batches", "Lô hàng"));
        routes.put(SideBar.MENU_INVENTORY, new ScreenSpec("screen_inventory", "Tồn kho"));
        routes.put(SideBar.MENU_STOCK_MOVEMENTS, new ScreenSpec("screen_stock_movements", "Xuất/Nhập kho"));

        routes.put(SideBar.MENU_ORDERS, new ScreenSpec("screen_orders", "Đơn hàng"));
        routes.put(SideBar.MENU_INVOICES, new ScreenSpec("screen_invoices", "Hóa đơn"));
        routes.put(SideBar.MENU_PAYMENTS, new ScreenSpec("screen_payments", "Thanh toán"));

        routes.put(SideBar.MENU_REPORT_SUMMARY, new ScreenSpec("screen_report_summary", "Báo cáo tóm tắt"));
        routes.put(SideBar.MENU_REPORT_DETAIL, new ScreenSpec("screen_report_detail", "Thống kê chi tiết"));

        routes.put(SideBar.MENU_SHOP_PRODUCTS, new ScreenSpec("screen_shop_products", "Sản phẩm"));
        routes.put(SideBar.MENU_MY_ORDERS, new ScreenSpec("screen_my_orders", "Đơn hàng của tôi"));
        routes.put(SideBar.MENU_MY_INVOICES, new ScreenSpec("screen_my_invoices", "Hóa đơn của tôi"));
        routes.put(SideBar.MENU_MY_PAYMENTS, new ScreenSpec("screen_my_payments", "Thanh toán của tôi"));

        routes.put(SideBar.MENU_LOGOUT, new ScreenSpec(null, "Đăng xuất"));
        return routes;
    }
}
