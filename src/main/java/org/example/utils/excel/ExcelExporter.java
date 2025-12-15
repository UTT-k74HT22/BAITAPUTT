package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class để xuất dữ liệu ra file Excel.
 * @author Hoang Dinh Dung
 */
public class ExcelExporter {

    /**
     * Hiển thị hộp thoại lưu file và xuất dữ liệu ra file Excel.
     */
    public static <T> void exportWithDialog(List<T> data, ExcelSheetWriter<T> writer, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File(writer.sheetName() + ".xlsx"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;

        Path finalPath = ensureXlsxExtension(chooser.getSelectedFile().toPath());

        // hỏi ghi đè nếu file tồn tại
        if (Files.exists(finalPath)) {
            int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "File đã tồn tại. Bạn có muốn ghi đè không?\n" + finalPath,
                    "Xác nhận ghi đè",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        try {
            exportToFile(data, writer, finalPath);
            Object[] options = {"Mở file", "Mở thư mục", "Đóng"};
            int pick = JOptionPane.showOptionDialog(
                    parent,
                    "Xuất file Excel thành công!\n" + finalPath,
                    "Thành công",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (pick == 0) {
                openFile(finalPath.toFile(), parent);
            } else if (pick == 1) {
                Path folderPath = finalPath.getParent();
                if (folderPath != null) openFolder(folderPath.toFile(), parent);
                else JOptionPane.showMessageDialog(parent, "Không xác định được thư mục chứa file.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Lỗi khi xuất file Excel:\n" + rootMessage(e),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Core: Xuất dữ liệu ra file Excel (không phụ thuộc UI).
     */
    public static <T> void exportToFile(List<T> data, ExcelSheetWriter<T> writer, Path filePath) throws Exception {
        Path parentDir = filePath.getParent();
        if (parentDir != null) Files.createDirectories(parentDir);

        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = new ExcelStyles(workbook);
            Sheet sheet = workbook.createSheet(writer.sheetName());

            writer.create(sheet, styles, data);

            // Freeze title + header
            sheet.createFreezePane(0, 2);

            int columnCount = writer.headers().length;
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
                // thêm padding để không bị sát chữ
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 900, 255 * 256));
            }

            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                workbook.write(out);
            }
        }
    }

    // Đảm bảo file có đuôi .xlsx
    private static Path ensureXlsxExtension(Path path) {
        String p = path.toString();
        if (!p.toLowerCase().endsWith(".xlsx")) p += ".xlsx";
        return Path.of(p);
    }

    // Lấy thông điệp gốc từ chuỗi ngoại lệ
    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage() != null ? cur.getMessage() : cur.toString();
    }

    // Mở file bằng ứng dụng mặc định của hệ thống
    private static void openFile(File file, Component parent) {
        try {
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(parent,
                        "Hệ thống không hỗ trợ mở file tự động.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Không thể mở file:\n" + rootMessage(e),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mở thư mục bằng ứng dụng mặc định của hệ thống
    private static void openFolder(File folder, Component parent) {
        try {
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(parent,
                        "Hệ thống không hỗ trợ mở thư mục.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Không thể mở thư mục:\n" + rootMessage(e),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
