package org.example.controller;

import org.example.dto.request.CreateManufacturerRequest;
import org.example.entity.Manufacturer;
import org.example.service.ManufacturerService;
import java.awt.*;
import java.util.List;

public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    public List<Manufacturer> getAll() {
        return manufacturerService.findAll();
    }

    public Manufacturer findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return manufacturerService.findById(id);
    }

    public void create(Manufacturer manufacturer) {
        manufacturerService.create(manufacturer);
    }

    public void update(Manufacturer manufacturer) {
        manufacturerService.update(manufacturer);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        manufacturerService.delete(id);
    }
}
