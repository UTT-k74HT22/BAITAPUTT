package org.example.controller;

import org.example.dto.response.ReportSummaryResponse;
import org.example.service.ReportSummaryService;

public class ReportSummaryController {

    private final ReportSummaryService service;

    public ReportSummaryController(ReportSummaryService service) {
        this.service = service;
    }

    public ReportSummaryResponse getSummary() {
        return service.getSummary();
    }
}
