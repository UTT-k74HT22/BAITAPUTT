# 📘 KIẾN THỨC SWING UI - TỔNG HỢP

## 🎯 TỔNG QUAN
Tất cả các màn hình trong hệ thống dùng **Java Swing** để xây dựng giao diện desktop.

## 🧩 CẤU TRÚC PHÂN CẤP

```
JFrame (Cửa sổ chính)
    ├── JMenuBar (Menu bar: File, Edit, View...)
    ├── JPanel (Content pane)
        ├── JPanel (Header - Title, Search, Filters)
        ├── JScrollPane
        │   └── JTable (Hiển thị dữ liệu)
        └── JPanel (Footer - Buttons, Pagination)
```

## 📦 COMPONENTS

### **1. JFrame - Cửa sổ chính**
```java
public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Quản lý kho");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        
        // Set layout
        setLayout(new BorderLayout());
        
        // Add components
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }
}
```

### **2. JPanel - Container**
```java
JPanel panel = new JPanel();
panel.setLayout(new BorderLayout());
panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
panel.setBackground(Color.WHITE);
```

### **3. JTable - Bảng dữ liệu**
```java
// Model
DefaultTableModel model = new DefaultTableModel();
model.setColumnIdentifiers(new String[]{"Mã", "Tên", "Giá", "Số lượng"});

// Table
JTable table = new JTable(model);
table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
table.setRowHeight(30);

// Add to ScrollPane
JScrollPane scrollPane = new JScrollPane(table);
```

### **4. Buttons**
```java
JButton btnAdd = new JButton("Thêm");
btnAdd.setFont(new Font("Arial", Font.PLAIN, 14));
btnAdd.setBackground(new Color(76, 175, 80)); // Green
btnAdd.setForeground(Color.WHITE);
btnAdd.setFocusPainted(false);
btnAdd.addActionListener(e -> handleAdd());
```

### **5. Text Fields**
```java
JTextField searchField = new JTextField(20);
searchField.setFont(new Font("Arial", Font.PLAIN, 14));
searchField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(Color.GRAY),
    BorderFactory.createEmptyBorder(5, 5, 5, 5)
));
```

### **6. ComboBox (Dropdown)**
```java
String[] options = {"Tất cả", "Hoạt động", "Vô hiệu"};
JComboBox<String> combo = new JComboBox<>(options);
combo.addActionListener(e -> applyFilter());
```

### **7. Labels**
```java
JLabel titleLabel = new JLabel("Quản lý sản phẩm");
titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
titleLabel.setForeground(new Color(33, 150, 243)); // Blue
```

## 🎨 LAYOUTS

### **1. BorderLayout**
```
┌─────────────────────────┐
│      NORTH (Header)     │
├───┬─────────────────┬───┤
│ W │                 │ E │
│ E │    CENTER       │ A │
│ S │   (Content)     │ S │
│ T │                 │ T │
├───┴─────────────────┴───┤
│     SOUTH (Footer)      │
└─────────────────────────┘
```

```java
panel.setLayout(new BorderLayout());
panel.add(header, BorderLayout.NORTH);
panel.add(content, BorderLayout.CENTER);
panel.add(footer, BorderLayout.SOUTH);
```

### **2. FlowLayout - Xếp theo dòng**
```
┌────────────────────────┐
│ [Btn1] [Btn2] [Btn3]   │
│ [Btn4] [Btn5]          │
└────────────────────────┘
```

```java
JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
panel.add(btn1);
panel.add(btn2);
```

### **3. GridLayout - Lưới**
```
┌───────┬───────┬───────┐
│ Cell1 │ Cell2 │ Cell3 │
├───────┼───────┼───────┤
│ Cell4 │ Cell5 │ Cell6 │
└───────┴───────┴───────┘
```

```java
panel.setLayout(new GridLayout(2, 3, 10, 10)); // 2 rows, 3 cols, gap 10px
```

### **4. BoxLayout - Xếp dọc/ngang**
```java
JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical

panel.add(label1);
panel.add(Box.createVerticalStrut(10)); // Spacer 10px
panel.add(label2);
```

