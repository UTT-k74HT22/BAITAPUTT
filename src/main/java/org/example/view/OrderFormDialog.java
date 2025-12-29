package org.example.view;

import org.example.controller.OrderController;
import org.example.dto.request.Order.CreateOrderRequest;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import java.awt.*;

public class OrderFormDialog extends JDialog {

    private final OrderController controller = new OrderController();
    private final Long currentEmployeeId;

    private JTextField txtCustomerId;
    private JTextField txtShippingAddress;
    private JTextArea txtNote;

    private boolean submitted = false;
    private Long createdOrderId;

    public OrderFormDialog(Window owner, Long currentEmployeeId) {
        super(owner, "Tạo đơn hàng", ModalityType.APPLICATION_MODAL);
        this.currentEmployeeId = currentEmployeeId;

        setSize(420, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));
        setResizable(false);

        add(buildForm(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    /* ================= UI ================= */

    private JPanel buildForm() {
        JPanel p = UiFactory.cardPanel();
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = 0;
        p.add(label("ID Khách hàng"), g);

        g.gridx = 1;
        txtCustomerId = new JTextField();
        p.add(txtCustomerId, g);

        g.gridx = 0;
        g.gridy++;
        p.add(label("Địa chỉ giao hàng"), g);

        g.gridx = 1;
        txtShippingAddress = new JTextField();
        p.add(txtShippingAddress, g);

        g.gridx = 0;
        g.gridy++;
        g.anchor = GridBagConstraints.NORTHWEST;
        p.add(label("Ghi chú"), g);

        g.gridx = 1;
        txtNote = new JTextArea(4, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        p.add(new JScrollPane(txtNote), g);

        return p;
    }

    private JPanel buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        p.setOpaque(false);

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Tạo đơn");

        btnSave.setFont(UiTheme.FONT_BOLD);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSubmit());

        p.add(btnCancel);
        p.add(btnSave);
        return p;
    }


    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UiTheme.FONT_BOLD);
        return l;
    }

    /* ================= ACTION ================= */

    private void onSubmit() {
        if (txtCustomerId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập ID khách hàng",
                    "Thiếu dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long customerId;
        try {
            customerId = Long.parseLong(txtCustomerId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID khách hàng không hợp lệ",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        CreateOrderRequest req = new CreateOrderRequest();
        req.setCustomerId(customerId);
        req.setShippingAddress(txtShippingAddress.getText().trim());
        req.setNote(txtNote.getText().trim());

        try {
            createdOrderId = controller.createOrder(req, currentEmployeeId);
            submitted = true;

            JOptionPane.showMessageDialog(this,
                    "Tạo đơn hàng thành công",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tạo đơn hàng:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ================= PUBLIC API ================= */

    public boolean isSubmitted() {
        return submitted;
    }

    public Long getCreatedOrderId() {
        return createdOrderId;
    }
}
