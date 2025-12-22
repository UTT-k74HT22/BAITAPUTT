package org.example.repository;

import org.example.entity.Manufacturer;
import java.sql.Connection;
import java.util.List;

public interface ManufacturerReponsitory {

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
     * @param conn
     * @param m
     * @return
     */
    long insert(Connection conn, Manufacturer m);

    /**
     *
     * @param m
     */
    void update(Manufacturer m);

    /**
     *
     * @param id
     */
    void deleteById(Long id);
}
