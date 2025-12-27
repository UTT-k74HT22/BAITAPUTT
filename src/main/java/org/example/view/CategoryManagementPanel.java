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
import java.io.File;
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
        row.add(createButton("📊 PDF", new Color(23,162,184), e -> exportPdf()));

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
    // ===================== CREATE CATEGORY =====================
    private void openCreateDialog() {
        // ===== Form fields =====
        JTextField txtCode = new JTextField(20);
        JTextField txtName = new JTextField(20);
        JTextField txtDescription = new JTextField(20);
        JCheckBox chkActive = new JCheckBox("Active", true);

        // ===== Parent category combobox =====
        List<CategoryResponse> allCategories = controller.getAllCategories();
        JComboBox<CategoryResponse> parentCombo = new JComboBox<>();
        parentCombo.addItem(null); // Không có parent (root)
        for (CategoryResponse c : allCategories) parentCombo.addItem(c);
        parentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponse cat) setText(cat.getName() + " [" + cat.getCode() + "]");
                else setText("None");
                return this;
            }
        });

        // ===== Form panel =====
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Code:"));      form.add(txtCode);
        form.add(new JLabel("Name:"));      form.add(txtName);
        form.add(new JLabel("Description:")); form.add(txtDescription);
        form.add(new JLabel("Parent:"));    form.add(parentCombo);
        form.add(new JLabel(""));            form.add(chkActive);

        // ===== Show dialog =====
        int result = JOptionPane.showConfirmDialog(this, form, "Create Category",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        // ===== Validate =====
        if (txtCode.getText().isBlank() || txtName.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Code và Name không được để trống",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ===== Build entity =====
        Category c = new Category();
        c.setCode(txtCode.getText().trim());
        c.setName(txtName.getText().trim());
        c.setDescription(txtDescription.getText().trim());
        c.setActive(chkActive.isSelected());

        // ===== Set parent and display_order =====
        CategoryResponse parent = (CategoryResponse) parentCombo.getSelectedItem();
        if (parent != null) {
            c.setParentId(parent.getId());
            int maxOrder = controller.getAllCategories().stream()
                    .filter(cat -> parent.getId().equals(cat.getParentId()))
                    .mapToInt(CategoryResponse::getDisplayOrder)
                    .max().orElse(0);
            c.setDisplayOrder(maxOrder + 1);
        } else {
            c.setParentId(null);
            int maxOrder = controller.getAllCategories().stream()
                    .filter(cat -> cat.getParentId() == null)
                    .mapToInt(CategoryResponse::getDisplayOrder)
                    .max().orElse(0);
            c.setDisplayOrder(maxOrder + 1);
        }

        // ===== Call controller =====
        try {
            controller.createCategory(c);
            loadCategories();
            JOptionPane.showMessageDialog(this, "Tạo category thành công",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== EDIT CATEGORY =====================
    private void openEditDialog() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) return;

        String code = tableModel.getValueAt(row, 1).toString();
        Category c = controller.getCategoryByCode(code);

        // ===== Form fields =====
        JTextField txtName = new JTextField(c.getName(), 20);
        JTextField txtDescription = new JTextField(c.getDescription(), 20);
        JCheckBox chkActive = new JCheckBox("Active", c.isActive());

        // ===== Parent combobox =====
        List<CategoryResponse> allCategories = controller.getAllCategories();
        JComboBox<CategoryResponse> parentCombo = new JComboBox<>();
        parentCombo.addItem(null); // root
        for (CategoryResponse cat : allCategories) {
            if (!cat.getId().equals(c.getId())) parentCombo.addItem(cat); // không cho chọn chính nó
        }
        parentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponse cat) setText(cat.getName() + " [" + cat.getCode() + "]");
                else setText("None");
                return this;
            }
        });

        // ===== Select current parent =====
        if (c.getParentId() != null) {
            allCategories.stream()
                    .filter(cat -> cat.getId().equals(c.getParentId()))
                    .findFirst()
                    .ifPresent(parentCombo::setSelectedItem);
        } else {
            parentCombo.setSelectedItem(null);
        }

        // ===== Form panel =====
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Description:")); form.add(txtDescription);
        form.add(new JLabel("Parent:")); form.add(parentCombo);
        form.add(new JLabel("")); form.add(chkActive);

        // ===== Show dialog =====
        int result = JOptionPane.showConfirmDialog(this, form, "Edit Category",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        // ===== Update entity =====
        c.setName(txtName.getText().trim());
        c.setDescription(txtDescription.getText().trim());
        c.setActive(chkActive.isSelected());

        // ===== Update parent and display_order =====
        CategoryResponse newParent = (CategoryResponse) parentCombo.getSelectedItem();
        if ((newParent == null && c.getParentId() != null) ||
                (newParent != null && !newParent.getId().equals(c.getParentId()))) {
            // parent thay đổi
            if (newParent != null) {
                c.setParentId(newParent.getId());
                int maxOrder = controller.getAllCategories().stream()
                        .filter(cat -> newParent.getId().equals(cat.getParentId()) && !cat.getCode().equals(c.getCode()))
                        .mapToInt(CategoryResponse::getDisplayOrder)
                        .max().orElse(0);
                c.setDisplayOrder(maxOrder + 1);
            } else {
                c.setParentId(null);
                int maxOrder = controller.getAllCategories().stream()
                        .filter(cat -> cat.getParentId() == null && !cat.getCode().equals(c.getCode()))
                        .mapToInt(CategoryResponse::getDisplayOrder)
                        .max().orElse(0);
                c.setDisplayOrder(maxOrder + 1);
            }
        }

        // ===== Call controller =====
        controller.updateCategory(c);
        loadCategories();
        JOptionPane.showMessageDialog(this, "Cập nhật category thành công", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }




    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) return;

        String code = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xoá category [" + code + "] không?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        controller.delete(code); // soft delete
        JOptionPane.showMessageDialog(this, "Đã xoá category thành công!");
        loadCategories();
    }





    // ===================== EXPORT Exel====================
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
    // ===================== EXPORT PDF=====================
    private void exportPdf() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn xuất danh sách category ra file PDF không?",
                "Xác nhận xuất PDF",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file PDF");
        chooser.setSelectedFile(new File("categories.pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        controller.exportPdf(file.getAbsolutePath());
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
