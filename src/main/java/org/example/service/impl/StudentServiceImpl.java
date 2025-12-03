package org.example.service.impl;

import org.example.entity.Student;
import org.example.service.StudentService;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private static final String FILE_NAME = "sinhvien.dat";

    private final List<Student> students = new ArrayList<>();

    public StudentServiceImpl() {
        loadFromFile();
    }

    @Override
    public List<Student> getAllStudents() {
        // trả về copy để code bên ngoài không sửa trực tiếp list
        return new ArrayList<>(students);
    }

    @Override
    public void addStudent(Student student) {
        students.add(student);
        saveToFile();
    }

    @Override
    public void updateStudent(int index, Student student) {
        if (index >= 0 && index < students.size()) {
            students.set(index, student);
            saveToFile();
        }
    }

    @Override
    public void deleteStudent(int index) {
        if (index >= 0 && index < students.size()) {
            students.remove(index);
            saveToFile();
        }
    }

    @Override
    public void deleteAll() {
        students.clear();
        saveToFile();
    }

    @Override
    public boolean existsMaSV(String maSV, int ignoreIndex) {
        if (maSV == null) return false;
        String target = maSV.trim().toLowerCase();
        for (int i = 0; i < students.size(); i++) {
            if (i == ignoreIndex) continue;
            if (students.get(i).getMaSinhVien().trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existsEmail(String email, int ignoreIndex) {
        if (email == null) return false;
        String target = email.trim().toLowerCase();
        for (int i = 0; i < students.size(); i++) {
            if (i == ignoreIndex) continue;
            if (students.get(i).getEmail().trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Student> searchByMaSV(String keyword) {
        List<Student> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            result.addAll(students);
            return result;
        }
        String key = keyword.trim().toLowerCase();
        for (Student sv : students) {
            if (sv.getMaSinhVien() != null &&
                    sv.getMaSinhVien().trim().toLowerCase().contains(key)) {
                result.add(sv);
            }
        }
        return result;
    }

    @Override
    public void sortByScoreDesc() {
        Collections.sort(students,
                Comparator.comparingDouble(Student::getDiemTongKet).reversed());
        saveToFile();
    }

    // ============ PRIVATE: LOAD / SAVE FILE ============

    private void loadFromFile() {
        students.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
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
            }

            System.out.println("Loaded " + students.size() + " students from file.");
        } catch (IOException e) {
            System.out.println("Error loadFromFile: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
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
            System.out.println("Error saveToFile: " + e.getMessage());
        }
    }
}
