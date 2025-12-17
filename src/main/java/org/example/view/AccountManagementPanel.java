package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.service.impl.AccountServiceImpl;
import org.example.service.impl.UserCreationServiceImpl;

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
            "STT", "ID", "Username", "Role", "Status", "Last Login"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    // ===================== CONSTRUCTOR =====================
    public AccountManagementPanel() {
        this(new AccountController(new AccountServiceImpl(), new UserCreationServiceImpl()));
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
        int[] widths = {50, 70, 200, 140, 120, 200};
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
                    index++,
                    a.getId(),
                    a.getUsername(),
                    a.getRole() == null ? "" : a.getRole().name(),
                    a.isActive() ? "ACTIVE" : "INACTIVE",
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo Account", true);
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField tfUsername = new JTextField(20);
        JPasswordField pfPassword = new JPasswordField(20);
        JPasswordField pfConfirm = new JPasswordField(20);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"EMPLOYEE", "CUSTOMER"});
        JCheckBox cbActive = new JCheckBox("Active", true);

        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfUsername, gbc);

        // Password
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(pfPassword, gbc);

        // Confirm
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Confirm"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(pfConfirm, gbc);

        // Role
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Role"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbRole, gbc);

        // Active
        row++;
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(cbActive, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            try {
                String username = tfUsername.getText().trim();
                String password = new String(pfPassword.getPassword()).trim();
                String confirm  = new String(pfConfirm.getPassword()).trim();

                org.example.dto.request.CreateAccountRequest req =
                        new org.example.dto.request.CreateAccountRequest();

                req.setUsername(username);
                req.setPassword(password);
                req.setConfirmPassword(confirm);
                req.setRole(org.example.entity.enums.Role.valueOf((String) cbRole.getSelectedItem()));
                req.setActive(cbActive.isSelected());

                controller.createAccount(req);

                JOptionPane.showMessageDialog(dialog, "Tạo account thành công!");
                dialog.dispose();
                reloadAccountTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openEditDialog() {
        Account acc = getSelectedAccount();
        if (acc == null) return;

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cập nhật tài khoản",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(400, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10,10));

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(3,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField usernameField = new JTextField(acc.getUsername());
        usernameField.setEnabled(false);

        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        roleBox.setSelectedItem(acc.getRole());

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        statusBox.setSelectedItem(acc.isActive() ? "ACTIVE" : "INACTIVE");

        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Role"));
        form.add(roleBox);
        form.add(new JLabel("Status"));
        form.add(statusBox);

        // ===== BUTTON =====
        JButton saveBtn = new JButton("Lưu");
        JButton cancelBtn = new JButton("Hủy");

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(cancelBtn);
        footer.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);

        // ===== EVENTS =====
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            Role newRole = (Role) roleBox.getSelectedItem();
            boolean newStatus = "ACTIVE".equals(statusBox.getSelectedItem());

            controller.updateRoleAndStatus(acc.getId(), newRole, newStatus);

            dialog.dispose();
            reloadAccountTable();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
        });

        dialog.setVisible(true);
    }
    private void deleteSelectedAccount() {
        Account acc = getSelectedAccount();
        if (acc == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa tài khoản '" + acc.getUsername() + "' không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteAccountById(acc.getId());
            reloadAccountTable();
            JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công.");
        }
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

    private Account getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ bảng.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return null;
        } else {
            long accountId = (long) tableModel.getValueAt(row, 1);
            return controller.findById(accountId);
        }
    }
}
