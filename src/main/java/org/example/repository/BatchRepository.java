package org.example.repository;

import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BatchRepository {
    /**
     * Lấy toàn bộ lô hàng
     * */
    List<BatchResponse> findAll();

    BatchResponse findById(Long id);

    /**
     * Lấy danh sách lô hàng theo sản phẩm
     * */
    List<BatchResponse> findByProduct(Long productId);
    /**
     * Lấy chi tiết 1 lô hàng
     * */
    Optional<BatchResponse> findDetail(Long batchId);
    /**
     * Tạo mới lô hàng
     * */
    long insert(CreateBatchRequest request);
    /**
     * Cập nhật thông tin lô hàng
     * */
    void update(Long batchId, UpdateBatchRequest request);
    /**
     * Ngưng sử dụng lô hàng
     * */
    void disable(Long batchId);
    /**
     * Lấy các lô hàng sắp hết hạn trong N ngày tới.
     *
     * @param fromDate ngày bắt đầu (thường là today)
     * @param toDate   ngày kết thúc (today + N ngày)
     */
    List<BatchResponse> findExpiringBetween(LocalDate fromDate, LocalDate toDate);
}
