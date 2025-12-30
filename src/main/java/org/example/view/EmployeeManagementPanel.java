package org.example.view;

import org.example.controller.EmployeeController;
import org.example.dto.request.Employee.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.Employee.UpdateProfileEmployeeRequest;
import org.example.dto.response.Employee.EmployeeProfileDetailResponse;
import org.example.dto.response.Employee.EmployeeProfileResponse;
import org.example.entity.enums.ProfileStatus;
import org.example.service.impl.EmployeeServiceImpl;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

        row.add(createButton("👁 Xem chi tiết", new Color(23,162,184), e -> openDetailDialog()));
        row.add(createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("🗑️ Xóa", new Color(220,53,69), e -> deleteEmployee()));
        row.add(createButton("🔄 Refresh", new Color(108,117,125), e -> loadEmployees()));
        row.add(Box.createHorizontalStrut(20));
        row.add(createButton("📄 Excel", new Color(111,66,193), e -> exportExcel()));
        row.add(createButton("📄 Excel Detail List", new Color(111,66,193), e -> controller.exportExcelDetailList(this)));
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
    private void openDetailDialog() {
        String code = getSelectedEmployeeCode();
        if (code == null) return;

        EmployeeProfileDetailResponse d =
                controller.getEmployeeDetailByCode(code);

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Employee Detail",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(600, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // ===== READ ONLY =====
        JTextField tfAccountId = roField(String.valueOf(d.getAccountId()));
        JTextField tfUsername  = roField(d.getAccountUsername());
        JTextField tfProfileId = roField(String.valueOf(d.getProfileId()));
        JTextField tfCode      = roField(d.getCode());

        // ===== EDITABLE =====
        JTextField tfFullName   = editField(d.getFullName());
        JTextField tfPhone      = editField(d.getPhone());
        JTextField tfEmail      = editField(d.getEmail());
        JTextField tfAddress    = editField(d.getAddress());
        JTextField tfPosition   = editField(d.getPosition());
        JTextField tfDepartment = editField(d.getDepartment());
        JTextField tfHiredDate  = editField(
                d.getHiredDate() == null ? "" : d.getHiredDate().toString() // yyyy-MM-dd
        );
        JTextField tfSalary     = editField(
                d.getSalary() == null ? "" : d.getSalary().toString()
        );
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cbStatus.setSelectedItem(d.getStatus().name());

        // disable edit by default
        for (JComponent c : new JComponent[]{
                tfFullName, tfPhone, tfEmail, tfAddress,
                tfPosition, tfDepartment, tfHiredDate, tfSalary, cbStatus}) {
            c.setEnabled(false);
        }

        int r = 0;
        addRow(form, gc, r++, "Account ID", tfAccountId);
        addRow(form, gc, r++, "Username", tfUsername);
        addRow(form, gc, r++, "Profile ID", tfProfileId);
        addRow(form, gc, r++, "Code", tfCode);

        addRow(form, gc, r++, "Full Name", tfFullName);
        addRow(form, gc, r++, "Phone", tfPhone);
        addRow(form, gc, r++, "Email", tfEmail);
        addRow(form, gc, r++, "Address", tfAddress);
        addRow(form, gc, r++, "Position", tfPosition);
        addRow(form, gc, r++, "Department", tfDepartment);
        addRow(form, gc, r++, "Hired Date (yyyy-MM-dd)", tfHiredDate);
        addRow(form, gc, r++, "Salary", tfSalary);
        addRow(form, gc, r++, "Status", cbStatus);

        // ===== ACTIONS =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actions.setBackground(Color.WHITE);

        JButton btnClose = createOutlineButton("Close");
        JButton btnEdit  = createButton("Update", new Color(255,193,7), null);
        JButton btnSave  = createButton("Save", new Color(40,167,69), null);
        JButton btnExcel = createButton("Excel", new Color(111,66,193), e -> {
            controller.exportEmployeeDetailExcel(d.getCode(), dialog);
        });
        btnSave.setVisible(false);

        btnClose.addActionListener(e -> dialog.dispose());

        btnEdit.addActionListener(e -> {
            for (JComponent c : new JComponent[]{
                    tfFullName, tfPhone, tfEmail, tfAddress,
                    tfPosition, tfDepartment, tfHiredDate, tfSalary, cbStatus
            }) {
                c.setEnabled(true);
            }
            btnEdit.setVisible(false);
            btnSave.setVisible(true);
        });

        btnSave.addActionListener(e -> {
            UpdateEmployeeProfileDetailRequest req = new UpdateEmployeeProfileDetailRequest();

            req.setFullName(tfFullName.getText().trim());
            req.setPhone(tfPhone.getText().trim());
            req.setEmail(tfEmail.getText().trim());
            req.setAddress(tfAddress.getText().trim());
            req.setPosition(tfPosition.getText().trim());
            req.setDepartment(tfDepartment.getText().trim());
            req.setStatus(ProfileStatus.valueOf(cbStatus.getSelectedItem().toString()));

            // hiredDate parse
            String hiredStr = tfHiredDate.getText().trim();
            if (!hiredStr.isEmpty()) {
                try {
                    LocalDate hd;
                    if (hiredStr.contains("/")) {
                        hd = LocalDate.parse(hiredStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } else {
                        hd = LocalDate.parse(hiredStr); // yyyy-MM-dd
                    }
                    req.setHiredDate(hd);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Hired Date không hợp lệ. Nhập: yyyy-MM-dd hoặc dd/MM/yyyy");
                    return;
                }
            } else {
                req.setHiredDate(null);
            }

            // salary parse
            String salaryStr = tfSalary.getText().trim();
            if (!salaryStr.isEmpty()) {
                try {
                    req.setSalary(new BigDecimal(salaryStr));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Salary không hợp lệ!");
                    return;
                }
            } else {
                req.setSalary(null);
            }

            controller.updateProfileDetail(tfCode.getText().trim(), req);

            JOptionPane.showMessageDialog(dialog, "Updated successfully!");
            dialog.dispose();
            loadEmployees();
        });

        actions.add(btnClose);
        actions.add(btnEdit);
        actions.add(btnSave);
        actions.add(btnExcel);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createOutlineButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(COLOR_PRIMARY);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 36));
        return b;
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
//        cbStatus.setEnabled(false);

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
            req.setStatus(ProfileStatus.valueOf(cbStatus.getSelectedItem().toString()));
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
        EmployeeProfileResponse e = getSelectedEmployee();
        if (e == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xoá nhân viên [" + e.getCode() + "] không?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        controller.delete(e.getCode(), ProfileStatus.INACTIVE);

        JOptionPane.showMessageDialog(this, "Đã xoá nhân viên thành công!");
        loadEmployees();
    }


    // ===================== EXPORT =====================
    private void exportExcel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn xuất danh sách nhân viên ra file Excel không?",
                "Xác nhận xuất Excel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        controller.exportExcel(this);
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
        System.out.println("Selected model row: " + modelRow);
        System.out.println("Employee code: " + tableModel.getValueAt(modelRow, 3));
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
}
