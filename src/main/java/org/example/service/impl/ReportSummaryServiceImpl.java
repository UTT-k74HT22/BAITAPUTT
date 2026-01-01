package org.example.service.impl;

import org.example.dto.response.ReportSummaryResponse;
import org.example.repository.ReportSummaryRepository;
import org.example.repository.impl.ReportSummaryRepositoryImpl;
import org.example.service.ReportSummaryService;

import java.math.BigDecimal;

public class ReportSummaryServiceImpl implements ReportSummaryService {

    private final ReportSummaryRepository repository;

    public ReportSummaryServiceImpl() {
        this.repository = new ReportSummaryRepositoryImpl();
    }

    @Override
    public ReportSummaryResponse getSummary() {
        long products = repository.countProducts();
        long categories = repository.countCategories();
        long warehouses = repository.countWarehouses();
        long stock = repository.sumInventoryStock();
        long expired = repository.countExpiredBatches();
        BigDecimal revenue = repository.sumRevenue();

        return new ReportSummaryResponse(
                products,
                categories,
                warehouses,
                stock,
                expired,
                revenue
        );
    }
}

