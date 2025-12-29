package org.example.repository;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.ProfileResponse;
import org.example.entity.UserProfile;

import java.sql.Connection;
import java.util.Optional;

public interface UserProfileRepository {
    /**
     * Chèn hồ sơ người dùng mới và trả về ID đã chèn.
     * @param conn kết nối cơ sở dữ liệu
     * @param accountId ID tài khoản liên kết
     * @param code mã hồ sơ
     * @param fullName họ và tên đầy đủ
     * @param phone số điện thoại
     * @param email email
     * @param address địa chỉ
     * @return ID của hồ sơ người dùng đã chèn
     */
    long insert(Connection conn, long accountId, String code, String fullName,
                String phone, String email, String address);

    /**
     * Tạo mã hồ sơ người dùng tiếp theo dựa trên tiền tố cho trước.
     * @param conn kết nối cơ sở dữ liệu
     * @param prefix tiền tố mã hồ sơ
     * @return mã hồ sơ người dùng tiếp theo
     */
    String generateNextCode(Connection conn, String prefix);

    /**
     * Tìm ID hồ sơ người dùng dựa trên mã hồ sơ.
     * @param code mã hồ sơ
     * @return Optional chứa ID hồ sơ nếu tìm thấy, ngược lại là rỗng
     */
    Optional<Long> findIdByCode(String code);

    /**
     * Cập nhật thông tin cơ bản của hồ sơ người dùng dựa trên mã hồ sơ.
     * @param code mã hồ sơ
     * @param request đối tượng yêu cầu chứa thông tin cập nhật
     */
    void updateBasicByCode(String code, UpdateProfileEmployeeRequest request);

    /**
     * Cập nhật chi tiết hồ sơ người dùng dựa trên mã hồ sơ.
     * @param code mã hồ sơ
     * @param request đối tượng yêu cầu chứa thông tin cập nhật chi tiết
     */
    void updateDetailByCode(String code, UpdateEmployeeProfileDetailRequest request);

    /**
     *
     * @param accountId
     * @return
     */
    Optional<UserProfile> findByAccountId(long accountId);

    /**
     *
     * @param username
     * @return
     */
    Optional<ProfileResponse> findProfileByUsername(String username);
}
