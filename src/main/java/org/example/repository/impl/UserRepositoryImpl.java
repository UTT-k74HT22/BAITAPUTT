package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.User;
import org.example.entity.enums.Gender;
import org.example.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    /**
     * Lấy danh sách User
     *
     * @return Trả về tất cả user
     */
    @Override
    public List<User> findAll() {

        List<User> lists = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = DatabaseConfig.getConnection()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    lists.add(mapRow(rs));
                }
                return lists;


        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách Users: ", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setGender(Gender.valueOf(rs.getString("gender")));
        return user;
    }
}
