package org.example.controller;

import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.service.BatchService;

import java.time.LocalDate;
import java.util.List;

public class BatchController {
    private final BatchService batchService ;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }
    /**
     * Lấy toàn bộ lô hàng
     * */
    public List<BatchResponse> getALl(){
        return batchService.getAll();
    }
    /**
     * Lấy danh sách lô hàng theo sản phẩm
     * */
    public List<BatchResponse> getByProduct(Long productId){
        return batchService.getByProduct(productId);
    }
    /**
     *Lấy chi tiết 1 lô theo ID
     **/
    public BatchResponse getDetail(Long batchId){
        return batchService.getDetail(batchId);
    }
    public long create(CreateBatchRequest request){
        return batchService.create(request);
    }
    /**
     * Cập nhật thông tin lô (có kiểm soát).
     */
    public void update(Long batchId, UpdateBatchRequest request) {
        batchService.update(batchId, request);
    }
    /**
     * Ngưng sử dụng lô hàng (soft delete).
     */
    public void disable(Long batchId) {
        batchService.disable(batchId);
    }

    public List<BatchResponse> getExpiringBetween(LocalDate fromDate, LocalDate toDate) {
        return batchService.getExpiringBetween(fromDate, toDate);
    }
}
