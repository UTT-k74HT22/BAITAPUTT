package org.example.utils.importer.demo;

import org.example.utils.importer.template.ProductTemplateGenerator;

/**
 * Class để test tạo file Excel example
 * Chạy main method này để tạo file Excel mẫu
 */
public class CreateExcelExample {
    public static void main(String[] args) {
        try {
            // Tạo file Excel example tại thư mục gốc project
            String outputPath = "Product_Import_Example.xlsx";
            ProductTemplateGenerator.generateTemplate(outputPath);

            System.out.println("✅ File Excel example đã được tạo thành công!");
            System.out.println("📁 Đường dẫn: " + outputPath);
            System.out.println("\n📖 Hướng dẫn:");
            System.out.println("1. Mở file Excel");
            System.out.println("2. Header row (dòng 1): Code, Name, Category Code, Manufacturer Code, Barcode, Dosage Form, Unit, Description, Standard Price, Requires Prescription");
            System.out.println("3. Data rows (từ dòng 2): Điền dữ liệu theo mẫu");
            System.out.println("4. Lưu file và import vào hệ thống");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo file Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

