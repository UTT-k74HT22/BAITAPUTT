package org.example.view;

import org.example.controller.CustomerController;
import org.example.dto.response.CustomerProfileResponse;
import org.example.service.impl.CustomerServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CustomerManagementPanel extends BaseManagementPanel<CustomerProfileResponse> {

    private final CustomerController controller;

    private JTextField keywordField;
    private JComboBox<String> statusFilter;

    private static final String[] COLS = {
            "STT", "Profile ID", "Code", "Full Name", "Phone", "Status"
    };

    public CustomerManagementPanel() {
        this(new CustomerController(new CustomerServiceImpl()));
    }

    public CustomerManagementPanel(CustomerController controller) {
        this.controller = controller;
        // Base constructor sẽ gọi reload() luôn -> nên init field filter trước bằng cách gọi super() sau khi gán?
        // Cách đơn giản: để Base constructor chạy, rồi mình override buildFilters() dùng lazy init.
    }

    @Override protected String titleTotal() { return "Tổng số khách hàng"; }

    @Override protected String[] columns() { return COLS; }

    @Override protected int[] columnWidths() { return new int[]{60, 90, 120, 220, 140, 110}; }

    @Override protected List<CustomerProfileResponse> fetchData() {
//        return controller.getAllCustomers();
        return List.of();
    }

    @Override protected Object[] mapRow(CustomerProfileResponse c, int stt) {
        return new Object[]{
                stt,
                safe(c.getProfileId()),
                safe(c.getCode()),
                safe(c.getFullName()),
                safe(c.getPhone()),
                safe(c.getCustomerType())
        };
    }

    @Override
    protected JPanel buildFilters() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        keywordField = UiFactory.textField(20, 240);
        JButton searchBtn = UiFactory.button("🔍 Tìm", UiTheme.PRIMARY, e -> applyFilters());

        statusFilter = new JComboBox<>(new String[]{"ALL", "ACTIVE", "INACTIVE"});
        statusFilter.setPreferredSize(new Dimension(140, 34));
        statusFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);
        row.add(Box.createHorizontalStrut(12));
        row.add(new JLabel("Trạng thái:"));
        row.add(statusFilter);

        return row;
    }

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("➕ Thêm", new Color(40,167,69), e -> openCreate()));
        row.add(UiFactory.button("✏️ Sửa", new Color(255,193,7), e -> openEdit()));
        row.add(UiFactory.button("🗑️ Xóa", new Color(220,53,69), e -> deleteCustomer()));
        row.add(UiFactory.button("🔄 Refresh", new Color(108,117,125), e -> reload()));

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField == null ? "" : keywordField.getText().trim().toLowerCase();
        String st = statusFilter == null ? "ALL" : String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends javax.swing.table.DefaultTableModel, ? extends Integer> e) {
                // cols: 0 STT, 1 profileId, 2 code, 3 fullName, 4 phone, 5 status
                String code = val((Map.Entry<? extends DefaultTableModel, ? extends Integer>) e, 2);
                String fullName = val((Map.Entry<? extends DefaultTableModel, ? extends Integer>) e, 3);
                String phone = val((Map.Entry<? extends DefaultTableModel, ? extends Integer>) e, 4);
                String status = val((Map.Entry<? extends DefaultTableModel, ? extends Integer>) e, 5);

                if (!kw.isEmpty()) {
                    String hay = (code + " " + fullName + " " + phone).toLowerCase();
                    if (!hay.contains(kw)) return false;
                }

                if (!"ALL".equalsIgnoreCase(st)) {
                    if (!status.equalsIgnoreCase(st)) return false;
                }

                return true;
            }

            private String val(Map.Entry<? extends DefaultTableModel, ? extends Integer> e, int col) {
                Object v = e.getValue();
                return v == null ? "" : v.toString();
            }
        });
    }

    // -------- actions (demo) --------
    private void openCreate() { JOptionPane.showMessageDialog(this, "TODO create customer"); }
    private void openEdit()   { JOptionPane.showMessageDialog(this, "TODO edit customer"); }
    private void deleteCustomer() { JOptionPane.showMessageDialog(this, "TODO delete customer"); }
    private String safe(Object v) { return v == null ? "" : v.toString(); }
}
