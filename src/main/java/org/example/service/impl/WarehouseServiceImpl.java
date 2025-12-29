package org.example.service.impl;

import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;
import org.example.repository.WarehouseRepository;
import org.example.repository.impl.WarehouseRepositoryImpl;
import org.example.service.WarehouseService;

import java.util.List;

public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl() {
        this.warehouseRepository = new WarehouseRepositoryImpl();
    }

    @Override
    public void create(CreateWarehouseRequest request) {
        warehouseRepository.insert(request);
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.getAll();
    }

    @Override
    public WarehouseResponse getWarehouseByCode(String code) {
        return warehouseRepository.getByCode(code).orElse(null);
    }

    @Override
    public void update(String code, CreateWarehouseRequest request) {
        warehouseRepository.updateByCode(code, request);
    }

    @Override
    public void delete(String code) {
        warehouseRepository.softDelete(code);
    }
}
