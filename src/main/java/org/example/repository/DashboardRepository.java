package org.example.repository;

import org.example.dto.DashboardDTO;
import java.math.BigDecimal;

public class DashboardRepository extends BaseRepository {

    public DashboardDTO loadDashboard() {

        DashboardDTO dto = new DashboardDTO();

        // ===== USERS =====
        dto.setTotalUsers(
                queryForObject(
                        "SELECT COUNT(*) FROM user_profiles",
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setTotalEmployees(
                queryForObject(
                        "SELECT COUNT(*) FROM accounts WHERE role = 'EMPLOYEE'",
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setTotalCustomers(
                queryForObject(
                        "SELECT COUNT(*) FROM accounts WHERE role = 'CUSTOMER'",
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        // ===== PRODUCTS / INVENTORY =====
        dto.setTotalProducts(
                queryForObject(
                        "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'",
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setLowStockItems(
                queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM inventory_items
                        WHERE quantity_on_hand < min_threshold
                        """,
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setExpiredBatches(
                queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM batches
                        WHERE expiry_date < CURDATE()
                        """,
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        // ===== ORDERS =====
        dto.setTodayOrders(
                queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM orders
                        WHERE DATE(order_date) = CURDATE()
                        """,
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setPendingOrders(
                queryForObject(
                        "SELECT COUNT(*) FROM orders WHERE status = 'NEW'",
                        rs -> rs.getInt(1)
                ).orElse(0)
        );

        dto.setTodayRevenue(
                queryForObject(
                        """
                        SELECT COALESCE(SUM(total_amount), 0)
                        FROM orders
                        WHERE DATE(order_date) = CURDATE()
                        """,
                        rs -> rs.getBigDecimal(1)
                ).orElse(BigDecimal.ZERO)
        );

        return dto;
    }
}
