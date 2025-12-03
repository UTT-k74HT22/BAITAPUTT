package org.example.view;

import org.example.entity.Student;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
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

    //field delete, update could be added here
    private int editIndex = -1; //-1 = add new item

    public StudentTableFrame() {
        setTitle("Danh sách sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);

        String[] columnNames = {
                "Họ tên", "Ngày sinh", "Địa chỉ", "Giới tính",
                "Mã SV", "Email", "Điểm tổng kết"
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

        // Save button
        JButton btnSave = new JButton("Lưu vào file & Load lại");
        btnSave.addActionListener(e -> onSaveOrUpdateStudent());

        // Delete all button
        JButton btnDeleteAll = new JButton("Xóa toàn bộ");
        btnDeleteAll.addActionListener(e -> onDeleteAllStudents());

        // Delete by select
        JButton btnDeleteSelected = new JButton("Xóa mục chọn");
        btnDeleteSelected.addActionListener(e -> onDeleteSelectedStudent());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDeleteAll);
        buttonPanel.add(btnDeleteSelected);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnSave, BorderLayout.SOUTH);

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        // selected item
        table.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });

        // Mở lên load sẵn file (nếu có)
        loadStudentsFromFile();
    }

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

        String line = hoTen + "$" + ngaySinh + "$" + diaChi + "$"
                + gioiTinh + "$" + maSV + "$" + email + "$" + diem;

        try (BufferedWriter bw = new BufferedWriter(
                new java.io.FileWriter(StudentTableFrame.FILE_NAME, true))) {

            System.out.println("Running saving data to file = " + StudentTableFrame.FILE_NAME);
            bw.write(line);
            bw.newLine();

        } catch (IOException ex) {
            System.out.println("Error in save student to file: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Lưu sinh viên thất bại: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Lưu sinh viên thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);

        loadStudentsFromFile();
        clearForm();
    }

    private void loadStudentsFromFile() {
        students.clear();
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(StudentTableFrame.FILE_NAME))) {
            System.out.println("Running loading data in file = " + StudentTableFrame.FILE_NAME);

            String line;
            System.out.println("=== Danh sách sinh viên ===");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\$");
                if (parts.length < 7) {
                    System.out.println("Error line not valid: " + line);
                    continue;
                }

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
                    System.out.println("Diem not format in line: " + line);
                    continue;
                }

                Student sv = new Student(hoTen, ngaySinh, diaChi, gioiTinh, maSV, email, diemTongKet);

                if (editIndex == -1) {
                    System.out.println("Adding new student:");
                    students.add(sv);
                    JOptionPane.showMessageDialog(this, "Thêm sinh viên: " + sv.getHoTen() + " thành công!");
                } else {
                    System.out.println("Updating student at index " + editIndex + ":");
                    students.set(editIndex, sv);
                    JOptionPane.showMessageDialog(this, "Cập nhật sinh viên: " + sv.getHoTen() + " thành công!");
                    editIndex = -1;
                }

                tableModel.addRow(new Object[]{
                        sv.getHoTen(),
                        sv.getNgaySinh(),
                        sv.getDiaChi(),
                        sv.getGioiTinh(),
                        sv.getMaSinhVien(),
                        sv.getEmail(),
                        sv.getDiemTongKet()
                });

                sv.xuat();
                System.out.println("------------------------------------");
            }

            System.out.println("Loaded " + students.size() + " students from file.");
        } catch (IOException e) {
            System.out.println("Error in loadStudentsFromFile: " + e.getMessage());
        }
    }

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
        } else {
            students.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this,
                    "Xóa sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onDeleteAllStudents() {

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa toàn bộ sinh viên?",
                "Xác nhận xóa toàn bộ",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        } else {
            students.clear();
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this,
                    "Xóa toàn bộ sinh viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
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
//        SwingUtilities.invokeLater(() -> {
//            new StudentTableFrame().setVisible(true);
//        });
//    }
}
