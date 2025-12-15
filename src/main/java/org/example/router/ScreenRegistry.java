package org.example.router;

import org.example.view.AccountManagementPanel;
import org.example.view.EmployeeManagementPanel;

public class ScreenRegistry {

    public static void registerAll(ScreenFactory factory) {
        // ✅ màn thật bạn đã làm
        factory.register("screen_accounts", AccountManagementPanel::new);

        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
        // factory.register("screen_customers", CustomerManagementPanel::new);
    }
}
