package org.example.view;

import org.example.controller.OrderController;
import org.example.dto.response.Order.OrderResponse;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class OrderManagementPanel extends BaseManagementPanel<OrderResponse> {

    private final OrderController controller = new OrderController();

    private JTextField keywordField;

    private JButton btnCreate;
    private JButton btnConfirm;
    private JButton btnCancel;

    /* ================= BASE CONTRACT ================= */

    @Override
    protected String titleTotal() {
        return "Tổng số đơn hàng";
    }

    @Override
    protected String[] columns() {
        return new String[]{
                "STT",
                "Mã đơn",
                "Khách hàng",
                "Ngày đặt",
                "Trạng thái",
                "Tổng tiền"
        };
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{60, 140, 220, 160, 140, 140};
    }

    @Override
    protected List<OrderResponse> fetchData() {
        return controller.getAllOrders();
    }

    @Override
    protected Object[] mapRow(OrderResponse o, int stt) {
        return new Object[]{
                stt,
                o.getOrderNumber(),
                o.getCustomerName(),
                o.getOrderDate(),
                o.getStatus(),
                o.getTotalAmount()
        };
    }

    /* ================= TOOLBAR ================= */

    @Override
    protected JPanel buildFilters() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);

        keywordField = new JTextField(20);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> applyFilters());

        p.add(new JLabel("Tìm kiếm:"));
        p.add(keywordField);
        p.add(btnSearch);

        return p;
    }

    @Override
    protected JPanel buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);

        btnCreate = new JButton("Tạo đơn");
        btnConfirm = new JButton("Xác nhận");
        btnCancel = new JButton("Hủy");

        btnCreate.setFont(UiTheme.FONT_BOLD);

        btnCreate.addActionListener(e -> onCreate());
        btnConfirm.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> onCancel());

        p.add(btnCreate);
        p.add(btnConfirm);
        p.add(btnCancel);

        return p;
    }

    /* ================= TABLE HOOK ================= */

    @Override
    protected void afterTableCreated() {
        // căn giữa cột trạng thái
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
    }

    /* ================= FILTER ================= */

    @Override
    protected void applyFilters() {
        String kw = keywordField.getText().trim().toLowerCase();

        if (kw.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + kw, 1, 2));
    }

    /* ================= ACTION HANDLERS ================= */

    private void onCreate() {
        Window win = SwingUtilities.getWindowAncestor(this);
        OrderFormDialog dlg = new OrderFormDialog(win, 1L); // TODO: lấy employeeId thật
        dlg.setVisible(true);

        if (dlg.isSubmitted()) {
            reload();
        }
    }

    private void onConfirm() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đơn hàng",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = getCellString(row, 4);
        if (!"NEW".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ xác nhận đơn ở trạng thái NEW",
                    "Không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận đơn hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            controller.confirmOrder(getOrderIdAt(row));
            reload();
        }
    }

    private void onCancel() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đơn hàng",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Hủy đơn hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            controller.cancelOrder(getOrderIdAt(row));
            reload();
        }
    }

    /* ================= HELPER ================= */

    private Long getOrderIdAt(int modelRow) {
        String orderNumber = getCellString(modelRow, 1);
        return controller.getAllOrders().stream()
                .filter(o -> o.getOrderNumber().equals(orderNumber))
                .findFirst()
                .map(OrderResponse::getId)
                .orElse(null);
    }
}
