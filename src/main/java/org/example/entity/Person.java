package org.example.entity;

import java.io.Serializable;

/**
 *
 * @author hoang
 */
public class Person implements Serializable {

    private String hoTen;
    private String ngaySinh;
    private String diaChi;
    private String gioiTinh;

    public Person() {
    }

    public Person(String hoTen, String ngaySinh, String diaChi, String gioiTinh) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void xuat() {
        System.out.println("Ho ten: " + this.hoTen);
        System.out.println("Ngay sinh: " + this.ngaySinh);
        System.out.println("Dia chi: " + this.diaChi);
        System.out.println("Gioi tinh: " + this.gioiTinh);
    }

    @Override
    public String toString() {
        return "Person{" + "hoTen=" + hoTen + ", ngaySinh=" + ngaySinh + ", diaChi=" + diaChi + ", gioiTinh=" + gioiTinh + '}';
    }
}
