package org.example.router;

import org.example.view.*;

public class ScreenRegistry {

    public static void registerAll(ScreenFactory factory) {
        factory.register("screen_dashboard", Dashboard::new);

        factory.register("screen_accounts", AccountManagementPanel::new);
        factory.register("screen_employee", EmployeeManagementPanel::new);
        factory.register("screen_categories", CategoryManagementPanel::new);
        factory.register("screen_manufacturers", ManufacturerManagementPanel::new);
        factory.register("screen_products", ProductManagementPanel::new);

        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
        // factory.register("screen_customers", CustomerManagementPanel::new);
    }
}
