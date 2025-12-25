package org.example.service.impl;

import org.example.repository.impl.ManufacturerReponsitoryImpl;
import org.example.service.ManufacturerService;
import org.example.entity.Manufacturer;
import org.example.repository.ManufacturerReponsitory;

import java.util.List;

public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerReponsitory manufacturerReponsitory;

    public ManufacturerServiceImpl() {
        this.manufacturerReponsitory = new ManufacturerReponsitoryImpl();
    }

    @Override
    public List<Manufacturer> findAll() {
        return manufacturerReponsitory.findAll();
    }

    @Override
    public Manufacturer findById(Long id) {
        return manufacturerReponsitory.findById(id);
    }

    @Override
    public Long create(Manufacturer manufacturer) {
        return manufacturerReponsitory.insert(manufacturer);
    }

    @Override
    public void update(Manufacturer manufacturer) {
        manufacturerReponsitory.update(manufacturer);
    }

    @Override
    public void delete(Long id) {
        manufacturerReponsitory.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return manufacturerReponsitory.ExistsByCode(code);
    }
}
