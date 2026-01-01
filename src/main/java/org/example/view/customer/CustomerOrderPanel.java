package org.example.view.customer;

import org.example.controller.OrderController;
import org.example.dto.response.Order.OrderResponse;
import org.example.router.AuthContextManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class CustomerOrderPanel extends JPanel {

    private final OrderController controller = new OrderController();
    private final Long customerId = AuthContextManager.getUserId();

    private JTable table;
    private DefaultTableModel model;
    private List<OrderResponse> data;

    public CustomerOrderPanel() {
        setLayout(new BorderLayout(10, 10));
        buildUI();
        load();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField txt = new JTextField(14);
        JButton btn = new JButton(" Tìm đơn");

        btn.addActionListener(e -> search(txt.getText()));

        top.add(new JLabel("Mã đơn:"));
        top.add(txt);
        top.add(btn);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã đơn","Ngày tạo","Trạng thái","Tổng tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);

        if (customerId == null) {
            addPlaceholder(" Bạn chưa đăng nhập!");
            return;
        }

        data = controller.getAll().stream()
                .filter(o -> Objects.equals(o.getCustomerId(), customerId))
                .toList();

        if (data.isEmpty()) {
            addPlaceholder(" Bạn chưa có đơn hàng nào.");
            return;
        }

        for (OrderResponse o : data) {
            model.addRow(new Object[]{
                    o.getOrderNumber(),
                    o.getOrderDate(),
                    o.getStatus(),
                    o.getTotalAmount()
            });
        }
    }

    private void addPlaceholder(String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(new Color(150,150,180));
        p.add(lbl, BorderLayout.CENTER);
        add(p, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void search(String key) {
        if (data == null || data.isEmpty()) return;
        key = key.trim().toLowerCase();

        if (key.isEmpty()) {
            load();
            return;
        }
        String searchKey = key.trim().toLowerCase();
        List<OrderResponse> result = data.stream()
                .filter(o -> o.getOrderNumber().toLowerCase().contains(searchKey))
                .toList();

        model.setRowCount(0);
        for (OrderResponse o : result) {
            model.addRow(new Object[]{
                    o.getOrderNumber(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()
            });
        }
    }
}
