package org.example.view.customer;

import org.example.controller.OrderController;
import org.example.dto.response.Order.OrderResponse;
import org.example.router.AuthContextManager;
import org.example.view.base.BaseManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class CustomerOrderPanel extends BaseManagementPanel<OrderResponse> {

    private final OrderController controller = new OrderController();
    private final Long customerId = AuthContextManager.getUserId();
    private List<OrderResponse> data;

    @Override
    protected String titleTotal() {
        return "Tổng số đơn hàng";
    }

    @Override
    protected String[] columns() {
        return new String[]{"Mã đơn", "Ngày tạo", "Trạng thái", "Tổng tiền"};
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{140, 120, 120, 120};
    }

    @Override
    protected List<OrderResponse> fetchData() {
        System.out.println("\n===== DEBUG ORDER API =====");
        System.out.println("Customer ID đang login = " + customerId);

        List<OrderResponse> all = controller.getAll();
        System.out.println("Tổng số order API trả về = " + all.size());

        if (all.isEmpty()) {
            System.out.println("⚠ API không có dữ liệu order!");
        }

        System.out.println("\n-- Chi tiết từng Order từ API --");
        for (OrderResponse o : all) {
            System.out.println("OrderNumber = " + o.getOrderNumber()
                    + " | orderDate = " + o.getOrderDate()
                    + " | status = " + o.getStatus()
                    + " | total = " + o.getTotalAmount()
                    + " | customerId = " + o.getCustomerId());
        }

        System.out.println("\n-- Sau khi filter theo customer đang login --");
        List<OrderResponse> filtered = all.stream()
                .filter(o -> Objects.equals(o.getCustomerId(), customerId))
                .toList();

        System.out.println("Số đơn khớp với customer login = " + filtered.size());
        for (OrderResponse o : filtered) {
            System.out.println("✔ MATCH → " + o.getOrderNumber());
        }

        System.out.println("===========================\n");

        return filtered;
    }

    @Override
    protected Object[] mapRow(OrderResponse o, int stt) {
        return new Object[]{
                o.getOrderNumber(),
                o.getOrderDate(),
                o.getStatus(),
                o.getTotalAmount()


        };
    }

    @Override
    protected void afterTableCreated() {
        System.out.println("Table đã được tạo, số row hiện tại = " + model.getRowCount());
    }

    @Override
    protected void applyFilters() {
        System.out.println("applyFilters() được gọi — sorter đang active = " + (sorter != null));
    }

    public CustomerOrderPanel() {
        super();

        System.out.println("\n===== DEBUG PANEL CONSTRUCTOR =====");
        System.out.println("Panel khởi tạo với customerId = " + customerId);

        data = fetchData();

        if (data.isEmpty()) {
            System.out.println("⚠ Không có đơn sau filter → hiển thị placeholder");
            showPlaceholder("Không có đơn hàng nào để hiển thị!");
        } else {
            System.out.println("✔ Có dữ liệu → reload table");
            reload();
        }

        System.out.println("Số row trong model sau reload = " + model.getRowCount());
        System.out.println("=================================\n");
    }

    private void showPlaceholder(String msg) {
        System.out.println("Placeholder message = " + msg);

        removeAll();
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        add(lbl, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /** Chạy độc lập để debug UI nếu cần */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Customer Order Debug");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);
        frame.setLocationRelativeTo(null);
        frame.add(new CustomerOrderPanel());
        frame.setVisible(true);
    }
}
