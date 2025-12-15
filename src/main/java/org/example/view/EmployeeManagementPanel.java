package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Employee Management Screen
 * Author: HMS
 */
public class EmployeeManagementPanel extends JPanel {

    // ===================== UI =====================
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField keywordField;
    private JComboBox<String> departmentFilter;
    private JComboBox<String> statusFilter;

    private JLabel totalLabel;

    // ===================== TABLE =====================
    private static final String[] COLUMNS = {
            "STT", "Code", "Full Name", "Position", "Department",
            "Phone", "Email", "Status", "Hired Date"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        initUI();
        loadEmployees(); // demo / stub
    }

    // ===================== INIT =====================
    private void initUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ===================== TOOLBAR =====================
    private JPanel buildToolbar() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);

        root.add(buildFilterRow());
        root.add(buildActionRow());

        return root;
    }

    private JPanel buildFilterRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        keywordField = createTextField(20);
        JButton searchBtn = createButton("🔍 Tìm", COLOR_PRIMARY, e -> searchEmployees());

        departmentFilter = new JComboBox<>(new String[]{"ALL", "HR", "IT", "SALES", "FINANCE"});
        statusFilter = new JComboBox<>(new String[]{"ALL", "ACTIVE", "INACTIVE"});

        departmentFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Phòng ban:"));
        row.add(departmentFilter);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Trạng thái:"));
        row.add(statusFilter);

        return row;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        row.add(createButton("➕ Thêm", new Color(40,167,69), e -> openCreateDialog()));
        row.add(createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("🗑️ Xóa", new Color(220,53,69), e -> deleteEmployee()));
        row.add(createButton("🔄 Refresh", new Color(108,117,125), e -> loadEmployees()));
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

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(38);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureHeader();
        configureAppearance();
        configureColumns();
        configureRenderers();

        JScrollPane scroll = new JScrollPane(employeeTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void configureHeader() {
        JTableHeader header = employeeTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        r.setBackground(COLOR_PRIMARY);
        r.setForeground(Color.WHITE);
        r.setFont(new Font("Segoe UI", Font.BOLD, 13));
        r.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setHeaderRenderer(r);
        }
    }

    private void configureAppearance() {
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setSelectionBackground(COLOR_SELECT);
        employeeTable.setSelectionForeground(COLOR_TEXT);
        employeeTable.setFillsViewportHeight(true);
    }

    private void configureColumns() {
        int[] widths = {50, 90, 180, 120, 120, 120, 180, 100, 120};
        for (int i = 0; i < widths.length; i++) {
            employeeTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void configureRenderers() {
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(COLOR_TEXT);
                setBackground(sel ? COLOR_SELECT : (r % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT));
                setHorizontalAlignment(c == 0 || c == 7 ? CENTER : LEFT);
                return this;
            }
        };

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        // Status badge
        employeeTable.getColumnModel().getColumn(7).setCellRenderer(createStatusBadge());
    }

    private DefaultTableCellRenderer createStatusBadge() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (sel) {
                    setBackground(COLOR_SELECT);
                    setForeground(COLOR_TEXT);
                    return this;
                }

                if ("ACTIVE".equals(v)) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
                return this;
            }
        };
    }

    // ===================== DATA =====================
    private void loadEmployees() {
        // TODO: replace by controller.getAllEmployees()
        tableModel.setRowCount(0);

        // Demo row
        tableModel.addRow(new Object[]{
                1, "EMP001", "Nguyễn Văn A", "Developer", "IT",
                "0909123456", "a@gmail.com", "ACTIVE", "01/03/2023"
        });

        totalLabel.setText("Tổng số nhân viên: 1");
    }

    private void searchEmployees() {
        JOptionPane.showMessageDialog(this, "TODO: search employees");
    }

    private void applyFilters() {
        JOptionPane.showMessageDialog(this, "TODO: apply filters");
    }

    // ===================== CRUD =====================
    private void openCreateDialog() {
        JOptionPane.showMessageDialog(this, "TODO: create employee");
    }

    private void openEditDialog() {
        JOptionPane.showMessageDialog(this, "TODO: edit employee");
    }

    private void deleteEmployee() {
        JOptionPane.showMessageDialog(this, "TODO: delete employee");
    }

    // ===================== EXPORT =====================
    private void exportExcel() {
        JOptionPane.showMessageDialog(this, "TODO: export excel");
    }

    private void exportPdf() {
        JOptionPane.showMessageDialog(this, "TODO: export pdf");
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        totalLabel = new JLabel("Tổng số nhân viên: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(totalLabel);

        return panel;
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
}
