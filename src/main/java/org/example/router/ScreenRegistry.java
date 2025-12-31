package org.example.router;
import org.example.view.*;
import org.example.view.WarehouseManagementPanel;
import org.example.view.ReportSummaryManagementPanel;
import org.example.view.InvoiceManagementPanel;

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

        factory.register("screen_report_summary", ReportSummaryManagementPanel::new);

        factory.register("screen_report_detail", ReportDetailManagementPanel::new);
        factory.register("screen_my_invoices", InvoiceManagementPanel::new);


        // TODO sau này:
        // factory.register("screen_employees", EmployeeManagementPanel::new);
         factory.register("screen_customer", CustomerManagementPanel::new);
    }
}
