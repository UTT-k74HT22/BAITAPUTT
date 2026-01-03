package org.example.view;

import org.example.controller.InvoiceController;
import org.example.dto.request.CreateInvoiceRequest;
import org.example.dto.response.InvoiceResponse;
import org.example.entity.enums.InvoiceStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceManagementPanel extends JPanel {

    private final InvoiceController controller = new InvoiceController();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtOrderId;
    private JTextField txtPayAmount;
    private JTextField txtTotal;


    public InvoiceManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildCreatePanel(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ================= CREATE =================

    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtOrderId = new JTextField(10);
        txtTotal = new JTextField(10);

        panel.add(new JLabel("Total:"));
        panel.add(txtTotal);


        JButton btnCreate = new JButton("Create Invoice");
        btnCreate.addActionListener(e -> createInvoice());
        JButton btnEdit = new JButton("Edit Status");
        JButton btnDelete = new JButton("Delete");
        JButton btnReload = new JButton("Reload");

        btnEdit.addActionListener(e -> editInvoice());
        btnDelete.addActionListener(e -> deleteInvoice());
        btnReload.addActionListener(e -> refreshTable());


        panel.add(new JLabel("Order ID:"));
        panel.add(txtOrderId);
        panel.add(btnCreate);
        panel.add(btnEdit);
        panel.add(btnDelete);


        return panel;
    }

    private void createInvoice() {
        if (txtOrderId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Order ID is required");
            return;
        }

        try {
            int orderId = Integer.parseInt(txtOrderId.getText());

            CreateInvoiceRequest req = new CreateInvoiceRequest();
            req.setOrderId(orderId);


            BigDecimal total = new BigDecimal(txtTotal.getText());

            CreateInvoiceRequest requestq = new CreateInvoiceRequest();

            req.setTotalAmount(total);


            // ❗ KHÔNG SET total / taxCode
            controller.createInvoice(req);

            refreshTable();
            txtOrderId.setText("");

            JOptionPane.showMessageDialog(this, "Invoice created successfully");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Order ID must be number");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }
    private void editInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = (int) tableModel.getValueAt(row, 0);

        InvoiceStatus status = (InvoiceStatus) JOptionPane.showInputDialog(
                this,
                "Select status",
                "Edit Invoice",
                JOptionPane.PLAIN_MESSAGE,
                null,
                InvoiceStatus.values(),
                InvoiceStatus.NEW
        );

        if (status != null) {
            controller.updateInvoiceStatus(id, status);
            refreshTable();
        }
    }
    private void deleteInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select invoice first");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Soft delete this invoice?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) tableModel.getValueAt(row, 0);
        controller.deleteInvoice(id);
        refreshTable();
    }


    // ================= TABLE =================

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID", "Invoice No", "Order ID",
                        "Issue Date", "Total", "Paid",
                        "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(table);
    }

    // ================= PAYMENT =================

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        txtPayAmount = new JTextField(10);

        JButton btnPay = new JButton("Pay");
        btnPay.addActionListener(e -> payInvoice());

        JButton btnReload = new JButton("Reload");
        btnReload.addActionListener(e -> refreshTable());

        panel.add(new JLabel("Pay Amount:"));
        panel.add(txtPayAmount);
        panel.add(btnPay);
        panel.add(btnReload);

        return panel;
    }

    private void payInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select invoice first");
            return;
        }

        try {
            int invoiceId = (int) tableModel.getValueAt(row, 0);
            BigDecimal amount = new BigDecimal(txtPayAmount.getText());

            controller.payInvoice(invoiceId, amount);

            refreshTable();
            txtPayAmount.setText("");

            JOptionPane.showMessageDialog(this, "Payment success");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= LOAD =================

    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            List<InvoiceResponse> list = controller.getAllInvoices();
            for (InvoiceResponse r : list) {

                BigDecimal total =
                        r.getTotalAmount() != null
                                ? r.getTotalAmount()
                                : BigDecimal.ZERO;

                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getInvoiceNumber(),
                        r.getOrderId(),
                        r.getIssueDate(),
                        total,               // ⬅ FIX Ở ĐÂY
                        r.getPaidAmount(),
                        r.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
