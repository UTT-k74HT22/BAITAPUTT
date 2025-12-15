package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
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
    private void openCreateDialog() { JOptionPane.showMessageDialog(this,"TODO: Create"); }
    private void openEditDialog()   { JOptionPane.showMessageDialog(this,"TODO: Edit"); }
    private void deleteSelectedAccount() { JOptionPane.showMessageDialog(this,"TODO: Delete"); }

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
}
