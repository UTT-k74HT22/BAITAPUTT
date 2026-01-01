package org.example.dto.response;
import java.math.BigDecimal;


public class ReportSummaryResponse {
    private long totalProducts;
    private long totalCategories;
    private long totalWarehouses;
    private long totalStock;
    private long expiredBatches;
    private BigDecimal totalRevenue;

    public ReportSummaryResponse(
            long totalProducts,
            long totalCategories,
            long totalWarehouses,
            long totalStock,
            long expiredBatches,
            BigDecimal totalRevenue
    ) {
        this.totalProducts = totalProducts;
        this.totalCategories = totalCategories;
        this.totalWarehouses = totalWarehouses;
        this.totalStock = totalStock;
        this.expiredBatches = expiredBatches;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalProducts() { return totalProducts; }
    public long getTotalCategories() { return totalCategories; }
    public long getTotalWarehouses() { return totalWarehouses; }
    public long getTotalStock() { return totalStock; }
    public long getExpiredBatches() { return expiredBatches; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
