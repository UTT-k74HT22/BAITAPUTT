package org.example.view;

import org.example.controller.CustomerController;
import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerManagementPanel extends JPanel {

    // ===================== CONTROLLER =====================
    private final CustomerController controller = new CustomerController();

    // ===================== TABLE =====================
    private final DefaultTableModel model;
    private final JTable table;

    // ===================== FORM FIELDS =====================
    private final JTextField txtCode    = createTextField();
    private final JTextField txtName    = createTextField();
    private final JTextField txtType    = createTextField();
    private final JTextField txtPhone   = createTextField();
    private final JTextField txtEmail   = createTextField();
    private final JTextField txtAddress = createTextField();
    private final JTextField txtTaxCode = createTextField();

    // ===================== BUTTONS =====================
    private final JButton btnAdd    = createButton("➕ Thêm", new Color(40,167,69));
    private final JButton btnUpdate = createButton("✏️ Sửa", new Color(255,193,7));
    private final JButton btnDelete = createButton("🗑️ Xóa", new Color(220,53,69));
    private final JButton btnClear  = createButton("🧹 Clear", new Color(108,117,125));
    private final JButton btnReload = createButton("🔄 Reload", COLOR_PRIMARY);

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    // ===================== CONSTRUCTOR =====================
    public CustomerManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        model = new DefaultTableModel(
                new String[]{
                        "ID", "Code", "Name", "Type", "Phone", "Email",
                        "Address", "Tax Code", "Username", "Status"
                }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        styleTable();
        initLayout();
        initEvents();
        loadData();
    }

    /* ====================== DATA ====================== */

    private void loadData() {
        model.setRowCount(0);

        for (CustomerResponse c : controller.getAllActiveCustomers()) {
            model.addRow(new Object[]{
                    c.getId(),
                    c.getCode(),
                    c.getName(),
                    c.getType(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getAddress(),
                    c.getTaxCode(),
                    c.getUsername(),
                    c.getStatus()
            });
        }
    }

    /* ====================== EVENTS ====================== */

    private void initEvents() {
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearSelection());
        btnReload.addActionListener(e -> reload());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    /* ====================== ACTIONS ====================== */

    private void onAdd() {
        if (!validateForm()) return;

        try {
            controller.create(buildCustomerFromForm());
            reload();
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        Integer id = getSelectedId();
        if (id == null || !validateForm()) return;

        try {
            Customer c = buildCustomerFromForm();
            c.setId(id.longValue());
            controller.update(c);
            reload();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Integer id = getSelectedId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa (ẩn) khách hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.softDelete(id);
            reload();
        }
    }

    /* ====================== HELPERS ====================== */

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtCode.setText(model.getValueAt(row, 1).toString());
        txtName.setText(model.getValueAt(row, 2).toString());
        txtType.setText(model.getValueAt(row, 3).toString());
        txtPhone.setText(model.getValueAt(row, 4).toString());
        txtEmail.setText(model.getValueAt(row, 5).toString());
        txtAddress.setText(model.getValueAt(row, 6).toString());
        txtTaxCode.setText(model.getValueAt(row, 7).toString());
    }

    private Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarn("Vui lòng chọn khách hàng");
            return null;
        }
        return ((Number) model.getValueAt(row, 0)).intValue();
    }

    private Customer buildCustomerFromForm() {
        Customer c = new Customer();
        c.setCode(txtCode.getText().trim());
        c.setName(txtName.getText().trim());
        c.setType(txtType.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        c.setTaxCode(txtTaxCode.getText().trim());
        return c;
    }

    private boolean validateForm() {
        if (txtCode.getText().isBlank()) {
            showWarn("Mã khách hàng không được để trống");
            return false;
        }
        if (txtName.getText().isBlank()) {
            showWarn("Tên khách hàng không được để trống");
            return false;
        }
        return true;
    }

    private void reload() {
        loadData();
        clearSelection();
    }

    private void clearSelection() {
        clearForm();
        table.clearSelection();
    }

    private void clearForm() {
        txtCode.setText("");
        txtName.setText("");
        txtType.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtTaxCode.setText("");
    }

    /* ====================== UI ====================== */

    private void initLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = baseGbc();

        // 2 cột label-field trên 4 hàng
        addField(formPanel, gbc, 0, 0, "Mã KH", txtCode);
        addField(formPanel, gbc, 2, 0, "Tên KH", txtName);
        addField(formPanel, gbc, 0, 1, "Loại", txtType);
        addField(formPanel, gbc, 2, 1, "SĐT", txtPhone);
        addField(formPanel, gbc, 0, 2, "Email", txtEmail);
        addField(formPanel, gbc, 2, 2, "Địa chỉ", txtAddress);
        addField(formPanel, gbc, 0, 3, "Mã số thuế", txtTaxCode);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnReload);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void styleTable() {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(COLOR_PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private static JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return b;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int x, int y, String label, JTextField field) {
        // label
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        // field
        gbc.gridx = x + 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}
