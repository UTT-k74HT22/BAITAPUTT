package org.example.router;

import org.example.view.*;
import org.example.view.WarehouseManagementPanel;
import org.example.view.customer.CustomerOrderPanel;
import org.example.view.customer.ProductCatalogPanel;

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
        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
        factory.register("screen_shop_products", ProductCatalogPanel::new);
        factory.register("screen_customer_order", CustomerOrderPanel::new);
        factory.register("screen_customer", CustomerManagementPanel::new);
    }
}
