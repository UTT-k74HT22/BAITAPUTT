package org.example.view;

import org.example.controller.ManufacturerController;
import org.example.entity.Manufacturer;
import org.example.service.impl.ManufacturerServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManufacturerManagementPanel extends JPanel {

    // ===================== DEPENDENCY =====================
    private final ManufacturerController controller;

    // ===================== UI =====================
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JLabel totalLabel;

    // ===================== CONSTANT =====================
    private static final String[] TABLE_COLUMNS = {
            "STT", "ID", "Code", "Name", "Country", "Phone", "Email"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    // ===================== CONSTRUCTOR =====================
    public ManufacturerManagementPanel() {
        this(new ManufacturerController(new ManufacturerServiceImpl()));
    }

    public ManufacturerManagementPanel(ManufacturerController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        initUI();
        reloadTable();
    }

    // ===================== INIT =====================
    private void initUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ===================== TOOLBAR =====================
    private JPanel buildToolbar() {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        searchField = createTextField(20);
        JButton searchBtn = createButton("🔍 Tìm", COLOR_PRIMARY, e -> search());
        JButton addBtn = createButton("➕ Thêm", new Color(40,167,69), e -> openCreateDialog());
        JButton editBtn = createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog());
        JButton delBtn = createButton("🗑️ Xóa", new Color(220,53,69), e -> delete());
        JButton refreshBtn = createButton("🔄 Refresh", new Color(108,117,125), e -> reloadTable());

        row.add(new JLabel("Keyword:"));
        row.add(searchField);
        row.add(searchBtn);
        row.add(Box.createHorizontalStrut(15));
        row.add(addBtn);
        row.add(editBtn);
        row.add(delBtn);
        row.add(refreshBtn);

        box.add(row);
        return box;
    }

    // ===================== TABLE =====================
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureHeader();
        configureRenderer();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void configureHeader() {
        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 40));
        h.setReorderingAllowed(false);

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
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, f, row, col);
                setForeground(COLOR_TEXT);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                setBackground(sel ? COLOR_SELECT : (row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(r);
        }
    }

    // ===================== DATA =====================
    private void reloadTable() {
        render(controller.getAll());
    }

    private void search() {
        String key = searchField.getText().trim().toLowerCase();
        List<Manufacturer> data = controller.getAll();

        if (!key.isEmpty()) {
            data = data.stream()
                    .filter(m ->
                            m.getCode().toLowerCase().contains(key) ||
                                    m.getName().toLowerCase().contains(key))
                    .collect(Collectors.toList());
        }
        render(data);
    }

    private void render(List<Manufacturer> list) {
        tableModel.setRowCount(0);
        int i = 1;
        for (Manufacturer m : list) {
            tableModel.addRow(new Object[]{
                    i++, m.getId(), m.getCode(), m.getName(),
                    m.getCountry(), m.getPhone(), m.getEmail()
            });
        }
        totalLabel.setText("Tổng số hãng: " + list.size());
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        totalLabel = new JLabel("Tổng số hãng: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(totalLabel);
        return p;
    }

    // ===================== CRUD =====================
    private void openCreateDialog() {
        Manufacturer m = new Manufacturer();
        showFormDialog("Thêm Manufacturer", m, true);
    }

    private void openEditDialog() {
        Manufacturer m = getSelected();
        if (m != null) showFormDialog("Cập nhật Manufacturer", m, false);
    }

    private void delete() {
        Manufacturer m = getSelected();
        if (m == null) return;

        int c = JOptionPane.showConfirmDialog(
                this, "Xóa hãng " + m.getName() + " ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION
        );

        if (c == JOptionPane.YES_OPTION) {
            controller.delete(m.getId());
            reloadTable();
        }
    }

    // ===================== FORM =====================
    private void showFormDialog(String title, Manufacturer m, boolean isCreate) {
        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                title, Dialog.ModalityType.APPLICATION_MODAL
        );
        d.setSize(500, 420);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(7,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField tfCode = new JTextField(m.getCode());
        JTextField tfName = new JTextField(m.getName());
        JTextField tfCountry = new JTextField(m.getCountry());
        JTextField tfAddress = new JTextField(m.getAddress());
        JTextField tfPhone = new JTextField(m.getPhone());
        JTextField tfEmail = new JTextField(m.getEmail());
        JTextField tfContact = new JTextField(m.getContactPerson());

        form.add(new JLabel("Code")); form.add(tfCode);
        form.add(new JLabel("Name")); form.add(tfName);
        form.add(new JLabel("Country")); form.add(tfCountry);
        form.add(new JLabel("Address")); form.add(tfAddress);
        form.add(new JLabel("Phone")); form.add(tfPhone);
        form.add(new JLabel("Email")); form.add(tfEmail);
        form.add(new JLabel("Contact Person")); form.add(tfContact);

        JButton save = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        foot.add(cancel);
        foot.add(save);

        cancel.addActionListener(e -> d.dispose());

        save.addActionListener(e -> {
            try {
                m.setCode(tfCode.getText().trim());
                m.setName(tfName.getText().trim());
                m.setCountry(tfCountry.getText().trim());
                m.setAddress(tfAddress.getText().trim());
                m.setPhone(tfPhone.getText().trim());
                m.setEmail(tfEmail.getText().trim());
                m.setContactPerson(tfContact.getText().trim());

                if (isCreate) controller.create(m);
                else controller.update(m);

                d.dispose();
                reloadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.add(form, BorderLayout.CENTER);
        d.add(foot, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ===================== UTIL =====================
    private Manufacturer getSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng");
            return null;
        }
        Long id = (Long) tableModel.getValueAt(r, 1);
        return controller.findById(id);
    }

    private JTextField createTextField(int c) {
        JTextField t = new JTextField(c);
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {}

            JFrame frame = new JFrame("TEST – Manufacturer CRUD");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            frame.setContentPane(new ManufacturerManagementPanel());
            frame.setVisible(true);
        });
    }
}
