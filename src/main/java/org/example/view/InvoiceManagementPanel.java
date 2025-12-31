package org.example.view;

import org.example.controller.InvoiceController;
import org.example.dto.request.CreateInvoiceRequest;
import org.example.dto.response.InvoiceResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Invoice Management Screen
 */
public class InvoiceManagementPanel extends JPanel {

    private final InvoiceController controller;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtOrderId;
    private JTextField txtTotalAmount;
    private JTextField txtTaxCode;
    private JTextField txtPayAmount;

    // ====== CONSTRUCTOR DUY NHẤT ======
    public InvoiceManagementPanel() {
        this.controller = new InvoiceController();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        refreshTable(); // LOAD DATA NGAY
    }

    // ================= FORM =================

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));

        txtOrderId = new JTextField();
        txtTotalAmount = new JTextField();
        txtTaxCode = new JTextField();

        panel.add(new JLabel("Order ID"));
        panel.add(new JLabel("Total Amount"));
        panel.add(new JLabel("Tax Code"));
        panel.add(new JLabel(""));

        panel.add(txtOrderId);
        panel.add(txtTotalAmount);
        panel.add(txtTaxCode);

        JButton btnCreate = new JButton("Create Invoice");
        btnCreate.addActionListener(e -> createInvoice());
        panel.add(btnCreate);

        return panel;
    }

    // ================= TABLE =================

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID", "Invoice No", "Order ID",
                        "Issue Date", "Total", "Paid",
                        "Status", "Tax Code"
                }, 0
        );

        table = new JTable(tableModel);
        table.setRowHeight(24);

        return new JScrollPane(table);
    }

    // ================= ACTION =================

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        txtPayAmount = new JTextField(10);

        JButton btnPay = new JButton("Pay");
        btnPay.addActionListener(e -> payInvoice());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshTable());

        panel.add(new JLabel("Pay Amount"));
        panel.add(txtPayAmount);
        panel.add(btnPay);
        panel.add(btnRefresh);

        return panel;
    }

    // ================= LOGIC =================

    private void createInvoice() {
        try {
            CreateInvoiceRequest req = new CreateInvoiceRequest();
            req.setOrderId(Integer.parseInt(txtOrderId.getText()));
            req.setTotalAmount(new BigDecimal(txtTotalAmount.getText()));
            req.setTaxCode(txtTaxCode.getText());
            req.setDueDate(LocalDate.now().plusDays(7));

            controller.createInvoice(req);
            refreshTable();

            JOptionPane.showMessageDialog(this, "Create invoice success");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void payInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select invoice first");
            return;
        }

        try {
            Integer invoiceId = (Integer) tableModel.getValueAt(row, 0);
            BigDecimal amount = new BigDecimal(txtPayAmount.getText());

            controller.payInvoice(invoiceId, amount);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load dữ liệu bảng
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            // Nếu có nhập orderId → lọc
            if (!txtOrderId.getText().isBlank()) {
                InvoiceResponse r =
                        controller.getInvoiceByOrder(Integer.parseInt(txtOrderId.getText()));
                addRow(r);
                return;
            }

            // Ngược lại → load ALL
            List<InvoiceResponse> list = controller.getAllInvoices();
            for (InvoiceResponse r : list) {
                addRow(r);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addRow(InvoiceResponse r) {
        tableModel.addRow(new Object[]{
                r.getId(),
                r.getInvoiceNumber(),
                r.getOrderId(),
                r.getIssueDate(),
                r.getTotalAmount(),
                r.getPaidAmount(),
                r.getStatus(),
                r.getTaxCode()
        });
    }
}
