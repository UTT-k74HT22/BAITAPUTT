package org.example.controller;

import org.example.dto.response.ReportSummaryResponse;
import org.example.service.ReportSummaryService;
import org.example.service.impl.ReportSummaryServiceImpl;

public class ReportSummaryController {

    private final ReportSummaryService service;

    public ReportSummaryController() {
        this.service = new ReportSummaryServiceImpl();
    }

    public ReportSummaryResponse getSummary() {
        return service.getSummary();
    }
}
