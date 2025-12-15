package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.service.impl.AccountServiceImpl;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AccountManagementPanel extends JPanel {

    // ===================== DEPENDENCY =====================
    private final AccountController controller;

    // ===================== UI COMPONENTS =====================
    private JTable accountTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;
    private JLabel totalLabel;

    // ===================== CONSTANTS =====================
    private static final String[] TABLE_COLUMNS = {
            "STT", "ID", "Username", "Email", "Phone", "Role", "Status", "Last Login"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    // ===================== CONSTRUCTOR =====================
    public AccountManagementPanel() {
        this(new AccountController(new AccountServiceImpl()));
    }

    public AccountManagementPanel(AccountController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        initUI();
        reloadAccountTable();
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
        JButton searchBtn = createButton("🔍 Tìm", COLOR_PRIMARY, e -> searchByUsername());

        roleFilter = new JComboBox<>(new String[]{"ALL", "ADMIN", "EMPLOYEE", "CUSTOMER"});
        statusFilter = new JComboBox<>(new String[]{"ALL", "ACTIVE", "INACTIVE"});

        roleFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Username:"));
        row.add(searchField);
        row.add(searchBtn);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Role:"));
        row.add(roleFilter);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Status:"));
        row.add(statusFilter);

        return row;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        row.add(createButton("➕ Thêm", new Color(40,167,69), e -> openCreateDialog()));
        row.add(createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("🗑️ Xóa", new Color(220,53,69), e -> deleteSelectedAccount()));
        row.add(createButton("🔄 Refresh", new Color(108,117,125), e -> reloadAccountTable()));
        row.add(Box.createHorizontalStrut(20));
        row.add(createButton("📊 Excel", new Color(23,162,184), e -> exportExcel()));
        row.add(createButton("📄 PDF", new Color(111,66,193), e -> exportPdf()));

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

        accountTable = new JTable(tableModel);
        accountTable.setRowHeight(38);
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureTableHeader();
        configureTableAppearance();
        configureTableColumns();
        configureTableRenderers();

        JScrollPane scroll = new JScrollPane(accountTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void configureTableHeader() {
        JTableHeader header = accountTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        renderer.setBackground(COLOR_PRIMARY);
        renderer.setForeground(Color.WHITE);
        renderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        renderer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < accountTable.getColumnCount(); i++) {
            accountTable.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
        }
    }

    private void configureTableAppearance() {
        accountTable.setShowGrid(false);
        accountTable.setIntercellSpacing(new Dimension(0, 0));
        accountTable.setSelectionBackground(COLOR_SELECT);
        accountTable.setSelectionForeground(COLOR_TEXT);
        accountTable.setFillsViewportHeight(true);
    }

    private void configureTableColumns() {
        int[] widths = {50, 60, 140, 220, 120, 100, 110, 160};
        for (int i = 0; i < widths.length; i++) {
            accountTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
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
                setHorizontalAlignment(c <= 1 || c >= 6 ? CENTER : LEFT);
                return this;
            }
        };

        for (int i = 0; i < accountTable.getColumnCount(); i++) {
            accountTable.getColumnModel().getColumn(i).setCellRenderer(base);
        }
    }

    // ===================== DATA =====================
    private void reloadAccountTable() {
        renderTable(controller.getAllAccounts());
    }

    private void searchByUsername() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            reloadAccountTable();
            return;
        }
        Account a = controller.findByUsername(keyword);
        renderTable(a == null ? List.of() : List.of(a));
    }

    private void applyFilters() {
        List<Account> data = controller.getAllAccounts();

        String role = (String) roleFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();

        List<Account> filtered = data.stream()
                .filter(a -> "ALL".equals(role) || a.getRole().name().equals(role))
                .filter(a -> "ALL".equals(status) ||
                        ("ACTIVE".equals(status) && a.isActive()) ||
                        ("INACTIVE".equals(status) && !a.isActive()))
                .collect(Collectors.toList());

        renderTable(filtered);
    }

    private void renderTable(List<Account> accounts) {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int index = 1;
        for (Account a : accounts) {
            tableModel.addRow(new Object[]{
                    index++, a.getId(), a.getUsername(),
                    a.getEmail(), a.getPhone(),
                    a.getRole(), a.isActive() ? "ACTIVE" : "INACTIVE",
                    a.getLastLoginAt() == null ? "" : a.getLastLoginAt().format(fmt)
            });
        }
        totalLabel.setText("Tổng số tài khoản: " + accounts.size());
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        totalLabel = new JLabel("Tổng số tài khoản: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(totalLabel);
        return panel;
    }

    // ===================== CRUD =====================
    private void openCreateDialog() {
        showAccountDialog(null);
    }

    private void openEditDialog() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account account = buildAccountFromTableRow(selectedRow);
        showAccountDialog(account);
    }

    private void deleteSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 1);
        int id;
        if (idValue instanceof Number) {
            id = ((Number) idValue).intValue();
        } else {
            try {
                id = Integer.parseInt(String.valueOf(idValue));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID tài khoản không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        controller.deleteAccountById(id);
        reloadAccountTable();
    }

    // ===================== EXPORT =====================
    private void exportExcel() { JOptionPane.showMessageDialog(this,"TODO: Export Excel"); }
    private void exportPdf()   { JOptionPane.showMessageDialog(this,"TODO: Export PDF"); }

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

    // ===================== FORM DIALOG =====================

    private void showAccountDialog(Account accountToEdit) {
        boolean isEdit = accountToEdit != null;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Cập nhật tài khoản" : "Tạo tài khoản mới",
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = createTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = createTextField(20);
        JTextField phoneField = createTextField(20);
        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        JCheckBox activeCheck = new JCheckBox("Hoạt động");

        if (isEdit) {
            usernameField.setText(accountToEdit.getUsername());
            emailField.setText(accountToEdit.getEmail());
            phoneField.setText(accountToEdit.getPhone());
            roleBox.setSelectedItem(accountToEdit.getRole());
            activeCheck.setSelected(accountToEdit.isActive());
        } else {
            activeCheck.setSelected(true);
        }

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(usernameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(isEdit ? "Password (để trống nếu không đổi)" : "Password"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(passwordField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(emailField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Số điện thoại"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(phoneField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Role"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(roleBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Trạng thái"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(activeCheck, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton(isEdit ? "Cập nhật" : "Tạo mới");
        JButton btnCancel = new JButton("Hủy");
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnOk.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Role role = (Role) roleBox.getSelectedItem();
            boolean active = activeCheck.isSelected();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username không được để trống", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isEdit && password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password không được để trống", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Account acc = isEdit ? accountToEdit : new Account();
            acc.setUsername(username);
            if (!password.isEmpty()) {
                acc.setPassword(password);
            }
            acc.setEmail(email.isEmpty() ? null : email);
            acc.setPhone(phone.isEmpty() ? null : phone);
            acc.setRole(role);
            acc.setActive(active);

            try {
                if (isEdit) {
                    controller.updateAccount(acc);
                } else {
                    controller.createAccount(acc);
                }
                reloadAccountTable();
                dialog.dispose();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(form, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private Account buildAccountFromTableRow(int rowIndex) {
        Account a = new Account();

        Object idValue = tableModel.getValueAt(rowIndex, 1);
        if (idValue instanceof Number) {
            a.setId(((Number) idValue).longValue());
        } else {
            try {
                a.setId(Long.parseLong(String.valueOf(idValue)));
            } catch (NumberFormatException ignored) {
            }
        }

        a.setUsername(String.valueOf(tableModel.getValueAt(rowIndex, 2)));
        a.setEmail(String.valueOf(tableModel.getValueAt(rowIndex, 3)));
        a.setPhone(String.valueOf(tableModel.getValueAt(rowIndex, 4)));

        Object roleValue = tableModel.getValueAt(rowIndex, 5);
        if (roleValue instanceof Role) {
            a.setRole((Role) roleValue);
        } else if (roleValue != null) {
            try {
                a.setRole(Role.valueOf(roleValue.toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        Object statusValue = tableModel.getValueAt(rowIndex, 6);
        a.setActive("ACTIVE".equalsIgnoreCase(String.valueOf(statusValue)));

        return a;
    }
}
