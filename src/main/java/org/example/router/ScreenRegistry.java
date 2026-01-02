package org.example.router;

import org.example.view.*;
import org.example.view.WarehouseManagementPanel;
import org.example.view.customer.CustomerOrderPanel;
import org.example.view.customer.ProductCatalogPanel;
import org.example.view.ReportSummaryManagementPanel;

public class ScreenRegistry {

    public static void registerAll(ScreenFactory factory) {
        factory.register("screen_dashboard", Dashboard::new);

        factory.register("screen_accounts", AccountManagementPanel::new);
        factory.register("screen_employee", EmployeeManagementPanel::new);
        factory.register("screen_categories", CategoryManagementPanel::new);
        factory.register("screen_manufacturers", ManufacturerManagementPanel::new);
        factory.register("screen_products", ProductManagementPanel::new);
        factory.register("screen_warehouses", WarehouseManagementPanel::new);
        factory.register("screen_batches", BatchManagementPanel::new);
        // cả admin và employee đều có quyền truy cập
        factory.register("screen_orders", () -> new OrderManagementPanel(AuthContextManager.getUserId()));
        factory.register("screen_inventory", InventoryManagementPanel::new);
        factory.register("screen_stock_movements", StockMovementManagementPanel::new);

        factory.register("screen_report_summary",ReportSummaryManagementPanel::new);

        factory.register("screen_report_detail", ReportDetailManagementPanel::new);
        factory.register("screen_invoices", InvoiceManagementPanel::new);

        factory.register("screen_payments", PaymentManagementPanel::new);

        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
        factory.register("screen_shop_products", ProductCatalogPanel::new);
        factory.register("screen_customer_order", CustomerOrderPanel::new);
        factory.register("screen_customer", CustomerManagementPanel::new);
         factory.register("screen_customer", CustomerManagementPanel::new);
    }
}
