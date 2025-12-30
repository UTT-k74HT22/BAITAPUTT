package org.example.controller;

import org.example.dto.response.ReportDetailResponse;
import org.example.service.ReportDetailService;
import org.example.service.impl.ReportDetailServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * Controller báo cáo tồn kho
 */
public class ReportDetailController {

    private final ReportDetailService service = new ReportDetailServiceImpl();

    public List<ReportDetailResponse> getReport(String warehouseName, String productCode, Date fromDate, Date toDate) {
        return service.getReport(warehouseName, productCode, fromDate, toDate);
    }
}

