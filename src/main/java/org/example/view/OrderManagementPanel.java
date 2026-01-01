package org.example.view;

import org.example.controller.OrderController;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.enums.OrderStatus;
import org.example.view.base.BaseManagementPanel;
import org.example.view.dialog.CreateOrderDialog;
import org.example.view.dialog.OrderDetailDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class OrderManagementPanel extends BaseManagementPanel<OrderResponse> {

    private final OrderController controller = new OrderController();
    private final Long userId; // đổi tên cho đúng nghĩa
    private List<OrderResponse> allOrders;

    public OrderManagementPanel(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        this.userId = userId;
        reload();
    }

    /* ================= BASE CONTRACT ================= */

    @Override
    protected String titleTotal() {
        return "Tổng số đơn hàng";
    }

    @Override
    protected String[] columns() {
        return new String[]{
                "STT", "ID", "Mã đơn", "Ngày tạo", "Trạng thái", "Tổng tiền"
        };
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{60, 150, 180, 140, 160};
    }

    @Override
    protected List<OrderResponse> fetchData() {
        allOrders = controller.getAll();
        return allOrders;
    }

    @Override
    protected Object[] mapRow(OrderResponse o, int stt) {
        return new Object[]{
                stt, o.getId(), o.getOrderNumber(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()
        };
    }

    /* ================= FILTER ================= */

    @Override
    protected JPanel buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);

        JButton btnCreate = new JButton("Tạo đơn");
        JButton btnView = new JButton("Xem chi tiết");
        JButton btnConfirm = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy đơn");

        JTextField txtSearch = new JTextField(18);
        JButton btnSearch = new JButton("Tìm kiếm");

        btnCreate.addActionListener(e -> onCreate());
        btnView.addActionListener(e -> onViewDetail());
        btnConfirm.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> onCancel());
        btnSearch.addActionListener(e -> applySearch(txtSearch.getText()));
        txtSearch.addActionListener(e -> applySearch(txtSearch.getText())); // ENTER để tìm

        p.add(btnCreate);
        p.add(btnView);
        p.add(btnConfirm);
        p.add(btnCancel);
        p.add(txtSearch);
        p.add(btnSearch);

        return p;
    }

    private void applySearch(String keyword) {
        if (sorter == null) return;
        keyword = keyword.trim();
        sorter.setRowFilter(keyword.isBlank() ? null : RowFilter.regexFilter("(?i)" + keyword, 2));
    }

    /* ================= TABLE ================= */

    @Override
    protected void afterTableCreated() {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
    }

    /* ================= ACTION HANDLERS ================= */

    private OrderResponse getSelectedOrder() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        Long id = (Long) table.getModel().getValueAt(modelRow, 1);
        return allOrders.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
    }

    private void onCreate() {
        Window win = SwingUtilities.getWindowAncestor(this);
        CreateOrderDialog dlg = new CreateOrderDialog(win,userId);
        dlg.setVisible(true);
        if (dlg.isSubmitted()) reload();
    }

    private void onViewDetail() {
        OrderResponse order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Chọn đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window win = SwingUtilities.getWindowAncestor(this);
        new OrderDetailDialog(win, order.getId()).setVisible(true);
    }

    private void onConfirm() {
        OrderResponse order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Chọn đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!OrderStatus.NEW.name().equals(order.getStatus())) {
            JOptionPane.showMessageDialog(this, "Chỉ xác nhận đơn NEW!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xác nhận trừ kho?", "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            controller.confirm(order.getId());
            reload();
        }
    }

    private void onCancel() {
        OrderResponse order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Chọn đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!OrderStatus.NEW.name().equals(order.getStatus())) {
            JOptionPane.showMessageDialog(this, "Chỉ hủy đơn NEW!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.cancel(order.getId());
        reload();
    }
}
