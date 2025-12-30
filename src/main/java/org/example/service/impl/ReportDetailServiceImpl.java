package org.example.service.impl;

import org.example.dto.response.ReportDetailResponse;
import org.example.repository.ReportDetailRepository;
import org.example.repository.impl.ReportDetailRepositoryImpl;
import org.example.service.ReportDetailService;

import java.util.Date;
import java.util.List;

/**
 * Service implementation báo cáo tồn kho + xuất nhập
 */
public class ReportDetailServiceImpl implements ReportDetailService {

    private final ReportDetailRepository repository = new ReportDetailRepositoryImpl();

    @Override
    public List<ReportDetailResponse> getReport(String warehouseName, String productCode, Date fromDate, Date toDate) {
        return repository.getReportDetails(warehouseName, productCode, fromDate, toDate);
    }
}
