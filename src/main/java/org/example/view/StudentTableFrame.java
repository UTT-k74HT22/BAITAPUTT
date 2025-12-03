package org.example.view;

import org.example.entity.Student;
import org.example.service.StudentService;
import org.example.service.impl.StudentServiceImpl;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentTableFrame extends JFrame {

    // UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh;
    private JTextField txtDiaChi;
    private JTextField txtGioiTinh;
    private JTextField txtMaSV;
    private JTextField txtEmail;
    private JTextField txtDiemTongKet;
    private JTextField txtSearch;

    // service
    private final StudentService studentService;

    // index đang edit trong list gốc, -1 = thêm mới
    private int editIndex = -1;

    public StudentTableFrame() {
        this.studentService = new StudentServiceImpl(); // inject service

        setTitle("Danh sách sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // ====== TABLE ======
        String[] columnNames = {
                "STT", "Họ tên", "Ngày sinh", "Địa chỉ",
                "Giới tính", "Mã SV", "Email", "Điểm tổng kết"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // ====== INPUT FORM ======
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 5, 5));

        txtHoTen = new JTextField();
        txtNgaySinh = new JTextField();
        txtDiaChi = new JTextField();
        txtGioiTinh = new JTextField();
        txtMaSV = new JTextField();
        txtEmail = new JTextField();
        txtDiemTongKet = new JTextField();

        inputPanel.add(new JLabel("Họ tên:"));
        inputPanel.add(txtHoTen);
        inputPanel.add(new JLabel("Ngày sinh:"));
        inputPanel.add(txtNgaySinh);

        inputPanel.add(new JLabel("Địa chỉ:"));
        inputPanel.add(txtDiaChi);
        inputPanel.add(new JLabel("Giới tính:"));
        inputPanel.add(txtGioiTinh);

        inputPanel.add(new JLabel("Mã SV:"));
        inputPanel.add(txtMaSV);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("Điểm tổng kết:"));
        inputPanel.add(txtDiemTongKet);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        // ====== BUTTONS ======
        JButton btnSave = new JButton("Lưu (Thêm/Update)");
        btnSave.addActionListener(e -> onSaveOrUpdateStudent());

        JButton btnDeleteSelected = new JButton("Xóa mục chọn");
        btnDeleteSelected.addActionListener(e -> onDeleteSelectedStudent());

        JButton btnDeleteAll = new JButton("Xóa toàn bộ");
        btnDeleteAll.addActionListener(e -> onDeleteAllStudents());

        JButton btnExit = new JButton("Thoát");
        btnExit.addActionListener(e -> {
            StudentTableFrame.this.dispose();
            new LoginForm().setVisible(true);
        });

        JButton btnSort = new JButton("Sắp xếp theo điểm ↓");
        btnSort.addActionListener(e -> onSortByScore());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDeleteSelected);
        buttonPanel.add(btnDeleteAll);
        buttonPanel.add(btnSort);
        buttonPanel.add(btnExit);

        // ====== SEARCH / FILTER ======
        txtSearch = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm theo Mã SV:"));
        searchPanel.add(txtSearch);

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String keyword = txtSearch.getText().trim();
                if (keyword.isEmpty()) {
                    reloadTableFromService(); // hết filter -> full
                } else {
                    filterStudents(keyword);
                }
                // mỗi lần tìm thì reset editIndex
                editIndex = -1;
                clearForm();
            }
        });

        // ====== TOP WRAPPER ======
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ====== TABLE CLICK => LOAD FORM ======
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked();
            }
        });

        // Load dữ liệu từ service (file) lần đầu
        reloadTableFromService();
    }

    // ==================== FILTER ====================
    private void filterStudents(String keyWord) {
        List<Student> filtered = studentService.searchByMaSV(keyWord);
        reloadTable(filtered);
    }

    // ==================== THÊM / UPDATE ====================
    private void onSaveOrUpdateStudent() {
        String hoTen = txtHoTen.getText().trim();
        String ngaySinh = txtNgaySinh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = txtGioiTinh.getText().trim();
        String maSV = txtMaSV.getText().trim();
        String email = txtEmail.getText().trim();
        String diemStr = txtDiemTongKet.getText().trim();

        if (hoTen.isEmpty() || ngaySinh.isEmpty() || diaChi.isEmpty()
                || gioiTinh.isEmpty() || maSV.isEmpty()
                || email.isEmpty() || diemStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Thiếu dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double diem;
        try {
            diem = Double.parseDouble(diemStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Điểm tổng kết phải là số!",
                    "Lỗi định dạng",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Không cho edit khi đang filter (để tránh lệch index)
        if (!txtSearch.getText().trim().isEmpty() && editIndex != -1) {
            JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật khi đang lọc. Vui lòng xóa ô tìm kiếm rồi thử lại.",
                    "Đang lọc dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validate trùng mã SV & email qua service
        if (studentService.existsMaSV(maSV, editIndex)) {
            JOptionPane.showMessageDialog(this,
                    "Mã sinh viên đã tồn tại!",
                    "Lỗi trùng mã SV",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (studentService.existsEmail(email, editIndex)) {
            JOptionPane.showMessageDialog(this,
                    "Email đã tồn tại!",
                    "Lỗi trùng email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student sv = new Student(hoTen, ngaySinh, diaChi, gioiTinh, maSV, email, diem);

        if (editIndex == -1) {
            studentService.addStudent(sv);
            JOptionPane.showMessageDialog(this,
                    "Thêm sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            studentService.updateStudent(editIndex, sv);
            JOptionPane.showMessageDialog(this,
                    "Cập nhật sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        reloadTableFromService();
        clearForm();
        editIndex = -1;
    }

    // ==================== CLICK TABLE => LOAD FORM ====================
    private void onTableRowClicked() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        // Nếu đang filter, không cho sửa (đơn giản, an toàn)
        if (!txtSearch.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Đang ở chế độ lọc, không thể sửa. Xóa ô tìm kiếm rồi chọn lại.",
                    "Đang lọc dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Student> list = studentService.getAllStudents();
        if (row >= 0 && row < list.size()) {
            Student sv = list.get(row);
            txtHoTen.setText(sv.getHoTen());
            txtNgaySinh.setText(sv.getNgaySinh());
            txtDiaChi.setText(sv.getDiaChi());
            txtGioiTinh.setText(sv.getGioiTinh());
            txtMaSV.setText(sv.getMaSinhVien());
            txtEmail.setText(sv.getEmail());
            txtDiemTongKet.setText(String.valueOf(sv.getDiemTongKet()));

            editIndex = row; // index trong list gốc
        }
    }

    // ==================== XÓA MỤC CHỌN ====================
    private void onDeleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một sinh viên để xóa.",
                    "Chưa chọn mục",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Không cho xóa khi đang filter, để tránh lệch index
        if (!txtSearch.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa khi đang lọc. Vui lòng xóa ô tìm kiếm rồi thử lại.",
                    "Đang lọc dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sinh viên đã chọn?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        studentService.deleteStudent(selectedRow);
        reloadTableFromService();
        clearForm();
        editIndex = -1;

        JOptionPane.showMessageDialog(this,
                "Xóa sinh viên thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== XÓA TOÀN BỘ ====================
    private void onDeleteAllStudents() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa toàn bộ sinh viên?",
                "Xác nhận xóa toàn bộ",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        studentService.deleteAll();
        reloadTableFromService();
        clearForm();
        editIndex = -1;

        JOptionPane.showMessageDialog(this,
                "Xóa toàn bộ sinh viên thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== SẮP XẾP THEO ĐIỂM ====================
    private void onSortByScore() {
        studentService.sortByScoreDesc();
        reloadTableFromService();
    }

    // ==================== RENDER TABLE ====================
    private void reloadTableFromService() {
        List<Student> list = studentService.getAllStudents();
        reloadTable(list);
    }

    private void reloadTable(List<Student> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (Student sv : list) {
            tableModel.addRow(new Object[]{
                    stt++,
                    sv.getHoTen(),
                    sv.getNgaySinh(),
                    sv.getDiaChi(),
                    sv.getGioiTinh(),
                    sv.getMaSinhVien(),
                    sv.getEmail(),
                    sv.getDiemTongKet()
            });
        }
    }

    private void clearForm() {
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtDiaChi.setText("");
        txtGioiTinh.setText("");
        txtMaSV.setText("");
        txtEmail.setText("");
        txtDiemTongKet.setText("");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new StudentTableFrame().setVisible(true));
//    }
}
