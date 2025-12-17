package org.example.repository;

import java.sql.Connection;

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
}
