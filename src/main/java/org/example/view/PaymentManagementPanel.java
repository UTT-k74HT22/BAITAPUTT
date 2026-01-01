package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.PaymentController;
import org.example.entity.Payment;
import org.example.entity.enums.PaymentStatus;
import org.example.service.impl.AccountServiceImpl;
import org.example.service.impl.PaymentServiceImpl;
import org.example.service.impl.UserCreationServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentManagementPanel extends JPanel {

    // ===================== DEPENDENCY =====================
    private final PaymentController controller;

    // ===================== UI COMPONENTS =====================
    private JTable paymentTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> methodFilter;
    private JLabel totalLabel;

    // ===================== CONSTANTS =====================
    private static final String[] TABLE_COLUMNS = {
            "STT", "ID", "Mã thanh toán", "Mã hóa đơn", "Ngày thanh toán", "Số tiền", "Phương thức", "Trạng thái"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    // ===================== CONSTRUCTOR =====================
    public PaymentManagementPanel() {
        this(new PaymentController(new PaymentServiceImpl()));
    }

    public PaymentManagementPanel(PaymentController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        initUI();
        reloadPaymentTable();
    }


    // ===================== INIT UI =====================
    private void initUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ===================== TOOLBAR =====================
    private JPanel buildToolbar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        container.add(buildFilterRow());
        container.add(buildActionRow());

        return container;
    }

    private JPanel buildFilterRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        searchField = createTextField(20);
        JButton searchBtn = createButton("Tìm", COLOR_PRIMARY, e -> searchByPaymentNumber());

        statusFilter = new JComboBox<>(new String[]{"ALL", "SUCCESS", "FAILED", "PENDING", "CANCELED"});
        methodFilter = new JComboBox<>(new String[]{"ALL", "CASH", "BANK_TRANSFER", "CREDIT_CARD", "E_WALLET"});

        statusFilter.addActionListener(e -> applyFilters());
        methodFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Mã thanh toán:"));
        row.add(searchField);
        row.add(searchBtn);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Trạng thái:"));
        row.add(statusFilter);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Phương thức:"));
        row.add(methodFilter);

        return row;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        row.add(createButton("Thêm", new Color(40,167,69), e -> openCreateDialog()));
        row.add(createButton("Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("Xoá", new Color(220,53,69), e -> deletePayment()));
        row.add(createButton("Refresh", new Color(108,117,125), e -> reloadPaymentTable()));
        row.add(Box.createHorizontalStrut(20));
        return row;
    }

    // ===================== TABLE =====================
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(38);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureTableHeader();
        configureTableAppearance();
        configureTableColumns();
        configureTableRenderers();

        JScrollPane scroll = new JScrollPane(paymentTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void configureTableHeader() {
        JTableHeader header = paymentTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        renderer.setBackground(COLOR_PRIMARY);
        renderer.setForeground(Color.WHITE);
        renderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        renderer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < paymentTable.getColumnCount(); i++) {
            paymentTable.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
        }
    }

    private void configureTableAppearance() {
        paymentTable.setShowGrid(false);
        paymentTable.setIntercellSpacing(new Dimension(0, 0));
        paymentTable.setSelectionBackground(COLOR_SELECT);
        paymentTable.setSelectionForeground(COLOR_TEXT);
        paymentTable.setFillsViewportHeight(true);
    }

    private void configureTableColumns() {
        int[] widths = {50, 60, 150, 120, 160, 140, 140, 120};
        for (int i = 0; i < widths.length; i++) {
            paymentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(COLOR_TEXT);
                setBackground(sel ? COLOR_SELECT : (r % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT));
                setHorizontalAlignment(c <= 1 ? CENTER : LEFT);
                return this;
            }
        };

        for (int i = 0; i < paymentTable.getColumnCount(); i++) {
            paymentTable.getColumnModel().getColumn(i).setCellRenderer(base);
        }
    }

    // ===================== DATA =====================
    private void reloadPaymentTable() {
        renderTable(controller.getAllPayments());
    }

    private void searchByPaymentNumber() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            reloadPaymentTable();
            return;
        }

        List<Payment> allPayments = controller.getAllPayments();
        List<Payment> filtered = allPayments.stream()
                .filter(p -> p.getPaymentNumber() != null &&
                        p.getPaymentNumber().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        renderTable(filtered);
    }

    private void applyFilters() {
        List<Payment> data = controller.getAllPayments();

        String status = (String) statusFilter.getSelectedItem();
        String method = (String) methodFilter.getSelectedItem();

        List<Payment> filtered = data.stream()
                .filter(p -> "ALL".equals(status) || p.getStatus().name().equals(status))
                .filter(p -> "ALL".equals(method) ||
                        (p.getMethod() != null && p.getMethod().equals(method)))
                .collect(Collectors.toList());

        renderTable(filtered);
    }

    private void renderTable(List<Payment> payments) {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int index = 1;
        for (Payment p : payments) {
            tableModel.addRow(new Object[]{
                    index++,
                    p.getId(),
                    p.getPaymentNumber(),
                    p.getInvoiceId(),
                    p.getPaymentDate() == null ? "" : p.getPaymentDate().format(fmt),
                    String.format("%,.0f VNĐ", p.getAmount()),
                    p.getMethod() == null ? "" : p.getMethod(),
                    p.getStatus() == null ? "" : p.getStatus().name()
            });
        }
        totalLabel.setText("Tổng số thanh toán: " + payments.size());
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        totalLabel = new JLabel("Tổng số thanh toán: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(totalLabel);
        return panel;
    }

    // ===================== CRUD =====================
    private void openCreateDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm thanh toán",
                true
        );
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField tfInvoiceId = new JTextField(20);
        JTextField tfAmount = new JTextField(20);

        JComboBox<String> cbMethod = new JComboBox<>(
                new String[]{"CASH", "BANK_TRANSFER", "CREDIT_CARD", "E_WALLET"}
        );

        JComboBox<PaymentStatus> cbStatus =
                new JComboBox<>(PaymentStatus.values());

        int row = 0;

        // ===== Invoice ID =====
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Mã hóa đơn"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfInvoiceId, gbc);

        // ===== Amount =====
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Số tiền"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfAmount, gbc);

        // ===== Method =====
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Phương thức"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbMethod, gbc);

        // ===== Status =====
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Trạng thái"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbStatus, gbc);

        // ===== ACTIONS =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            try {
                int invoiceId = Integer.parseInt(tfInvoiceId.getText().trim());
                java.math.BigDecimal amount =
                        new java.math.BigDecimal(tfAmount.getText().trim());

                Payment payment = new Payment();
                payment.setInvoiceId(invoiceId);
                payment.setAmount(amount);
                payment.setMethod((String) cbMethod.getSelectedItem());
                payment.setStatus((PaymentStatus) cbStatus.getSelectedItem());
                payment.setPaymentDate(java.time.LocalDateTime.now());
                payment.setPaymentNumber("PM" + System.currentTimeMillis());

                controller.createPayment(payment);

                JOptionPane.showMessageDialog(dialog,
                        "Thêm thanh toán thành công!");
                dialog.dispose();
                reloadPaymentTable();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void openEditDialog() {
        Payment payment = getSelectedPayment();
        if (payment == null) return;

        JOptionPane.showMessageDialog(this, "TODO: Chức năng sửa thanh toán");
    }

    private void deletePayment() {
        Payment payment = getSelectedPayment();
        if (payment == null) return;

        JOptionPane.showMessageDialog(this, "TODO: Chức năng hủy thanh toán");
    }

    // ===================== EXPORT =====================
    private void exportExcel() {
        JOptionPane.showMessageDialog(this, "TODO: Export Excel");
    }

    private void exportPdf() {
        JOptionPane.showMessageDialog(this, "TODO: Export PDF");
    }

    // ===================== UTIL =====================
    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private JButton createButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.addActionListener(action);
        return b;
    }

    private Payment getSelectedPayment() {
        int row = paymentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thanh toán từ bảng.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return null;
        } else {
            long paymentId = (long) tableModel.getValueAt(row, 1);
            return controller.getPaymentById(paymentId);
        }
    }
}