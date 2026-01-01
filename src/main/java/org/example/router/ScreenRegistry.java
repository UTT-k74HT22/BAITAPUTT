package org.example.router;

import org.example.controller.ReportSummaryController;
import org.example.repository.ReportSummaryRepository;
import org.example.repository.impl.ReportSummaryRepositoryImpl;
import org.example.service.ReportSummaryService;
import org.example.service.impl.ReportSummaryServiceImpl;
import org.example.view.*;
import org.example.view.WarehouseManagementPanel;
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
        factory.register("screen_inventory", InventoryManagementPanel::new);
        factory.register("screen_stock_movements", StockMovementManagementPanel::new);

        factory.register("screen_report_summary", () -> {
            ReportSummaryRepository repository = new ReportSummaryRepositoryImpl();
            ReportSummaryService service = new ReportSummaryServiceImpl(repository);
            ReportSummaryController controller = new ReportSummaryController(service);
            return new ReportSummaryManagementPanel(controller);
        });

        factory.register("screen_report_detail", ReportDetailManagementPanel::new);


        // TODO sau này:
         factory.register("screen_customer", CustomerManagementPanel::new);
    }
}
