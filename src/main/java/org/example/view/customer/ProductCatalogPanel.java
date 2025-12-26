package org.example.view.customer;

import org.example.controller.ProductController;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.service.impl.ProductServiceImpl;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ================================
 * PANEL HIỂN THỊ SẢN PHẨM CHO KHÁCH
 * - Chỉ xem (NO CRUD)
 * - Có tìm kiếm
 * - Có xem chi tiết
 * ================================
 */
public class ProductCatalogPanel extends JPanel {

    private final ProductController controller;

    private JTable table;
    private DefaultTableModel model;
    private JTextField keywordField;

    private static final String[] COLS = {
            "STT", "Mã", "Tên sản phẩm", "Danh mục", "Giá", "Toa thuốc"
    };

    public ProductCatalogPanel() {
        this.controller = new ProductController(new ProductServiceImpl());
        initUI();
        loadData();
    }

    // ================= UI =================

    private void initUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    /**
     * Thanh tìm kiếm phía trên
     */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        keywordField = UiFactory.textField(20, 260);
        JButton searchBtn = UiFactory.button("🔍 Tìm", UiTheme.PRIMARY, e -> applyFilter());
        JButton detailBtn = UiFactory.button("📄 Chi tiết", UiTheme.SECONDARY, e -> openDetail());

        p.add(new JLabel("Tìm kiếm:"));
        p.add(keywordField);
        p.add(searchBtn);
        p.add(Box.createHorizontalStrut(12));
        p.add(detailBtn);

        return p;
    }

    /**
     * Bảng danh sách sản phẩm
     */
    private JScrollPane buildTable() {
        model = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(table);
    }

    // ================= DATA =================

    /**
     * Load danh sách sản phẩm (chỉ ACTIVE)
     */
    private void loadData() {
        model.setRowCount(0);

        List<ProductResponse> products = controller.getAll();

        int stt = 1;
        for (ProductResponse p : products) {
            // 👉 chỉ hiển thị sản phẩm ACTIVE cho khách
            if (!"ACTIVE".equalsIgnoreCase(p.getStatus().name())) continue;

            model.addRow(new Object[]{
                    stt++,
                    p.getCode(),
                    p.getName(),
                    p.getCategoryName(),
                    p.getStandardPrice(),
                    p.isRequiresPrescription() ? "Có" : "Không"
            });
        }
    }

    // ================= FILTER =================

    /**
     * Lọc theo từ khóa
     */
    private void applyFilter() {
        String kw = keywordField.getText().trim().toLowerCase();
        model.setRowCount(0);

        List<ProductResponse> products = controller.getAll();
        int stt = 1;

        for (ProductResponse p : products) {
            if (!"ACTIVE".equalsIgnoreCase(p.getStatus().name())) continue;

            String hay = (p.getCode() + " " + p.getName() + " " + p.getCategoryName()).toLowerCase();
            if (!kw.isEmpty() && !hay.contains(kw)) continue;

            model.addRow(new Object[]{
                    stt++,
                    p.getCode(),
                    p.getName(),
                    p.getCategoryName(),
                    p.getStandardPrice(),
                    p.isRequiresPrescription() ? "Có" : "Không"
            });
        }
    }

    // ================= DETAIL =================

    /**
     * Xem chi tiết sản phẩm
     */
    private void openDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm");
            return;
        }

        String code = model.getValueAt(row, 1).toString();
        ProductDetailResponse d = controller.getDetail(code);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setText("""
                TÊN SẢN PHẨM: %s
                MÃ: %s
                BARCODE: %s
                
                DANH MỤC: %s
                NHÀ SẢN XUẤT: %s
                
                DẠNG BÀO CHẾ: %s
                ĐƠN VỊ: %s
                GIÁ: %s
                
                TOA THUỐC: %s
                
                MÔ TẢ:
                %s
                """.formatted(
                d.getName(),
                d.getCode(),
                d.getBarcode(),
                d.getCategoryName(),
                d.getManufacturerName(),
                d.getDosageForm(),
                d.getUnit(),
                d.getStandardPrice(),
                d.isRequiresPrescription() ? "Cần toa" : "Không cần toa",
                d.getDescription()
        ));

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(450, 420));

        JOptionPane.showMessageDialog(
                this,
                sp,
                "Chi tiết sản phẩm",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= DEMO =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Danh mục sản phẩm");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(900, 600);
            f.setLocationRelativeTo(null);
            f.setContentPane(new ProductCatalogPanel());
            f.setVisible(true);
        });
    }
}
