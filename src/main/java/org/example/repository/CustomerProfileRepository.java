package org.example.repository;

import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface CustomerProfileRepository {
    /**
     * Chèn hồ sơ khách hàng vào cơ sở dữ liệu.
     *
     * @param conn         kết nối cơ sở dữ liệu
     * @param profileId    ID hồ sơ
     * @param customerType loại khách hàng
     * @param taxCode      mã số thuế
     */
    void insert(Connection conn, long profileId, String customerType, String taxCode);

    /**
     *
     * @param keyword
     * @param type
     * @return
     */
    List<CustomerProfileResponse> search(String keyword, CustomerType type);

    /**
     *
     * @return
     */
    List<CustomerProfileResponse> findAll();

    /**
     *
     * @param code
     * @return
     */
    Optional<CustomerProfileResponse> findByCode(String code);

    /// @param code
    /// @param full_name
    /// @param phone
    /// @param customerType
    /// @param taxCode
    void update(String code, String full_name, String phone, CustomerType customerType, String taxCode);

    /**
     *
     * @param code
     */
    void delete(String code);
}
