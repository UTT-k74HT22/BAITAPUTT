package org.example.entity;

/**
 *
 * @author hoang
 */
public class Student extends Person {

    private String maSinhVien;
    private String email;
    private Double diemTongKet;

    public Student() {
    }

    public Student(String hoTen, String ngaySinh, String diaChi, String gioiTinh,
                   String maSinhVien, String email, Double diemTongKet) {
        super(hoTen, ngaySinh, diaChi, gioiTinh);
        this.maSinhVien = maSinhVien;
        this.email = email;
        this.diemTongKet = diemTongKet;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getDiemTongKet() {
        return diemTongKet;
    }

    public void setDiemTongKet(Double diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Ma sinh vien: " + this.maSinhVien);
        System.out.println("Email: " + this.email);
        System.out.println("DIem tong ket: " + this.diemTongKet);
    }

    @Override
    public String toString() {
        return "Student{" + "maSinhVien=" + maSinhVien + ", email=" + email + ", diemTongKet=" + diemTongKet + '}';
    }
}
