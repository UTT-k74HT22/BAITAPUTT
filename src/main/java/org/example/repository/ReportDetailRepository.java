package org.example.repository;

import org.example.dto.response.ReportDetailResponse;
import java.util.Date;
import java.util.List;

/**
 * Interface Repository cho báo cáo tồn kho chi tiết
 */
public interface ReportDetailRepository {

    /**
     * Lấy danh sách chi tiết tồn kho + xuất nhập theo filter
     * @param warehouseName tên kho (nullable để lấy tất cả)
     * @param productCode mã sản phẩm (nullable để lấy tất cả)
     * @param fromDate từ ngày (nullable)
     * @param toDate đến ngày (nullable)
     * @return danh sách ReportDetailResponse
     */
    List<ReportDetailResponse> getReportDetails(String warehouseName, String productCode, Date fromDate, Date toDate);
}


