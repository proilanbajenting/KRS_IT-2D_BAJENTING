package com.scc.dao;

import com.scc.model.user;
import com.scc.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public UserDao() {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE roles = 'ADMIN'";
        String insertSql = "INSERT INTO users (username, password, roles, status) VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.out.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("\n[✔] Registration successful! Waiting for admin approval.");
            return true;
        } catch (SQLException e) {
            System.out.println("\n[!] Registration failed (Username might already exist): " + e.getMessage());
            return false;
        }
    }

    public boolean addUserByAdmin(String username, String password, String role, String status) {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            pstmt.setString(4, status.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("\n[✔] User added successfully by Admin!");
            return true;
        } catch (SQLException e) {
            System.out.println("\n[!] Failed to add user: " + e.getMessage());
            return false;
        }
    }

    public user login(String username, String password) {
        String sql = "SELECT id, username, roles, status FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("PENDING".equalsIgnoreCase(status)) {
                    System.out.println("\n[!] Account is pending approval by admin.");
                    return null;
                }
                return new user(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        status
                );
            } else {
                System.out.println("\n[!] Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public List<user> getAllUsers() {
        List<user> list = new ArrayList<>();
        String sql = "SELECT id, username, roles, status FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new user(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return list;
    }

    public List<user> getPendingUsers() {
        List<user> list = new ArrayList<>();
        String sql = "SELECT id, username, roles, status FROM users WHERE status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new user(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending users: " + e.getMessage());
        }
        return list;
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✔] User ID " + userId + " approved successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRole.toUpperCase());
            pstmt.setString(2, newStatus.toUpperCase());
            pstmt.setInt(3, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✔] User ID " + userId + " updated successfully!");
                return true;
            } else {
                System.out.println("\n[!] User ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✔] User ID " + userId + " deleted successfully!");
                return true;
            } else {
                System.out.println("\n[!] User ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }
}