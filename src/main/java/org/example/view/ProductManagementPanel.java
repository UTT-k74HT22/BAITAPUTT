package org.example.view;

import org.example.controller.ProductController;
import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.ProductResponse;
import org.example.entity.enums.ProductStatus;
import org.example.service.impl.ProductServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Màn hình quản lý sản phẩm.
 * Popup thêm/sửa được viết trực tiếp trong file (không tạo dialog riêng).
 */
public class ProductManagementPanel extends BaseManagementPanel<ProductResponse> {

    private final ProductController controller;
    private final JFrame parentFrame;

    private JTextField keywordField;
    private JComboBox<String> statusFilter;

    private static final String[] COLS = {
            "STT", "Mã SP", "Tên sản phẩm",
            "Danh mục", "Nhà sản xuất",
            "Giá", "Toa thuốc", "Trạng thái"
    };

    public ProductManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = new ProductController(new ProductServiceImpl());
        reload();
    }

    // =========================
    // Base overrides
    // =========================

    @Override
    protected String titleTotal() {
        return "Tổng số sản phẩm";
    }

    @Override
    protected String[] columns() {
        return COLS;
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{60, 120, 220, 160, 160, 110, 100, 120};
    }

    @Override
    protected List<ProductResponse> fetchData() {
        return controller.getAll();
    }

    @Override
    protected Object[] mapRow(ProductResponse p, int stt) {
        return new Object[]{
                stt,
                safe(p.getCode()),
                safe(p.getName()),
                safe(p.getCategoryName()),
                safe(p.getManufacturerName()),
                p.getStandardPrice(),
                p.isRequiresPrescription() ? "Có" : "Không",
                p.getStatus()
        };
    }

    // =========================
    // Filters
    // =========================

    @Override
    protected JPanel buildFilters() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        keywordField = UiFactory.textField(20, 240);
        JButton searchBtn = UiFactory.button("🔍 Tìm", UiTheme.PRIMARY, e -> applyFilters());

        statusFilter = new JComboBox<>(new String[]{
                "ALL",
                ProductStatus.ACTIVE.name(),
                ProductStatus.INACTIVE.name(),
                ProductStatus.DISCONTINUED.name()
        });
        statusFilter.setPreferredSize(new Dimension(160, 34));
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
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField == null ? "" : keywordField.getText().trim().toLowerCase();
        String st = statusFilter == null ? "ALL" : String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String code = val(e, 1);
                String name = val(e, 2);
                String category = val(e, 3);
                String manufacturer = val(e, 4);
                String status = val(e, 7);

                if (!kw.isEmpty()) {
                    String hay = (code + " " + name + " " + category + " " + manufacturer).toLowerCase();
                    if (!hay.contains(kw)) return false;
                }

                if (!"ALL".equalsIgnoreCase(st)) {
                    if (!status.equalsIgnoreCase(st)) return false;
                }
                return true;
            }

            private String val(Entry<? extends DefaultTableModel, ? extends Integer> e, int col) {
                Object v = e.getModel().getValueAt(e.getIdentifier(), col);
                return v == null ? "" : v.toString();
            }
        });
    }

    // =========================
    // Actions
    // =========================

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("➕ Thêm", new Color(40,167,69), e -> openCreate()));
        row.add(UiFactory.button("✏️ Sửa", new Color(255,193,7), e -> openEdit()));
        row.add(UiFactory.button("🗑️ Xóa", new Color(220,53,69), e -> deleteProduct()));
        row.add(UiFactory.button("🔄 Refresh", new Color(108,117,125), e -> reload()));

        return row;
    }

    private void openCreate() {
        openProductDialog(null);
    }

    private void openEdit() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm");
            return;
        }
        String code = getCellString(row, 1);
        openProductDialog(code);
    }

    private void deleteProduct() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm");
            return;
        }
        String code = getCellString(row, 1);

        int c = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sản phẩm [" + code + "] ?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (c == JOptionPane.YES_OPTION) {
            controller.delete(code);
            reload();
        }
    }

    // =========================
    // Popup Add / Edit (INLINE)
    // =========================

    private void openProductDialog(String editCode) {
        boolean editMode = editCode != null;

        JDialog dialog = new JDialog(
                parentFrame,
                editMode ? "Sửa sản phẩm" : "Thêm sản phẩm",
                true
        );

        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JCheckBox chkRx = new JCheckBox("Cần toa thuốc");
        JComboBox<ProductStatus> cboStatus =
                new JComboBox<>(ProductStatus.values());

        if (editMode) {
            ProductResponse p = controller.getByCode(editCode);
            txtCode.setText(p.getCode());
            txtCode.setEnabled(false);
            txtName.setText(p.getName());
            txtPrice.setText(p.getStandardPrice().toString());
            chkRx.setSelected(p.isRequiresPrescription());
            cboStatus.setSelectedItem(p.getStatus());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Mã sản phẩm"));
        form.add(txtCode);
        form.add(new JLabel("Tên sản phẩm"));
        form.add(txtName);
        form.add(new JLabel("Giá"));
        form.add(txtPrice);
        form.add(new JLabel("Trạng thái"));
        form.add(cboStatus);
        form.add(new JLabel(""));
        form.add(chkRx);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            try {
                if (editMode) {
                    UpdateProductRequest req = new UpdateProductRequest();
                    req.setName(txtName.getText().trim());
                    req.setStandardPrice(new BigDecimal(txtPrice.getText().trim()));
                    req.setRequiresPrescription(chkRx.isSelected());
                    req.setStatus((ProductStatus) cboStatus.getSelectedItem());
//                  req.setCategoryId(old.getCategoryId());
                    req.setCategoryId(1L);

                    controller.update(editCode, req);
                } else {
                    CreateProductRequest req = new CreateProductRequest();
                    req.setCode(txtCode.getText().trim());
                    req.setName(txtName.getText().trim());
                    req.setStandardPrice(new BigDecimal(txtPrice.getText().trim()));
                    req.setRequiresPrescription(chkRx.isSelected());
                    req.setStatus((ProductStatus) cboStatus.getSelectedItem());
                    req.setCategoryId(1L); // demo nhanh
                    controller.create(req);
                }
                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnSave);
        actions.add(btnCancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Product Management");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 650);
            f.setLocationRelativeTo(null);
            f.setContentPane(new ProductManagementPanel(f));
            f.setVisible(true);
        });
    }
}
