package org.example.controller;

import java.util.List;
import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.service.BatchService;
import org.example.service.ProductService;
import org.example.service.impl.BatchServiceImpl;
import org.example.service.impl.ProductServiceImpl;

public class BatchController {
    private final BatchService batchService ;

    private final ProductService productService = new ProductServiceImpl();

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    public BatchController() {
        this.batchService = new BatchServiceImpl();
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

    public List<ProductResponse> getAllProducts() {
        return productService.getAll();
    }
}
