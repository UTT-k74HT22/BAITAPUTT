package org.example.service;

import org.example.dto.DashboardDTO;
import org.example.repository.DashboardRepository;

public class DashboardService {

    private final DashboardRepository dashboardRepository = new DashboardRepository();

    public DashboardDTO getDashboardSummary() {
        return dashboardRepository.loadDashboard();
    }
}
