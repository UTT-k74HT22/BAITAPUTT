package org.example.controller;

import org.example.dto.DashboardDTO;
import org.example.service.DashboardService;
import org.example.view.Dashboard;

import javax.swing.*;

public class DashboardController {

    private final DashboardService dashboardService = new DashboardService();
    private final Dashboard view;

    public DashboardController(Dashboard view) {
        this.view = view;
        loadData();
    }

    public void loadData() {
        // show loading state
        SwingUtilities.invokeLater(() -> view.setLoading(true));

        new SwingWorker<DashboardDTO, Void>() {
            @Override
            protected DashboardDTO doInBackground() throws Exception {
                return dashboardService.getDashboardSummary();
            }

            @Override
            protected void done() {
                try {
                    DashboardDTO dto = get();
                    view.render(dto);
                } catch (Exception e) {
                    // in case of error, render null (view should handle) and optionally show message
                    view.render(null);
                    JOptionPane.showMessageDialog(null,
                            "Không thể tải dữ liệu dashboard: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    view.setLoading(false);
                }
            }
        }.execute();
    }
}
