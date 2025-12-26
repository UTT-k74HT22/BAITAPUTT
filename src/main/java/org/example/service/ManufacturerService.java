package org.example.service;

import org.example.entity.Manufacturer;

import java.util.List;

public interface ManufacturerService {

    /**
     *
     * @return
     */
    List<Manufacturer> findAll();

    /**
     *
     * @param id
     * @return
     */
    Manufacturer findById(Long id);

    /**
     *
     * @param manufacturer
     * @return
     */
    Long create(Manufacturer manufacturer);

    /**
     *
     * @param manufacturer
     */
    void update(Manufacturer manufacturer);

    /**
     *
     * @param id
     */
    void delete(Long id);

    /**
     *
     * @param code
     * @return
     */
    boolean existsByCode(String code);
}
