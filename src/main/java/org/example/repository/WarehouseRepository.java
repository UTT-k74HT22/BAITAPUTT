package org.example.repository;

import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;
import org.example.entity.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {

    void insert(CreateWarehouseRequest request);

    List<WarehouseResponse> getAll();

    Optional<WarehouseResponse> getByCode(String code);

    void updateByCode(String code, CreateWarehouseRequest request);

    void softDelete(String code);

    Optional<Warehouse> findById(Long id);
}
