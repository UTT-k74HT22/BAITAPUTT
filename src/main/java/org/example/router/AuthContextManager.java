    package org.example.router;

    import org.example.entity.enums.Role;

    public class AuthContextManager {

        private static Long userId;
        private static Role role;

        public static void setUser(Long id, Role userRole) {
            userId = id;
            role = userRole;
        }

        public static Long getUserId() {
            return userId;
        }

        public static Role getRole() {
            return role;
        }

        public static void clear() {
            userId = null;
            role = null;
        }

        public static boolean hasRole(Role r) {
            return role == r;
        }

        public static boolean isCustomer() {
            return role == Role.CUSTOMER;
        }

        public static boolean isEmployee() {
            return role == Role.EMPLOYEE;
        }

        public static boolean isAdmin() {
            return role == Role.ADMIN;
        }
    }
