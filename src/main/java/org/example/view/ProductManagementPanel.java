package org.example.view;

import org.example.controller.ProductController;
import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
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
 * Product Management Panel
 * - Popup inline
 * - Category giả lập (tránh conflict module)
 * - Không dùng toString cho DTO
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

    // ===== Category giả lập =====
    private static class CategoryOption {
        Long id;
        String name;

        CategoryOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }
    }

    private static final CategoryOption[] MOCK_CATEGORIES = {
            new CategoryOption(1L, "Thuốc giảm đau"),
            new CategoryOption(2L, "Kháng sinh"),
            new CategoryOption(3L, "Vitamin")
    };

    public ProductManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = new ProductController(new ProductServiceImpl());
        reload();
    }

    @Override protected String titleTotal() {
        return "Tổng số sản phẩm";
    }

    @Override protected String[] columns() {
        return COLS;
    }

    @Override protected int[] columnWidths() {
        return new int[]{60, 120, 220, 160, 160, 110, 100, 120};
    }

    @Override protected List<ProductResponse> fetchData() {
        return controller.getAll();
    }

    @Override protected Object[] mapRow(ProductResponse p, int stt) {
        return new Object[]{
                stt,
                p.getCode(),
                p.getName(),
                p.getCategoryName(),
                p.getManufacturerName(),
                p.getStandardPrice(),
                p.isRequiresPrescription() ? "Có" : "Không",
                p.getStatus()
        };
    }

    // ================= FILTER =================

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
        row.add(new JLabel("Trạng thái:"));
        row.add(statusFilter);

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField.getText().trim().toLowerCase();
        String st = String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String hay = (
                        e.getValue(1) + " " +
                                e.getValue(2) + " " +
                                e.getValue(3) + " " +
                                e.getValue(4)
                ).toLowerCase();

                if (!kw.isEmpty() && !hay.contains(kw)) return false;
                if (!"ALL".equals(st) && !e.getValue(7).toString().equalsIgnoreCase(st)) return false;
                return true;
            }
        });
    }

    // ================= ACTIONS =================

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("➕ Thêm", new Color(40,167,69), e -> openProductDialog(null)));
        row.add(UiFactory.button("✏️ Sửa", new Color(255,193,7), e -> openEdit()));
        row.add(UiFactory.button("🗑️ Xóa", new Color(220,53,69), e -> deleteProduct()));
        row.add(UiFactory.button("🔍 Chi tiết", new Color(108,117,125), e -> openDetail()));
        row.add(UiFactory.button("🔄 Refresh", new Color(108,117,125), e -> reload()));

        return row;
    }

    // ================= DETAIL =================

    private void openDetail() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        String code = getCellString(row, 1);
        ProductDetailResponse d = controller.getDetail(code);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setText("""
                Mã: %s
                Barcode: %s
                Tên: %s

                Danh mục: %s
                Nhà sản xuất: %s (%s)

                Dạng bào chế: %s
                Đơn vị: %s
                Giá: %s
                Toa thuốc: %s

                Mô tả:
                %s
                """.formatted(
                d.getCode(),
                d.getBarcode(),
                d.getName(),
                d.getCategoryName(),
                d.getManufacturerName(),
                d.getManufacturerCountry(),
                d.getDosageForm(),
                d.getUnit(),
                d.getStandardPrice(),
                d.isRequiresPrescription() ? "Có" : "Không",
                d.getDescription()
        ));

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(480, 420));

        JOptionPane.showMessageDialog(this, sp, "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= ADD / EDIT =================

    private void openEdit() {
        int row = getSelectedModelRow();
        if (row < 0) return;
        openProductDialog(getCellString(row, 1));
    }

    private void deleteProduct() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        String code = getCellString(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Xóa [" + code + "] ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete(code);
            reload();
        }
    }

    private void openProductDialog(String editCode) {
        boolean edit = editCode != null;

        JDialog d = new JDialog(parentFrame, edit ? "Sửa sản phẩm" : "Thêm sản phẩm", true);
        d.setLayout(new BorderLayout());

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField barcode = new JTextField();
        JTextField dosage = new JTextField();
        JTextField unit = new JTextField();
        JTextField price = new JTextField();
        JTextArea desc = new JTextArea(3, 20);

        JCheckBox rx = new JCheckBox("Cần toa thuốc");
        JComboBox<ProductStatus> status = new JComboBox<>(ProductStatus.values());
        JComboBox<CategoryOption> category = new JComboBox<>(MOCK_CATEGORIES);

        if (edit) {
            ProductResponse p = controller.getByCode(editCode);
            code.setText(p.getCode());
            code.setEnabled(false);
            name.setText(p.getName());
            price.setText(p.getStandardPrice().toString());
            rx.setSelected(p.isRequiresPrescription());
            status.setSelectedItem(p.getStatus());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        form.add(new JLabel("Mã")); form.add(code);
        form.add(new JLabel("Tên")); form.add(name);
        form.add(new JLabel("Barcode")); form.add(barcode);
        form.add(new JLabel("Danh mục")); form.add(category);
        form.add(new JLabel("Dạng bào chế")); form.add(dosage);
        form.add(new JLabel("Đơn vị")); form.add(unit);
        form.add(new JLabel("Giá")); form.add(price);
        form.add(new JLabel("Trạng thái")); form.add(status);
        form.add(new JLabel("")); form.add(rx);
        form.add(new JLabel("Mô tả")); form.add(new JScrollPane(desc));

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                CategoryOption c = (CategoryOption) category.getSelectedItem();

                if (edit) {
                    UpdateProductRequest r = new UpdateProductRequest();
                    r.setName(name.getText());
                    r.setStandardPrice(new BigDecimal(price.getText()));
                    r.setRequiresPrescription(rx.isSelected());
                    r.setStatus((ProductStatus) status.getSelectedItem());
                    r.setCategoryId(c.id);
                    controller.update(editCode, r);
                } else {
                    CreateProductRequest r = new CreateProductRequest();
                    r.setCode(code.getText());
                    r.setName(name.getText());
                    r.setStandardPrice(new BigDecimal(price.getText()));
                    r.setRequiresPrescription(rx.isSelected());
                    r.setStatus((ProductStatus) status.getSelectedItem());
                    r.setCategoryId(c.id);
                    controller.create(r);
                }
                d.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(save);
        actions.add(new JButton("Hủy") {{ addActionListener(e -> d.dispose()); }});

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(parentFrame);
        d.setVisible(true);
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
