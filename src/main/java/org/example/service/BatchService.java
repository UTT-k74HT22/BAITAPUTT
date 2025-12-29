package org.example.service;

import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;

import java.time.LocalDate;
import java.util.List;

public interface BatchService {
    /**
     * Lấy toàn bộ lô hàng
     * */
    List<BatchResponse> getAll();
    /**
     * Lấy danh sách lô theo sản phẩm
     * */
    List<BatchResponse> getByProduct(Long productId);
    /**
     * Lấy chi tiết một lô
     * */
    BatchResponse getDetail(Long batchId);
    /**
     * Tạo mới một lô hàng
     * @param request
     * @return
     * */
    Long create(CreateBatchRequest request);
    /**
     *
     * */
    Long update(Long batchId, UpdateBatchRequest updateBatchRequest);
    /**
     * Ngưng sử dụng một lô hàng
     * */
    void disable(Long batchId);

    /**
     * Lấy các lô hàng sắp hết hạn trong N ngày tới.
     * */
    List<BatchResponse> getExpiringBetween(LocalDate fromDate, LocalDate toDate);
}
