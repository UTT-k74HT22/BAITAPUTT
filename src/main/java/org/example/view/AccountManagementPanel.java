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
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountManagementPanel extends JPanel {

    private final AccountController controller;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> activeFilter;

    private JLabel totalLabel;

    private final String[] columnNames = {
            "STT", "ID", "Username", "Email", "Phone", "Role", "Active", "Last Login"
    };

    public AccountManagementPanel() {
        this(new AccountController(new AccountServiceImpl()));
    }

    public AccountManagementPanel(AccountController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        initComponents();
        loadAccounts();
    }

    private void initComponents() {
        add(createToolbar(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // ===================== UI: TOOLBAR =====================
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Row 1: Search + Filter
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        topRow.setOpaque(false);

        searchField = createStyledTextField(22);

        JButton searchBtn = createStyledButton("🔍 Tìm", new Color(100, 149, 237));
        searchBtn.addActionListener(e -> searchAccounts());

        roleFilter = new JComboBox<>(new String[]{"Tất cả Role", "ADMIN", "EMPLOYEE", "CUSTOMER"});
        roleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleFilter.addActionListener(e -> applyFilters());

        activeFilter = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động", "Vô hiệu hóa"});
        activeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeFilter.addActionListener(e -> applyFilters());

        topRow.add(new JLabel("Tìm (username):"));
        topRow.add(searchField);
        topRow.add(searchBtn);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(new JLabel("Role:"));
        topRow.add(roleFilter);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(new JLabel("Trạng thái:"));
        topRow.add(activeFilter);

        // Row 2: Actions
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        bottomRow.setOpaque(false);

        JButton addBtn = createStyledButton("➕ Thêm", new Color(40, 167, 69));
        addBtn.addActionListener(e -> showAddDialog());

        JButton editBtn = createStyledButton("✏️ Sửa", new Color(255, 193, 7));
        editBtn.addActionListener(e -> showEditDialog());

        JButton deleteBtn = createStyledButton("🗑️ Xóa", new Color(220, 53, 69));
        deleteBtn.addActionListener(e -> deleteAccount());

        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshBtn.addActionListener(e -> loadAccounts());

        bottomRow.add(addBtn);
        bottomRow.add(editBtn);
        bottomRow.add(deleteBtn);
        bottomRow.add(refreshBtn);

        toolbar.add(topRow);
        toolbar.add(bottomRow);
        return toolbar;
    }

    // ===================== UI: TABLE =====================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 245));

        styleHeader(table);
        styleColumns(table);
        applyBadgeRenderers(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(100, 149, 237));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);
        header.setOpaque(true);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(100, 149, 237));
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
                setOpaque(true);
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private void styleColumns(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
        table.getColumnModel().getColumn(7).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
    }

    private void applyBadgeRenderers(JTable table) {
        // Active badge
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);

                if (!isSelected) {
                    String v = String.valueOf(value);
                    if ("Hoạt động".equalsIgnoreCase(v)) {
                        c.setBackground(new Color(212, 237, 218));
                        c.setForeground(new Color(21, 87, 36));
                    } else {
                        c.setBackground(new Color(248, 215, 218));
                        c.setForeground(new Color(114, 28, 36));
                    }
                }
                return c;
            }
        });

        // Role badge
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);

                if (!isSelected) {
                    String r = String.valueOf(value);
                    if ("ADMIN".equals(r)) {
                        c.setBackground(new Color(207, 226, 255));
                        c.setForeground(new Color(8, 66, 152));
                    } else if ("EMPLOYEE".equals(r)) {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(133, 100, 4));
                    } else {
                        c.setBackground(new Color(226, 227, 229));
                        c.setForeground(new Color(56, 61, 65));
                    }
                }
                return c;
            }
        });
    }

    // ===================== UI: BOTTOM =====================
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        totalLabel = new JLabel("Tổng số tài khoản: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(70, 70, 90));
        panel.add(totalLabel);

        return panel;
    }

    // ===================== DATA =====================
    private void loadAccounts() {
        try {
            List<Account> accounts = controller.getAllAccounts();
            renderTable(accounts);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách tài khoản: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAccounts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAccounts();
            return;
        }

        try {
            Account found = controller.findByUsername(keyword);
            if (found == null) {
                tableModel.setRowCount(0);
                updateTotal(0);
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy tài khoản với username: " + keyword,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            renderTable(List.of(found));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        try {
            List<Account> accounts = controller.getAllAccounts();

            String role = (String) roleFilter.getSelectedItem();
            String active = (String) activeFilter.getSelectedItem();

            List<Account> filtered = accounts.stream()
                    .filter(a -> {
                        if (role == null || "Tất cả Role".equals(role)) return true;
                        return a.getRole() != null && a.getRole().name().equals(role);
                    })
                    .filter(a -> {
                        if (active == null || "Tất cả trạng thái".equals(active)) return true;
                        if ("Đang hoạt động".equals(active)) return a.isActive();
                        return !a.isActive();
                    })
                    .collect(Collectors.toList());

            renderTable(filtered);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lọc: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderTable(List<Account> accounts) {
        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int stt = 1;
        for (Account a : accounts) {
            Object[] row = new Object[]{
                    stt++,
                    a.getId(),
                    safe(a.getUsername()),
                    safe(a.getEmail()),
                    safe(a.getPhone()),
                    a.getRole() != null ? a.getRole().name() : "",
                    a.isActive() ? "Hoạt động" : "Vô hiệu",
                    a.getLastLoginAt() != null ? a.getLastLoginAt().format(dtf) : ""
            };
            tableModel.addRow(row);
        }
        updateTotal(accounts.size());
    }

    private void updateTotal(int count) {
        totalLabel.setText("Tổng số tài khoản: " + count);
    }

    // ===================== CRUD DIALOGS =====================
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm tài khoản", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(520, 430);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = baseGbc();

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        JComboBox<String> activeBox = new JComboBox<>(new String[]{"Hoạt động", "Vô hiệu"});

        addFormField(form, gbc, 0, "Username: *", usernameField);
        addFormField(form, gbc, 1, "Password: *", passwordField);
        addFormField(form, gbc, 2, "Email:", emailField);
        addFormField(form, gbc, 3, "Phone:", phoneField);
        addFormField(form, gbc, 4, "Role: *", roleBox);
        addFormField(form, gbc, 5, "Trạng thái:", activeBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton save = createStyledButton("💾 Lưu", new Color(40, 167, 69));
        JButton cancel = createStyledButton("❌ Hủy", new Color(108, 117, 125));

        save.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập username!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập password!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Account a = new Account();
                a.setUsername(username);
                a.setPassword(password);
                String email = emailField.getText() != null ? emailField.getText().trim() : "";
                String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
                a.setEmail(email.isEmpty() ? null : email);
                a.setPhone(phone.isEmpty() ? null : phone);
                a.setRole((Role) roleBox.getSelectedItem());
                a.setActive(Objects.equals(activeBox.getSelectedItem(), "Hoạt động"));
                controller.createAccount(a);
                JOptionPane.showMessageDialog(dialog, "Tạo tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAccounts();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        btn.add(save);
        btn.add(cancel);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = parseInt(tableModel.getValueAt(row, 1));
        Account account = getByIdFallback(id);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản (ID=" + id + ")", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa tài khoản", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(540, 460);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = baseGbc();

        JTextField usernameField = new JTextField(20);
        usernameField.setText(account.getUsername());
        usernameField.setEditable(false);

        JPasswordField passwordField = new JPasswordField(20); // optional change
        JTextField emailField = new JTextField(20);
        emailField.setText(safe(account.getEmail()));
        JTextField phoneField = new JTextField(20);
        phoneField.setText(safe(account.getPhone()));

        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        roleBox.setSelectedItem(account.getRole());

        JComboBox<String> activeBox = new JComboBox<>(new String[]{"Hoạt động", "Vô hiệu"});
        activeBox.setSelectedItem(account.isActive() ? "Hoạt động" : "Vô hiệu");

        addFormField(form, gbc, 0, "Username:", usernameField);
        addFormField(form, gbc, 1, "Password (để trống nếu không đổi):", passwordField);
        addFormField(form, gbc, 2, "Email:", emailField);
        addFormField(form, gbc, 3, "Phone:", phoneField);
        addFormField(form, gbc, 4, "Role:", roleBox);
        addFormField(form, gbc, 5, "Trạng thái:", activeBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton save = createStyledButton("💾 Lưu", new Color(40, 167, 69));
        JButton cancel = createStyledButton("❌ Hủy", new Color(108, 117, 125));

        save.addActionListener(e -> {
            try {
                String newPass = new String(passwordField.getPassword()).trim();

                account.setEmail(emailField.getText().trim());
                account.setPhone(phoneField.getText().trim());
                account.setRole((Role) roleBox.getSelectedItem());
                account.setActive(Objects.equals(activeBox.getSelectedItem(), "Hoạt động"));

                if (!newPass.isEmpty()) {
                    account.setPassword(newPass);
                }

                controller.updateAccount(account);

                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAccounts();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        btn.add(save);
        btn.add(cancel);
        dialog.add(btn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteAccount() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = parseInt(tableModel.getValueAt(row, 1));
        String username = String.valueOf(tableModel.getValueAt(row, 2));
        String role = String.valueOf(tableModel.getValueAt(row, 5));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản:\n"
                        + "ID: " + id + "\nUsername: " + username + "\nRole: " + role
                        + "\n\nLưu ý: Thao tác này không thể hoàn tác!",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteAccountById(id);
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAccounts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===================== HELPERS =====================
    private JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        return gbc;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gbc);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private int parseInt(Object v) {
        try { return Integer.parseInt(String.valueOf(v)); }
        catch (Exception e) { return 0; }
    }

    private Account getByIdFallback(int id) {
        try {
            List<Account> all = controller.getAllAccounts();
            for (Account a : all) {
                if (a.getId() == id) return a;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Tài Khoản");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AccountManagementPanel());
            frame.setVisible(true);
        });
    }
}
