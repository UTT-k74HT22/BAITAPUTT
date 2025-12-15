package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.example.dto.response.EmployeeResponse;
import java.util.List;

/**
 * Excel writer cho DTO EmployeeResponse.
 * @author Hoang Dinh Dung
 */
public class EmployeeExcelWriter extends AbstractExcelWriter<EmployeeResponse> {

    @Override
    public String sheetName() {
        return "Employees";
    }

    @Override
    public String title() {
        return "DANH SÁCH NHÂN VIÊN";
    }

    @Override
    public String[] headers() {
        return new String[]{
                "STT",
                "Account Username",
                "Mã nhân viên",
                "Họ và tên",
                "Chức vụ",
                "Phòng ban",
                "Số điện thoại",
                "Email",
                "Trạng thái",
                "Ngày vào làm"
        };
    }

    @Override
    public void create(Sheet sheet, ExcelStyles styles, List<EmployeeResponse> data) {
        // Title row
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 600);

        Cell tCell = titleRow.createCell(0);
        tCell.setCellValue(title());
        tCell.setCellStyle(styles.get(StyleKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers().length - 1));

        // Header row
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 420);

        String[] hs = headers();
        for (int i = 0; i < hs.length; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(hs[i]);
            hCell.setCellStyle(styles.get(StyleKey.HEADER));
        }

        // Data rows
        int rowNum = 2;
        int stt = 1;

        for (EmployeeResponse e : data) {
            Row r = sheet.createRow(rowNum++);
            r.setHeight((short) 380);

            cell(r, 0, stt++, styles.get(StyleKey.DATA_CENTER));
            cell(r, 1, safe(e.getAccountUsername()), styles.get(StyleKey.DATA)); // account user
            cell(r, 2, safe(e.getCode()), styles.get(StyleKey.DATA_CENTER));
            cell(r, 3, safe(e.getFullName()), styles.get(StyleKey.DATA));
            cell(r, 4, safe(e.getPosition()), styles.get(StyleKey.DATA));
            cell(r, 5, safe(e.getDepartment()), styles.get(StyleKey.DATA));
            cell(r, 6, safe(e.getPhone()), styles.get(StyleKey.DATA_CENTER));
            cell(r, 7, safe(e.getEmail()), styles.get(StyleKey.DATA));

            cell(r, 8,
                    e.isActive() ? "ACTIVE" : "INACTIVE",
                    e.isActive() ? styles.get(StyleKey.BADGE_ACTIVE) : styles.get(StyleKey.BADGE_INACTIVE)
            );

            cell(r, 9, e.getHiredDate(), styles.get(StyleKey.DATA_CENTER));
        }
    }
}
