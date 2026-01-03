package org.example.view;

import org.example.controller.InventoryController;
import org.example.controller.OrderController;
import org.example.controller.ProductController;

import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.enums.OrderStatus;
import org.example.service.impl.InventoryServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.dialog.CreateOrderDialog;
import org.example.view.dialog.OrderDetailDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderManagementPanel extends BaseManagementPanel<OrderResponse> {

    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController(new ProductServiceImpl());
    private final InventoryController inventoryController = new InventoryController(new InventoryServiceImpl());
    private final Long userId;

    private List<OrderResponse> allOrders;

    public OrderManagementPanel(Long userId) {
        this.userId = userId;
        reload();
    }

    private String formatMoney(BigDecimal money) {
        return money.stripTrailingZeros().toPlainString();
    }

    @Override
    protected String titleTotal() {
        return "Tổng số đơn hàng";
    }

    @Override
    protected String[] columns() {
        return new String[]{"STT", "ID", "Mã đơn", "Ngày tạo", "Trạng thái", "Tổng tiền"};
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{60, 120, 180, 140, 140, 140}; // ✔ khớp 6 cột
    }

    @Override
    protected List<OrderResponse> fetchData() {
        allOrders = orderController.getAll();
        return allOrders;
    }

    @Override
    protected Object[] mapRow(OrderResponse o, int stt) {
        return new Object[]{stt, o.getId(), o.getOrderNumber(), o.getOrderDate(), o.getStatus(), formatMoney(o.getTotalAmount())};
    }

    @Override
    protected JPanel buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnCreate = new JButton("Tạo đơn");
        JButton btnConfirm = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy đơn");
        JButton btnView = new JButton("Xem chi tiết");

        JTextField txtSearch = new JTextField(18);
        JButton btnSearch = new JButton("Tìm kiếm");

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> onRefresh());
        p.add(btnRefresh);

        btnCreate.addActionListener(e -> onCreate());
        btnConfirm.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> onCancel());
        btnView.addActionListener(e -> onViewDetail());
        btnSearch.addActionListener(e -> applySearch(txtSearch.getText()));



        p.add(btnCreate);
        p.add(btnView);
        p.add(btnConfirm);
        p.add(btnCancel);
        p.add(txtSearch);
        p.add(btnSearch);
        p.add(btnRefresh);

        return p;
    }
    private void applySearch(String keyword) {
        if (sorter == null) return;
        keyword = keyword.trim();
        sorter.setRowFilter(keyword.isBlank() ? null : RowFilter.regexFilter("(?i)" + keyword, 2));
    }
    private void onViewDetail() {
        OrderResponse selected = getSelectedOrder();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn đơn hàng!");
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        new OrderDetailDialog(owner, selected.getId()).setVisible(true);
    }

    public void onRefresh() {
        if (sorter != null) {
            sorter.setRowFilter(null); // ← xóa filter tìm kiếm
        }
        reload();
    }

    @Override
    protected void afterTableCreated() {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

    }

    private OrderResponse getSelectedOrder() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        Long id = (Long) table.getModel().getValueAt(modelRow, 1);
        return allOrders.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
    }

    private void onCreate() {
        Window win = SwingUtilities.getWindowAncestor(this);
        new CreateOrderDialog(win, userId).setVisible(true);
        reload();
    }

    private void onConfirm() {
        OrderResponse order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn đơn!");
            return;
        }
        if (!OrderStatus.NEW.name().equals(order.getStatus())) {
            JOptionPane.showMessageDialog(this, "Chỉ confirm được đơn NEW!");
            return;
        }

        List<OrderItemResponse> orderItems = orderController.getItems(order.getId());

        for (OrderItemResponse item : orderItems) {

            // 🔥 Đổi lại đúng key tồn kho
            Long batchId = item.getBatchId();
            System.out.println("[VIEW]Lô hàng ID lấy từ order item: " + batchId);
            Long warehouseId = item.getWarehouseId(); // ← chắc chắn DTO này map đúng cột warehouse_id từ DB
            System.out.println("[VIEW]Kho hàng ID lấy từ order item: " + warehouseId);

            // ✅ Kiểm tra thông tin order item có đầy đủ không
            if (warehouseId == null) {
                JOptionPane.showMessageDialog(this,
                        "Order item cho sản phẩm '" + item.getProductName() + "' không có thông tin kho!\n" +
                        "Vui lòng kiểm tra lại dữ liệu đơn hàng.",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int requiredQty = item.getQuantity();
            System.out.println("[VIEW]Yêu cầu số lượng cho sản phẩm " + item.getProductName() + ": " + requiredQty);

            // tồn kho phải check đúng batch + warehouse
            int available = inventoryController.getCurrentQuantity(item.getProductId(), batchId, warehouseId);
            System.out.println("[VIEW]Có sẵn từ kho: " + available);

            System.out.println("Kiểm tra tồn kho cho sản phẩm " + item.getProductName() + " (ID " + item.getProductId() +
                    ") tại kho " + warehouseId +
                    " lô " + batchId +
                    ": yêu cầu " + requiredQty +
                    ", còn " + available);

            if (available < requiredQty) {
                String batchInfo = batchId != null ? " ở lô " + batchId : " (không chỉ định lô)";
                JOptionPane.showMessageDialog(this,
                        "Sản phẩm: " + item.getProductName() + "\n" +
                        "Kho " + warehouseId + batchInfo + " không đủ hàng!\n" +
                        "Yêu cầu: " + requiredQty + ", Tồn kho: " + available,
                        "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int before = available;
            int after = before - requiredQty;

            inventoryController.updateStock(item.getProductId(), batchId, warehouseId, after);
            inventoryController.insertStockMovement(
                    item.getProductId(), batchId, warehouseId,
                    requiredQty, before, after, this.userId,
                    "Trừ kho từ Order: " + order.getOrderNumber()
            );
        }

        orderController.confirm(order.getId());
        reload();
    }

    private void onCancel() {
        OrderResponse order = getSelectedOrder();
        if (order == null) return;
        if (!OrderStatus.NEW.name().equals(order.getStatus())) return;
        orderController.cancel(order.getId());
        reload();
    }
}
