package org.example.view;

import org.example.controller.CategoryController;
import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import org.example.service.impl.CategoryServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Category Management Screen
 * Author: Your Name
 */
public class CategoryManagementPanel extends JPanel {

    private final CategoryController controller;

    // ===================== UI =====================
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    private JTextField keywordField;
    private JLabel totalLabel;

    // ===================== TABLE COLUMNS =====================
    private static final String[] COLUMNS = {
            "STT", "Code", "Name", "Description", "Parent ID", "Parent Name", "Active", "Display Order"
    };

    // ===================== THEME =====================
    private static final Color COLOR_PRIMARY = new Color(113, 99, 248);
    private static final Color COLOR_BORDER  = new Color(230, 232, 245);
    private static final Color COLOR_ROW_ALT = new Color(248, 249, 255);
    private static final Color COLOR_SELECT  = new Color(232, 236, 255);
    private static final Color COLOR_TEXT    = new Color(45, 45, 70);

    public CategoryManagementPanel() {
        this(new CategoryController(new CategoryServiceImpl()));
    }

    public CategoryManagementPanel(CategoryController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        initUI();
        loadCategories(); // load dữ liệu
    }

    // ===================== INIT UI =====================
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
        JButton searchBtn = createButton("🔍 Tìm", COLOR_PRIMARY, e -> searchCategories());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);

        return row;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row.setOpaque(false);

        row.add(createButton("➕ Thêm", new Color(40,167,69), e -> openCreateDialog()));
        row.add(createButton("✏️ Sửa", new Color(255,193,7), e -> openEditDialog()));
        row.add(createButton("🗑️ Xóa", new Color(220,53,69), e -> deleteCategory()));
        row.add(createButton("🔄 Refresh", new Color(108,117,125), e -> loadCategories()));
        row.add(Box.createHorizontalStrut(20));
        row.add(createButton("📊 Excel", new Color(23,162,184), e -> exportExcel()));

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

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(38);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureHeader();
        configureAppearance();
        configureColumns();
        configureRenderers();

        JScrollPane scroll = new JScrollPane(categoryTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void configureHeader() {
        JTableHeader header = categoryTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        r.setBackground(COLOR_PRIMARY);
        r.setForeground(Color.WHITE);
        r.setFont(new Font("Segoe UI", Font.BOLD, 13));
        r.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < categoryTable.getColumnCount(); i++) {
            categoryTable.getColumnModel().getColumn(i).setHeaderRenderer(r);
        }
    }

    private void configureAppearance() {
        categoryTable.setShowGrid(false);
        categoryTable.setIntercellSpacing(new Dimension(0, 0));
        categoryTable.setSelectionBackground(COLOR_SELECT);
        categoryTable.setSelectionForeground(COLOR_TEXT);
        categoryTable.setFillsViewportHeight(true);
    }

    private void configureColumns() {
        int[] widths = {50, 100, 180, 200, 80, 120, 80, 100};
        for (int i = 0; i < widths.length; i++) {
            categoryTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
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
                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(COLOR_TEXT);
                setBackground(
                        isSelected
                                ? COLOR_SELECT
                                : (row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT)
                );
                return this;
            }
        };

        for (int i = 0; i < categoryTable.getColumnCount(); i++) {
            categoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ===================== DATA =====================
    private void loadCategories() {
        tableModel.setRowCount(0);

        List<CategoryResponse> categories = controller.getAllCategories();
        int stt = 1;
        for (CategoryResponse c : categories) {
            tableModel.addRow(new Object[]{
                    stt++,
                    c.getCode(),
                    c.getName(),
                    c.getDescription(),
                    c.getParentId(),
                    c.getParentName(),
                    c.isActive() ? "ACTIVE" : "INACTIVE",
                    c.getDisplayOrder()
            });
        }

        totalLabel.setText("Tổng số category: " + categories.size());
    }

    private void searchCategories() {
        String keyword = keywordField.getText().trim();
        List<CategoryResponse> filtered;

        if (keyword.isEmpty()) {
            filtered = controller.getAllCategories();
        } else {
            filtered = controller.searchCategories(keyword); // bạn cần triển khai search trong controller/service
        }

        tableModel.setRowCount(0);
        int stt = 1;
        for (CategoryResponse c : filtered) {
            tableModel.addRow(new Object[]{
                    stt++,
                    c.getCode(),
                    c.getName(),
                    c.getDescription(),
                    c.getParentId(),
                    c.getParentName(),
                    c.isActive() ? "ACTIVE" : "INACTIVE",
                    c.getDisplayOrder()
            });
        }
        totalLabel.setText("Tổng số category: " + filtered.size());
    }


    // ===================== CRUD =====================
    private void openCreateDialog() {

        // ===== Form panel =====
        JTextField txtCode = new JTextField(20);
        JTextField txtName = new JTextField(20);
        JTextField txtDescription = new JTextField(20);

        JCheckBox chkActive = new JCheckBox("Active", true);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Code:"));
        form.add(txtCode);

        form.add(new JLabel("Name:"));
        form.add(txtName);

        form.add(new JLabel("Description:"));
        form.add(txtDescription);

        form.add(new JLabel(""));
        form.add(chkActive);

        // ===== Show dialog =====
        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Create Category",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        // ===== Validate =====
        if (txtCode.getText().isBlank() || txtName.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Code và Name không được để trống",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // ===== Build entity =====
        Category c = new Category();
        c.setCode(txtCode.getText().trim());
        c.setName(txtName.getText().trim());
        c.setDescription(txtDescription.getText().trim());
        c.setActive(chkActive.isSelected());
        c.setDisplayOrder(1);

        // ===== Call controller =====
        try {
            controller.createCategory(c);
            loadCategories();

            JOptionPane.showMessageDialog(
                    this,
                    "Tạo category thành công",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openEditDialog() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) return;

        String code = tableModel.getValueAt(row, 1).toString();
        Category c = controller.getCategoryByCode(code);

        String newName = JOptionPane.showInputDialog(this, "Tên mới:", c.getName());
        if (newName == null || newName.isBlank()) return;

        c.setName(newName.trim());
        controller.updateCategory(c);
        loadCategories();
    }


    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) return;

        String code = tableModel.getValueAt(row, 1).toString();
        Category c = controller.getCategoryByCode(code);

        if (JOptionPane.showConfirmDialog(
                this, "Xóa " + code + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {
            controller.deleteCategory(c.getId());
            loadCategories();
        }
    }


    // ===================== EXPORT =====================
    private void exportExcel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn xuất danh sách category ra file Excel không?",
                "Xác nhận xuất Excel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        controller.exportExcel(null);
    }

    // ===================== FOOTER =====================
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        totalLabel = new JLabel("Tổng số category: 0");
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
