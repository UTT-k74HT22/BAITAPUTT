package org.example.service.impl;

import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.repository.BatchRepository;
import org.example.repository.impl.BatchRepositoryImpl;
import org.example.service.BatchService;

import java.time.LocalDate;
import java.util.List;

public class BatchServiceImpl implements BatchService {
    private final BatchRepository batchRepository;

    public BatchServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public BatchServiceImpl() {
        this.batchRepository = new BatchRepositoryImpl();
    }
    /**
     * Lấy danh sách tất cả lô hàng.
     */
    @Override
    public List<BatchResponse> getAll() {
        return batchRepository.findAll();
    }
    /**
     * Lấy danh sách lô hàng theo sản phẩm.
     */
    @Override
    public List<BatchResponse> getByProduct(Long productId) {
        return batchRepository.findByProduct(productId);
    }
    /**
     * Lấy chi tiết lô hàng.
     */
    @Override
    public BatchResponse getDetail(Long batchId) {
        return batchRepository.findDetail(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));
    }
    /**
     * Thêm mới lô hàng.
     */
    @Override
    public Long create(CreateBatchRequest request) {
        return batchRepository.insert(request);
    }
    /**
     * Cập nhật lô hàng.
     */
    @Override
    public Long update(Long batchId, UpdateBatchRequest updateBatchRequest) {
        batchRepository.update(batchId, updateBatchRequest);
        return batchId;
    }
    /**
     * Ngưng sử dụng lô hàng.
     */
    @Override
    public void disable(Long batchId) {
        batchRepository.disable(batchId);
    }

    @Override
    public List<BatchResponse> getExpiringBetween(LocalDate fromDate, LocalDate toDate) {
        return batchRepository.findExpiringBetween(fromDate, toDate);
    }
}
