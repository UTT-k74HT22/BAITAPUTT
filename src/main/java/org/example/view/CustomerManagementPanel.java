package org.example.view;

import org.example.controller.CustomerController;
import org.example.dto.request.UpdateCustomerProfileRequest;
import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;
import org.example.entity.enums.ProfileStatus;
import org.example.service.impl.CustomerServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CustomerManagementPanel extends BaseManagementPanel<CustomerProfileResponse> {

    private final CustomerController controller;

    private JTextField keywordField;
    private JComboBox<String> statusFilter;

    private static final String[] COLS = {
            "STT", "Profile ID", "Code", "Full Name", "Phone", "Email", "Address", "CustomerType", "Status"
    };

    public CustomerManagementPanel() {
        this(new CustomerController(new CustomerServiceImpl()));
    }

    public CustomerManagementPanel(CustomerController controller) {
        this.controller = controller;
        SwingUtilities.invokeLater(this::reload);
        // Base constructor sẽ gọi reload() luôn -> nên init field filter trước bằng cách gọi super() sau khi gán?
        // Cách đơn giản: để Base constructor chạy, rồi mình override buildFilters() dùng lazy init.
    }

    @Override protected String titleTotal() { return "Tổng số khách hàng"; }

    @Override protected String[] columns() { return COLS; }

    @Override protected int[] columnWidths() { return new int[]{60, 90, 120, 220, 140, 180, 220, 110, 110}; }

    @Override protected List<CustomerProfileResponse> fetchData() {
        return controller.getCustomers();
    }

    @Override protected Object[] mapRow(CustomerProfileResponse c, int stt) {
        return new Object[]{
                stt,
                safe(c.getProfileId()),
                safe(c.getCode()),
                safe(c.getFullName()),
                safe(c.getPhone()),
                safe(c.getEmail()),
                safe(c.getAddress()),
                safe(c.getCustomerType()),
                safe(c.getProfileStatus())
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

        row.add(UiFactory.button("Xem chi tiết", new Color(40,167,69), e -> openView()));
        row.add(UiFactory.button("Sửa", new Color(255,193,7), e -> openEdit()));
        row.add(UiFactory.button("Xóa", new Color(220,53,69), e -> deleteCustomer()));
        row.add(UiFactory.button("Refresh", new Color(108,117,125), e -> reload()));

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField == null ? "" : keywordField.getText().trim().toLowerCase();
        String st = statusFilter == null ? "ALL" : String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String code = safe(entry.getStringValue(2));
                String fullName = safe(entry.getStringValue(3));
                String phone = safe(entry.getStringValue(4));
                String email = safe(entry.getStringValue(5));
                String address = safe(entry.getStringValue(6));
                String customerType = safe(entry.getStringValue(7));
                String status = safe(entry.getStringValue(8));

                // filter theo keyword
                if (!kw.isEmpty()) {
                    String hay = (code + " " + fullName + " " + phone + " " + email + " " + address).toLowerCase();
                    if (!hay.contains(kw)) return false;
                }

                // filter theo trạng thái
                if (!"ALL".equalsIgnoreCase(st)) {
                    if (!status.equalsIgnoreCase(st)) return false;
                }

                return true;
            }
        });
    }

    private String getSelectedCustomerCode() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng");
            return null;
        }
        return getCellString(row, 2); // cột CODE
    }


    // -------- actions (demo) --------
    private void openView() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng");
            return;
        }

        CustomerProfileResponse c = controller.getCustomerByCode(
                getCellString(row, 2)
        );

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Chi tiết khách hàng",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(520, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 16, 16));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // ===== Read-only tất cả các field =====
        addRow(form, gc, 0, "Profile ID", roField(c.getProfileId()));
        addRow(form, gc, 1, "Code", roField(c.getCode()));
        addRow(form, gc, 2, "Full Name", roField(c.getFullName()));
        addRow(form, gc, 3, "Phone", roField(c.getPhone()));
        addRow(form, gc, 4, "Email", roField(c.getEmail()));
        addRow(form, gc, 5, "Address", roField(c.getAddress()));
        addRow(form, gc, 6, "Status", roField(c.getProfileStatus()));
        addRow(form, gc, 7, "Customer Type", roField(c.getCustomerType()));
        addRow(form, gc, 8, "Tax Code", roField(c.getTaxCode()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        actions.add(close);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private void openEdit() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng");
            return;
        }

        CustomerProfileResponse c = controller.getCustomerByCode(getCellString(row, 2));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cập nhật khách hàng",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16,16,16,16));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int r = 0;
        // Read-only
        addRow(form, gc, r++, "Profile ID", roField(c.getProfileId()));
        addRow(form, gc, r++, "Code", roField(c.getCode()));

        // Editable
        JTextField tfFullName = editField(c.getFullName());
        JTextField tfPhone = editField(c.getPhone());
        JTextField tfEmail = editField(c.getEmail());
        JTextField tfAddress = editField(c.getAddress());
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cbStatus.setSelectedItem(c.getProfileStatus().name());
        JComboBox<String> cbType = new JComboBox<>(new String[]{"RETAIL", "WHOLESALE"});
        cbType.setSelectedItem(c.getCustomerType().name());
        JTextField tfTaxCode = editField(c.getTaxCode());

        addRow(form, gc, r++, "Full Name", tfFullName);
        addRow(form, gc, r++, "Phone", tfPhone);
        addRow(form, gc, r++, "Email", tfEmail);
        addRow(form, gc, r++, "Address", tfAddress);
        addRow(form, gc, r++, "Status", cbStatus);
        addRow(form, gc, r++, "Customer Type *", cbType);
        addRow(form, gc, r++, "Tax Code", tfTaxCode);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        JButton cancel = new JButton("Cancel");
        JButton save   = new JButton("Save");

        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                UpdateCustomerProfileRequest request = new UpdateCustomerProfileRequest();
                request.setFullName(tfFullName.getText().trim());
                request.setPhone(tfPhone.getText().trim());
                request.setEmail(tfEmail.getText().trim());
                request.setAddress(tfAddress.getText().trim());
                request.setStatus(cbStatus.getSelectedItem().toString().equals("ACTIVE") ?
                        ProfileStatus.ACTIVE : ProfileStatus.INACTIVE);
                request.setCustomerType(CustomerType.valueOf(cbType.getSelectedItem().toString()));
                request.setTaxCode(tfTaxCode.getText().trim());

                controller.updateCustomer(c.getCode(), request);

                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        actions.add(cancel);
        actions.add(save);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteCustomer() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!");
            return;
        }

        String code = getCellString(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khách hàng " + code + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteCustomer(code); // soft delete
            JOptionPane.showMessageDialog(this, "Đã xóa khách hàng!");
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Xóa thất bại: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private JTextField roField(Object v) {
        JTextField tf = new JTextField(v == null ? "" : v.toString());
        tf.setEditable(false);
        tf.setBackground(new Color(245, 245, 245));
        tf.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return tf;
    }

    private JTextField editField(String v) {
        JTextField tf = new JTextField(v == null ? "" : v);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 232, 245)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridy = row;

        gc.gridx = 0;
        gc.weightx = 0;
        panel.add(new JLabel(label), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(field, gc);
    }
}
