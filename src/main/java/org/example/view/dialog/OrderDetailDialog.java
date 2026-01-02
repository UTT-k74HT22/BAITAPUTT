package org.example.view.dialog;

import org.example.controller.OrderController;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderDetailDialog extends JDialog {

    private final OrderController controller = new OrderController();
    private final Long orderId;

    private String formatMoney(BigDecimal money) {
        return money.stripTrailingZeros().toPlainString();
    }

    public OrderDetailDialog(Window owner, Long orderId) {
        super(owner, "Chi tiết đơn hàng", ModalityType.APPLICATION_MODAL);
        this.orderId = orderId;

        setSize(820, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));

        add(buildInfo(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /* ================= INFO ================= */

    private JPanel buildInfo() {
        OrderResponse o = controller.getById(orderId);

        JPanel p = UiFactory.cardPanel();
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        // ===== Dòng 1 =====
        c.gridy = 0;
        c.gridx = 0; c.weightx = 0.2;
        p.add(label("Mã đơn: " + o.getOrderNumber()), c);

        c.gridx = 1; c.weightx = 0.3;
        p.add(label("Ngày: " + o.getOrderDate()), c);

        c.gridx = 2; c.weightx = 0.25;
        p.add(label("Trạng thái: " + o.getStatus()), c);

        c.gridx = 3; c.weightx = 0.25;
        p.add(label("Tổng tiền: " + formatMoney(o.getTotalAmount())), c);

        // ===== Dòng 2 =====
        c.gridy = 1;
        c.gridx = 0; c.weightx = 0.2;
        p.add(label("Khách hàng: " + safe(o.getCustomerName())), c);

        c.gridx = 1; c.weightx = 0.3;
        p.add(label("SĐT: " + safe(o.getCustomerPhone())), c);

        c.gridx = 2; c.weightx = 0.5;
        p.add(label("Email: " + safe(o.getCustomerEmail())), c);

        c.gridx = 3; c.weightx = 0.1;
        p.add(new JLabel(), c);

        // ===== Dòng 3: Địa chỉ giao hàng =====
        c.gridy = 2;
        c.gridx = 0; c.weightx = 0.2;
        p.add(label("Địa chỉ giao hàng:"), c);

        c.gridx = 1; c.weightx = 0.8;
        c.gridwidth = 3;
        p.add(new JLabel(safe(o.getShippingAddress())), c);
        c.gridwidth = 1;

        // ===== Dòng 4: Người tạo =====
        c.gridy = 3;
        c.gridx = 0; c.weightx = 0.2;
        p.add(label("Người tạo: " + safe(o.getCreatorName())), c);

        c.gridx = 1; c.weightx = 0.4;
        p.add(label("Email: " + safe(o.getCreatorEmail())), c);

        c.gridx = 2; c.weightx = 0.4;
        p.add(label("SĐT: " + safe(o.getCreatorPhone())), c);


        return p;
    }


    /* ================= TABLE ================= */

    private JScrollPane buildTable() {
        String[] cols = {
                "STT",
                "Tên thuốc",
                "Số lượng",
                "Đơn giá",
                "Thành tiền",
                "Ghi chú"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);

//        table.getColumnModel().getColumn(0).setPreferredWidth(40);
//        table.getColumnModel().getColumn(1).setPreferredWidth(200);
//        table.getColumnModel().getColumn(2).setPreferredWidth(80);
//        table.getColumnModel().getColumn(3).setPreferredWidth(120);
//        table.getColumnModel().getColumn(4).setPreferredWidth(120);
//        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        table.getTableHeader().setResizingAllowed(false); // (tuỳ chọn) khóa kéo size
        UiFactory.styleTable(table);
        UiFactory.styleHeader(table);

        List<OrderItemResponse> items = controller.getItems(orderId);

        int stt = 1;
        for (OrderItemResponse i : items) {
            model.addRow(new Object[]{
                    stt++,
                    i.getProductName(),
                    i.getQuantity(),
                    i.getUnitPrice(),
                    i.getLineTotal(),
                    safe(i.getNote())
            });
        }

        return new JScrollPane(table);
    }

    /* ================= FOOTER ================= */

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        p.setOpaque(false);

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(UiTheme.FONT_BOLD);
        btnClose.addActionListener(e -> dispose());

        p.add(btnClose);
        return p;
    }

    /* ================= UTIL ================= */

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UiTheme.FONT_BOLD);
        return l;
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
