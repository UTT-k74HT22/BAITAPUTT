package org.example.view;

import org.example.entity.Customer;
import org.example.service.CustomerService;
import org.example.service.impl.CustomerServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerManagementPanel extends JPanel {

    private final CustomerService service = new CustomerServiceImpl();
    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtCode = createTextField();
    private final JTextField txtName = createTextField();
    private final JTextField txtType = createTextField();
    private final JTextField txtPhone = createTextField();
    private final JTextField txtEmail = createTextField();
    private final JTextField txtAddress = createTextField();
    private final JTextField txtTaxCode = createTextField();

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

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        model = new DefaultTableModel(
                new String[]{
                        "ID", "Code", "Name", "Phone", "Email", "Username", "Status"
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

    /* ====================== TABLE STYLE ====================== */

    private void styleTable() {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(COLOR_PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(COLOR_TEXT);
                setBackground(sel ? COLOR_SELECT : (r % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT));
                setHorizontalAlignment(c == 0 ? CENTER : LEFT);
                return this;
            }

        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(COLOR_SELECT);
        table.setFillsViewportHeight(true);
    }

    /* ====================== DATA ====================== */

    private void loadData() {
        model.setRowCount(0);

        service.getAllActiveWithAccount().forEach(c ->
                model.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getCustomerCode(),
                        c.getCustomerName(),
                        c.getPhone(),
                        c.getEmail(),
                        c.getUsername(),
                        c.getStatus()
                })
        );
    }


    /* ====================== LAYOUT ====================== */

    private void initLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = baseGbc();

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
        scroll.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
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
            service.create(buildCustomerFromForm());
            reload();

            JOptionPane.showMessageDialog(
                    this,
                    "Thêm khách hàng thành công",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void onUpdate() {
        Integer id = getSelectedId();
        if (id == null || !validateForm()) return;

        Customer c = buildCustomerFromForm();
        c.setId(id.longValue());
        service.update(c);
        reload();
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
            service.softDelete(id);
            reload();
        }
    }

    /* ====================== HELPERS ====================== */

    private Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarn("Vui lòng chọn khách hàng");
            return null;
        }
        return ((Number) model.getValueAt(row, 0)).intValue();
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtCode.setText(String.valueOf(model.getValueAt(row, 1)));
        txtName.setText(String.valueOf(model.getValueAt(row, 2)));
        txtPhone.setText(String.valueOf(model.getValueAt(row, 3)));
        txtEmail.setText(String.valueOf(model.getValueAt(row, 4)));

        // Những field không có trong DTO thì KHÔNG map
        txtType.setText("");
        txtAddress.setText("");
        txtTaxCode.setText("");
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
        if (txtCode.getText().trim().isEmpty()) {
            showWarn("Mã khách hàng không được để trống");
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
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

    /* ====================== UI UTILS ====================== */

    private static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private static JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int x, int y, String label, JTextField field) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}
