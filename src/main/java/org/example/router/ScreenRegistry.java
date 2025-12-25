package org.example.router;

import org.example.view.AccountManagementPanel;
import org.example.view.EmployeeManagementPanel;
import org.example.view.ManufacturerManagementPanel;

public class ScreenRegistry {

    public static void registerAll(ScreenFactory factory) {
        factory.register("screen_accounts", AccountManagementPanel::new);
        factory.register("screen_employee", EmployeeManagementPanel::new);
        factory.register("screen_manufacturers", ManufacturerManagementPanel::new);

        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
        // factory.register("screen_customers", CustomerManagementPanel::new);
    }
}
