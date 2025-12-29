package org.example.controller;

import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;
import org.example.service.WarehouseService;
import org.example.service.impl.WarehouseServiceImpl;

import java.util.List;

public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController() {
        this.warehouseService = new WarehouseServiceImpl();
    }

    /**
     * XEM DANH SÁCH KHO
     */
    public List<WarehouseResponse> getAllWarehouses() {
        System.out.println("[Controller] getAllWarehouses");
        return warehouseService.getAllWarehouses();
    }

    /**
     * THÊM KHO
     */
    public void createWarehouse(CreateWarehouseRequest request) {
        System.out.println("[Controller] createWarehouse");
        warehouseService.create(request);
    }
    /**
     * Lấy thông tin kho theo code
     */
    public WarehouseResponse getWarehouseByCode(String code) {
        if (code == null) return null;

        List<WarehouseResponse> list = warehouseService.getAllWarehouses();
        if (list == null) return null;

        for (WarehouseResponse w : list) {
            if (code.equals(w.getCode())) {
                return w;
            }
        }
        return null; // không tìm thấy
    }

    /**
     * SỬA KHO
     */
    public void updateWarehouse(String code, CreateWarehouseRequest request) {
        System.out.println("[Controller] updateWarehouse: " + code);
        warehouseService.update(code, request);
    }

    /**
     * XÓA MỀM KHO
     */
    public void deleteWarehouse(String code) {
        System.out.println("[Controller] deleteWarehouse: " + code);
        warehouseService.delete(code);
    }
}
