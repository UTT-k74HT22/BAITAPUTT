package org.example.service;

import org.example.dto.response.ReportDetailResponse;
import java.util.Date;
import java.util.List;

/**
 * Service báo cáo tồn kho
 */
public interface ReportDetailService {
    List<ReportDetailResponse> getReport(String warehouseName, String productCode, Date fromDate, Date toDate);
}
