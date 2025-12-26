package org.example.router;

import org.example.view.customer.ProductCatalogPanel;

import javax.swing.*;

public class CustomerScreenRegistry {

    public static void registerAll(ScreenFactory factory) {
        factory.register("screen_shop_products", ProductCatalogPanel::new);
        factory.register("screen_my_orders", () -> new JPanel()); // Placeholder for now
        factory.register("screen_my_invoices", () -> new JPanel()); // Placeholder for now
        factory.register("screen_my_payments", () -> new JPanel()); // Placeholder for now
    }
}
