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
     * @param m
     * @return
     */
    long insert(Manufacturer m);


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

    /**
     *
     * @param code
     * @return
     */
    boolean ExistsByCode(String code);
}
