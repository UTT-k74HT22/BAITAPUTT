package org.example.view;

import org.example.entity.Student;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentTableFrame extends JFrame {

    private static final String FILE_NAME = "sinhvien.dat";

    // load data
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Student> students = new ArrayList<>();

    // field insert
    private JTextField txtHoTen;
    private JTextField txtNgaySinh;
    private JTextField txtDiaChi;
    private JTextField txtGioiTinh;
    private JTextField txtMaSV;
    private JTextField txtEmail;
    private JTextField txtDiemTongKet;

    // index đang edit, -1 = thêm mới
    private int editIndex = -1;

    public StudentTableFrame() {
        setTitle("Danh sách sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);

        String[] columnNames = {
                "STT", "Họ tên", "Ngày sinh", "Địa chỉ", "Giới tính", "Mã SV", "Email", "Điểm tổng kết"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

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

        // ========== ACTION BUTTONS ============== //
        JButton btnSave = new JButton("Lưu (Thêm/Update)");
        btnSave.addActionListener(e -> onSaveOrUpdateStudent());

        JButton btnDeleteAll = new JButton("Xóa toàn bộ");
        btnDeleteAll.addActionListener(e -> onDeleteAllStudents());

        JButton btnDeleteSelected = new JButton("Xóa mục chọn");
        btnDeleteSelected.addActionListener(e -> onDeleteSelectedStudent());

        JButton btnExit = new JButton("Thoát");
        btnExit.addActionListener(e -> {
            this.dispose(); //close form current
            new LoginForm().setVisible(true);

        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDeleteSelected);
        buttonPanel.add(btnDeleteAll);
        buttonPanel.add(btnExit);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked();
            }
        });

        loadStudentsFromFile();
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

        Student sv = new Student(hoTen, ngaySinh, diaChi, gioiTinh, maSV, email, diem);

        if (editIndex == -1) {
            // thêm mới
            students.add(sv);
            JOptionPane.showMessageDialog(this,
                    "Thêm sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // update
            students.set(editIndex, sv);
            JOptionPane.showMessageDialog(this,
                    "Cập nhật sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        saveAllStudentsToFile();   // ghi lại toàn bộ list ra file
        loadStudentsFromFile();    // reload table
        clearForm();
        editIndex = -1;            // reset trạng thái
    }

    // ==================== CLICK TABLE => LOAD LÊN FORM ====================
    private void onTableRowClicked() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < students.size()) {
            Student sv = students.get(row);
            txtHoTen.setText(sv.getHoTen());
            txtNgaySinh.setText(sv.getNgaySinh());
            txtDiaChi.setText(sv.getDiaChi());
            txtGioiTinh.setText(sv.getGioiTinh());
            txtMaSV.setText(sv.getMaSinhVien());
            txtEmail.setText(sv.getEmail());
            txtDiemTongKet.setText(String.valueOf(sv.getDiemTongKet()));

            editIndex = row; // đánh dấu dòng đang edit
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sinh viên đã chọn?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        students.remove(selectedRow);
        saveAllStudentsToFile();
        loadStudentsFromFile();
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

        students.clear();
        saveAllStudentsToFile();   // ghi file rỗng
        loadStudentsFromFile();
        clearForm();
        editIndex = -1;

        JOptionPane.showMessageDialog(this,
                "Xóa toàn bộ sinh viên thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== LOAD & SAVE FILE ====================
    private void loadStudentsFromFile() {
        students.clear();
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(StudentTableFrame.FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\$");
                if (parts.length < 7) continue;

                String hoTen = parts[0].trim();
                String ngaySinh = parts[1].trim();
                String diaChi = parts[2].trim();
                String gioiTinh = parts[3].trim();
                String maSV = parts[4].trim();
                String email = parts[5].trim();
                String diemStr = parts[6].trim();

                double diemTongKet;
                try {
                    diemTongKet = Double.parseDouble(diemStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                Student sv = new Student(hoTen, ngaySinh, diaChi, gioiTinh, maSV, email, diemTongKet);
                students.add(sv);

                tableModel.addRow(new Object[]{
                        students.size(),
                        sv.getHoTen(),
                        sv.getNgaySinh(),
                        sv.getDiaChi(),
                        sv.getGioiTinh(),
                        sv.getMaSinhVien(),
                        sv.getEmail(),
                        sv.getDiemTongKet()
                });
            }

            System.out.println("Loaded " + students.size() + " students from file.");
        } catch (IOException e) {
            System.out.println("Error in loadStudentsFromFile: " + e.getMessage());
        }
    }

    // ghi lại toàn bộ list vào file (overwrite)
    private void saveAllStudentsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StudentTableFrame.FILE_NAME, false))) {
            for (Student sv : students) {
                String line = sv.getHoTen() + "$"
                        + sv.getNgaySinh() + "$"
                        + sv.getDiaChi() + "$"
                        + sv.getGioiTinh() + "$"
                        + sv.getMaSinhVien() + "$"
                        + sv.getEmail() + "$"
                        + sv.getDiemTongKet();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error in saveAllStudentsToFile: " + e.getMessage());
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
}
