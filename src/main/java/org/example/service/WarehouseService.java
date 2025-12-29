package org.example.service;

import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    /**
     * Lấy tất cả kho
     */
    List<WarehouseResponse> getAllWarehouses();

    /**
     * Lấy kho theo mã
     */
    WarehouseResponse getWarehouseByCode(String code);

    /**
     * Thêm kho mới
     */
    void create(CreateWarehouseRequest request);

    /**
     * Cập nhật kho
     */
    void update(String code, CreateWarehouseRequest request);

    /**
     * Xóa mềm kho
     */
    void delete(String code);
}
