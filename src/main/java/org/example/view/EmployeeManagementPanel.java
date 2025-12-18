package org.example.view;

import org.example.controller.EmployeeController;
import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.service.impl.EmployeeServiceImpl;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {

    private final EmployeeController controller;

    // ===================== UI =====================
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField keywordField;
    private JComboBox<String> departmentFilter;
    private JComboBox<String> statusFilter;

    private JLabel totalLabel;

    // ===================== TABLE =====================
    private static final String[] COLUMNS = {
            "STT",
            "Account Username",
            "Profile ID",
            "Code",
            "Full Name",
            "Phone",
            "Position",
            "Status"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    public EmployeeManagementPanel() {
        this(new EmployeeController(new EmployeeServiceImpl()));
    }

    public EmployeeManagementPanel(EmployeeController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initUI();
        loadEmployees();
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
        root.add(Box.createVerticalStrut(8));
        root.add(buildActionRow());

        return root;
    }

    private JPanel buildFilterRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 10);
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 1;

        keywordField = createTextField(22);
        JButton searchBtn = createButton("🔍 Tìm", COLOR_PRIMARY, e -> applyFilters());

        departmentFilter = new JComboBox<>(new String[]{"ALL"});
        departmentFilter.setPreferredSize(new Dimension(160, 34));
        departmentFilter.addActionListener(e -> applyFilters());

        statusFilter = new JComboBox<>(new String[]{"ALL", "ACTIVE", "INACTIVE"});
        statusFilter.setPreferredSize(new Dimension(140, 34));
        statusFilter.addActionListener(e -> applyFilters());

        // Label: Từ khóa
        gc.gridx = 0; gc.weightx = 0;
        row.add(createLabel("Từ khóa:"), gc);

        // Field
        gc.gridx = 1; gc.weightx = 1;
        row.add(keywordField, gc);

        // Search button
        gc.gridx = 2; gc.weightx = 0;
        row.add(searchBtn, gc);

        // Label: Phòng ban
        gc.gridx = 3;
        row.add(createLabel("Phòng ban:"), gc);

        // Department
        gc.gridx = 4;
        row.add(departmentFilter, gc);

        // Label: Trạng thái
        gc.gridx = 5;
        row.add(createLabel("Trạng thái:"), gc);

        // Status
        gc.gridx = 6;
        row.add(statusFilter, gc);

        return row;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(createButton("➕ Thêm", new Color(40,167,69), e -> openCreateDialog()));
        row.add(createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("🗑️ Xóa", new Color(220,53,69), e -> deleteEmployee()));
        row.add(createButton("🔄 Refresh", new Color(108,117,125), e -> loadEmployees()));
        row.add(Box.createHorizontalStrut(20));
        row.add(createButton("📄 PDF", new Color(111,66,193), e -> exportPdf()));

        return row;
    }

    // ===================== TABLE =====================
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class; // STT sort đúng
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(38);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

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
        r.setBorder(new EmptyBorder(8, 10, 8, 10));

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
        int[] widths = {60, 160, 90, 120, 200, 130, 140, 110};
        for (int i = 0; i < widths.length; i++) {
            employeeTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void configureRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setForeground(COLOR_TEXT);

                if (isSelected) {
                    setBackground(COLOR_SELECT);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT);
                }
                return this;
            }
        };

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        employeeTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(createStatusBadge());
    }

    private DefaultTableCellRenderer createStatusBadge() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setHorizontalAlignment(CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setOpaque(true);

                if (sel) {
                    setBackground(COLOR_SELECT);
                    setForeground(COLOR_TEXT);
                    return this;
                }

                String status = v == null ? "" : v.toString();
                if ("ACTIVE".equalsIgnoreCase(status)) {
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
        tableModel.setRowCount(0);

        List<EmployeeProfileResponse> employees = controller.getAllEmployees();
        departmentFilter.removeAllItems();
        departmentFilter.addItem("ALL");

        int stt = 1;
        for (EmployeeProfileResponse e : employees) {
            tableModel.addRow(new Object[]{
                    stt++,
                    safe(e.getAccountUsername()),
                    safe(e.getProfileId()),
                    safe(e.getCode()),
                    safe(e.getFullName()),
                    safe(e.getPhone()),
                    safe(e.getPosition()),
                    safe(e.getStatus())
            });
        }

        totalLabel.setText("Tổng số nhân viên: " + employees.size());
        applyFilters();
    }

    private void applyFilters() {
        String kw = keywordField.getText() == null ? "" : keywordField.getText().trim().toLowerCase();
        String dep = String.valueOf(departmentFilter.getSelectedItem());
        String st  = String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                // columns: 0 STT, 1 username, 2 profileId, 3 code, 4 fullName, 5 phone, 6 position, 7 status
                String username = val(entry, 1);
                String code     = val(entry, 3);
                String fullName = val(entry, 4);
                String phone    = val(entry, 5);
                String position = val(entry, 6);
                String status   = val(entry, 7);

                // keyword
                if (!kw.isEmpty()) {
                    String hay = (username + " " + code + " " + fullName + " " + phone + " " + position).toLowerCase();
                    if (!hay.contains(kw)) return false;
                }

                // status
                if (!"ALL".equalsIgnoreCase(st)) {
                    if (!status.equalsIgnoreCase(st)) return false;
                }

                // department: hiện chưa có cột phòng ban trong table -> nếu bạn thêm cột, filter ở đây
                // if (!"ALL".equalsIgnoreCase(dep)) { ... }

                return true;
            }

            private String val(Entry<? extends DefaultTableModel, ? extends Integer> e, int col) {
                Object v = e.getValue(col);
                return v == null ? "" : v.toString();
            }
        });
    }

    // ===================== CRUD =====================
    private void openCreateDialog() {
        JOptionPane.showMessageDialog(this, "TODO: create employee");
    }

    private void openEditDialog() {
        EmployeeProfileResponse e = getSelectedEmployee();
        if (e == null) return;

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Employee",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // ===== Read-only fields =====
        JTextField tfAccountId = roField(String.valueOf(e.getAccountId()));
        JTextField tfUsername  = roField(e.getAccountUsername());
        JTextField tfProfileId = roField(String.valueOf(e.getProfileId()));
        JTextField tfCode      = roField(e.getCode());

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cbStatus.setSelectedItem(e.getStatus() == null ? "INACTIVE" : e.getStatus().toString());
        cbStatus.setEnabled(false);

        // ===== Editable fields =====
        JTextField tfFullName = editField(e.getFullName());
        JTextField tfPhone    = editField(e.getPhone());
        JTextField tfPosition = editField(e.getPosition());

        int r = 0;
        addRow(form, gc, r++, "Account ID", tfAccountId);
        addRow(form, gc, r++, "Username", tfUsername);
        addRow(form, gc, r++, "Profile ID", tfProfileId);
        addRow(form, gc, r++, "Code", tfCode);
        addRow(form, gc, r++, "Status", cbStatus);

        addRow(form, gc, r++, "Full Name *", tfFullName);
        addRow(form, gc, r++, "Phone *", tfPhone);
        addRow(form, gc, r++, "Position *", tfPosition);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn   = new JButton("Save");

        cancelBtn.addActionListener(ev -> dialog.dispose());

        saveBtn.addActionListener(ev -> {
            UpdateProfileEmployeeRequest req = new UpdateProfileEmployeeRequest();
            req.setFullName(tfFullName.getText().trim());
            req.setPhone(tfPhone.getText().trim());
            req.setPosition(tfPosition.getText().trim());

            String code = tfCode.getText().trim();

            controller.updateProfile(code, req);

            JOptionPane.showMessageDialog(dialog, "Updated successfully!");
            dialog.dispose();
            loadEmployees();
        });


        actions.add(cancelBtn);
        actions.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteEmployee() {
        JOptionPane.showMessageDialog(this, "TODO: delete employee");
    }

    // ===================== EXPORT =====================
    private void exportPdf() {
        JOptionPane.showMessageDialog(this, "TODO: export pdf");
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        totalLabel = new JLabel("Tổng số nhân viên: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalLabel.setForeground(COLOR_TEXT);

        panel.add(totalLabel);
        return panel;
    }

    // ===================== UTIL =====================
    private JLabel createLabel(String text) {
        JLabel lb = new JLabel(text);
        lb.setForeground(COLOR_TEXT);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lb;
    }

    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setPreferredSize(new Dimension(220, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(6, 10, 6, 10)
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
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.addActionListener(action);
        return b;
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private String getSelectedEmployeeCode() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select row");
            return null;
        }
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        return String.valueOf(tableModel.getValueAt(modelRow, 3)); // Code column = 3
    }

    private EmployeeProfileResponse getSelectedEmployee() {
        String code = getSelectedEmployeeCode();
        if (code == null) return null;
        return controller.findByCode(code);
    }

    private JTextField roField(String value) {
        JTextField tf = new JTextField(value == null ? "" : value);
        tf.setEnabled(false);
        tf.setDisabledTextColor(COLOR_TEXT);
        tf.setBackground(new Color(245, 246, 250));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JTextField editField(String value) {
        JTextField tf = new JTextField(value == null ? "" : value);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridy = row;

        gc.gridx = 0; gc.weightx = 0;
        JLabel lb = new JLabel(label);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lb.setForeground(COLOR_TEXT);
        panel.add(lb, gc);

        gc.gridx = 1; gc.weightx = 1;
        panel.add(field, gc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employee Management");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new EmployeeManagementPanel());
            frame.setVisible(true);
        });
    }
}