## 🔍 JTable - JTABLE VỚI TableRowSorter

### **TableRowSorter - Filter dữ liệu**

```java
DefaultTableModel model = new DefaultTableModel();
JTable table = new JTable(model);

// Create sorter
TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
table.setRowSorter(sorter);

// Apply filter
JTextField searchField = new JTextField();
searchField.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { applyFilter(); }
    public void removeUpdate(DocumentEvent e) { applyFilter(); }
    public void changedUpdate(DocumentEvent e) { applyFilter(); }
});

private void applyFilter() {
    String keyword = searchField.getText().trim().toLowerCase();
    
    if (keyword.isEmpty()) {
        sorter.setRowFilter(null); // Show all
    } else {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                // Search in multiple columns
                for (int i = 0; i < entry.getValueCount(); i++) {
                    String value = entry.getStringValue(i).toLowerCase();
                    if (value.contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
```

### **Multi-column filter**

```java
private void applyFilters() {
    String keyword = searchField.getText().trim().toLowerCase();
    String selectedStatus = statusCombo.getSelectedItem().toString();
    
    sorter.setRowFilter(new RowFilter<>() {
        public boolean include(Entry e) {
            // Filter by keyword (search in col 1, 2, 3)
            String haystack = (e.getValue(1) + " " + e.getValue(2) + " " + e.getValue(3)).toLowerCase();
            if (!keyword.isEmpty() && !haystack.contains(keyword)) {
                return false;
            }
            
            // Filter by status (col 5)
            if (!"Tất cả".equals(selectedStatus)) {
                if (!e.getValue(5).toString().equals(selectedStatus)) {
                    return false;
                }
            }
            
            return true;
        }
    });
}
```

## 🎨 CUSTOM RENDERERS

### **TableCellRenderer - Tô màu cell**

```java
table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        String status = value.toString();
        if ("Hoạt động".equals(status)) {
            c.setBackground(new Color(200, 255, 200)); // Light green
        } else if ("Vô hiệu".equals(status)) {
            c.setBackground(new Color(255, 200, 200)); // Light red
        } else {
            c.setBackground(Color.WHITE);
        }
        
        return c;
    }
});
```

### **Số tiền - Currency format**

```java
table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    protected void setValue(Object value) {
        if (value instanceof Number) {
            setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value));
        } else {
            super.setValue(value);
        }
    }
});
```

## 💬 DIALOG

### **1. JOptionPane - Thông báo**

```java
// Success
JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

// Error
JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

// Confirm
int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
if (choice == JOptionPane.YES_OPTION) {
    // Delete
}
```

### **2. Custom Dialog - JDialog**

```java
public class ProductDialog extends JDialog {
    private JTextField codeField;
    private JTextField nameField;
    private JButton btnSave;
    private Product result;
    
    public ProductDialog(JFrame parent, Product product) {
        super(parent, "Thông tin sản phẩm", true); // Modal
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        // Build UI
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Mã sản phẩm:"));
        panel.add(codeField = new JTextField());
        panel.add(new JLabel("Tên sản phẩm:"));
        panel.add(nameField = new JTextField());
        
        // Pre-fill
        if (product != null) {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
        }
        
        // Buttons
        btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            result = new Product();
            result.setCode(codeField.getText());
            result.setName(nameField.getText());
            dispose();
        });
        
        add(panel, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
    }
    
    public Product getResult() {
        return result;
    }
}

// Usage:
ProductDialog dialog = new ProductDialog(this, selectedProduct);
dialog.setVisible(true);
Product result = dialog.getResult();
if (result != null) {
    // Save
}
```

## ⚡ EVENT HANDLING

### **ActionListener - Button click**
```java
btnSave.addActionListener(e -> handleSave());
btnDelete.addActionListener(e -> handleDelete());
```

### **DocumentListener - Text field change**
```java
searchField.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { applyFilter(); }
    public void removeUpdate(DocumentEvent e) { applyFilter(); }
    public void changedUpdate(DocumentEvent e) { applyFilter(); }
});
```

