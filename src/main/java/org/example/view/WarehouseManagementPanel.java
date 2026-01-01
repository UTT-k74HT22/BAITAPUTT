package org.example.view;

import org.example.controller.WarehouseController;
import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WarehouseManagementPanel extends JPanel {

    private final WarehouseController controller;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalLabel;

    private static final String[] TABLE_COLUMNS = {
            "STT", "Code", "Name", "Address", "Phone", "Manager", "Status"
    };

    // Theme màu
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    public WarehouseManagementPanel() {
        this.controller = new WarehouseController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        reloadTable();
    }

    private void initUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        searchField = createTextField(15);
        JButton searchBtn = createButton("Tìm", COLOR_PRIMARY, e -> search());
        JButton addBtn    = createButton("Thêm", new Color(40,167,69), e -> openForm(null, true));
        JButton editBtn   = createButton("Sửa", new Color(255,193,7), e -> openEdit());
        JButton delBtn    = createButton("Xóa", new Color(220,53,69), e -> delete());
        JButton reloadBtn = createButton("Reload", new Color(108,117,125), e -> reloadTable());

        panel.add(new JLabel("Keyword:"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(delBtn);
        panel.add(reloadBtn);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));

        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        configureHeader();
        configureRenderer();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void configureHeader() {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0,40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        r.setBackground(COLOR_PRIMARY);
        r.setForeground(Color.WHITE);
        r.setFont(new Font("Segoe UI", Font.BOLD, 13));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(r);
        }
    }

    private void configureRenderer() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setForeground(COLOR_TEXT);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                setBackground(isSelected ? COLOR_SELECT : (row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(r);
        }
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        totalLabel = new JLabel("Tổng số kho: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(totalLabel);
        return panel;
    }

    // ================== DATA ==================
    private void reloadTable() {
        render(controller.getAllWarehouses());
    }

    private void search() {
        String key = searchField.getText().trim().toLowerCase();
        List<WarehouseResponse> data = controller.getAllWarehouses();

        if(!key.isEmpty()) {
            data = data.stream()
                    .filter(w -> w.getCode().toLowerCase().contains(key)
                            || w.getName().toLowerCase().contains(key))
                    .collect(Collectors.toList());
        }
        render(data);
    }

    private void render(List<WarehouseResponse> list) {
        tableModel.setRowCount(0);
        int i = 1;
        for (WarehouseResponse w : list) {
            tableModel.addRow(new Object[]{
                    i++,
                    w.getCode(),
                    w.getName(),
                    w.getAddress(),
                    w.getPhone(),
                    w.getManagerName(),
                    w.isActive() ? "ACTIVE" : "INACTIVE"
            });
        }
        totalLabel.setText("Tổng số kho: " + list.size());
    }


    // ================== CRUD ==================
    private void openForm(WarehouseResponse w, boolean isCreate) {
        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                isCreate ? "Thêm kho" : "Sửa/Xem kho",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        d.setSize(450, 380);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(5,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField tfCode = new JTextField(w != null ? w.getCode() : "");
        JTextField tfName = new JTextField(w != null ? w.getName() : "");
        JTextField tfAddress = new JTextField(w != null ? w.getAddress() : "");
        JTextField tfPhone = new JTextField(w != null ? w.getPhone() : "");
        JTextField tfManager = new JTextField(w != null ? w.getManagerName() : "");

        if(w != null && !isCreate) tfCode.setEnabled(false);

        form.add(new JLabel("Code")); form.add(tfCode);
        form.add(new JLabel("Name")); form.add(tfName);
        form.add(new JLabel("Address")); form.add(tfAddress);
        form.add(new JLabel("Phone")); form.add(tfPhone);
        form.add(new JLabel("Manager")); form.add(tfManager);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave   = new JButton(isCreate ? "Thêm" : "Lưu");
        actions.add(btnCancel);
        actions.add(btnSave);

        btnCancel.addActionListener(e -> d.dispose());
        btnSave.addActionListener(e -> {
            CreateWarehouseRequest req = new CreateWarehouseRequest();
            req.setCode(tfCode.getText().trim());
            req.setName(tfName.getText().trim());
            req.setAddress(tfAddress.getText().trim());
            req.setPhone(tfPhone.getText().trim());
            req.setManagerName(tfManager.getText().trim());

            if(isCreate) controller.createWarehouse(req);
            else controller.updateWarehouse(tfCode.getText().trim(), req);

            d.dispose();
            reloadTable();
        });

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void openEdit() {
        WarehouseResponse w = getSelected();
        if(w != null) openForm(w, false);
    }

    private void delete() {
        WarehouseResponse w = getSelected();
        if (w == null) return;

        if (!w.isActive()) {
            JOptionPane.showMessageDialog(this,
                    "Kho này đã bị vô hiệu hóa rồi");
            return;
        }

        int c = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn vô hiệu hóa kho [" + w.getName() + "] không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (c == JOptionPane.YES_OPTION) {
            controller.deleteWarehouse(w.getCode());
            reloadTable();
        }
    }


    private WarehouseResponse getSelected() {
        int r = table.getSelectedRow();
        if(r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng");
            return null;
        }
        String code = tableModel.getValueAt(r, 1).toString();
        return controller.getWarehouseByCode(code);
    }

    private JTextField createTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(6,10,6,10)
        ));
        return t;
    }

    private JButton createButton(String text, Color bg, java.awt.event.ActionListener e) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.addActionListener(e);
        return b;
    }
}