### **ListSelectionListener - Table row selection**
```java
table.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Get data from selected row
            Long id = (Long) table.getValueAt(selectedRow, 0);
            loadDetails(id);
        }
    }
});
```

## 🎨 BEST PRACTICES

### **1. Tách logic ra Controller**
```java
// ❌ BAD: Business logic trong View
btnSave.addActionListener(e -> {
    // 50 lines of validation, DB insert...
});

// ✅ GOOD: Gọi Controller
btnSave.addActionListener(e -> controller.handleSave(getData()));
```

### **2. Refresh Table**
```java
private void refreshTable() {
    model.setRowCount(0); // Clear all rows
    
    List<Product> products = controller.getAllProducts();
    for (Product p : products) {
        model.addRow(new Object[]{
            p.getId(),
            p.getCode(),
            p.getName(),
            p.getPrice(),
            p.getQuantity()
        });
    }
}
```

### **3. SwingWorker - Không block UI**
```java
// ❌ BAD: Truy vấn DB trên EDT → UI đơ
btnLoad.addActionListener(e -> {
    List<Product> products = controller.getAllProducts(); // Slow query
    refreshTable(products);
});

// ✅ GOOD: Dùng SwingWorker
btnLoad.addActionListener(e -> {
    new SwingWorker<List<Product>, Void>() {
        @Override
        protected List<Product> doInBackground() throws Exception {
            return controller.getAllProducts(); // Run in background
        }
        
        @Override
        protected void done() {
            try {
                List<Product> products = get();
                refreshTable(products); // Update UI on EDT
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            }
        }
    }.execute();
});
```

## ❓ CÂU HỎI VẤN ĐÁP

### **Q1: JFrame vs JPanel vs JDialog?**
**A:**
- **JFrame:** Cửa sổ chính (có title bar, close button)
- **JPanel:** Container chứa components
- **JDialog:** Popup window (modal/non-modal)

### **Q2: BorderLayout vs FlowLayout vs GridLayout?**
**A:**
- **BorderLayout:** Chia 5 vùng (North, South, East, West, Center)
- **FlowLayout:** Xếp theo dòng, xuống dòng khi hết chỗ
- **GridLayout:** Lưới rows x cols

### **Q3: TableRowSorter hoạt động thế nào?**
**A:**
1. Tạo sorter: `new TableRowSorter<>(model)`
2. Set cho table: `table.setRowSorter(sorter)`
3. Filter: `sorter.setRowFilter(RowFilter)` → Chỉ hiển thị row pass filter

**Client-side:** Dữ liệu đã load về, filter ngay trên table → Nhanh!

### **Q4: Custom cell renderer để làm gì?**
**A:** Tùy chỉnh hiển thị cell:
- Tô màu theo giá trị (xanh/đỏ)
- Format số tiền: 25,000,000 ₫
- Hiển thị icon

### **Q5: Modal vs Non-modal dialog?**
**A:**
- **Modal:** Block parent window, phải đóng dialog mới tương tác parent
- **Non-modal:** Không block, có thể click parent

```java
new JDialog(parent, "Title", true);  // Modal
new JDialog(parent, "Title", false); // Non-modal
```

### **Q6: Demo tạo màn hình đơn giản?**
**A:**
```java
public class SimpleFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    
    public SimpleFrame() {
        setTitle("Demo");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(new JLabel("Tìm kiếm:"));
        header.add(searchField = new JTextField(20));
        add(header, BorderLayout.NORTH);
        
        // Table
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Tên", "Giá"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Footer
        JPanel footer = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        footer.add(btnAdd);
        add(footer, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
```

## 📝 CHECKLIST ÔN TẬP

- [ ] JFrame, JPanel, JDialog khác nhau thế nào
- [ ] 3 loại Layout: BorderLayout, FlowLayout, GridLayout
- [ ] JTable + TableRowSorter filter
- [ ] Custom TableCellRenderer tô màu
- [ ] JOptionPane vs JDialog
- [ ] Event handling: ActionListener, DocumentListener
- [ ] SwingWorker tránh block UI

**🎯 Nắm vững Swing UI - Cơ sở cho tất cả màn hình!**
