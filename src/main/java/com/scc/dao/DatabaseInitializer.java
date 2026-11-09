package com.scc.dao;

import com.scc.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "roles TEXT NOT NULL, " +
                "status TEXT DEFAULT 'APPROVED'" +
                ");";

        String rentalsTable = "CREATE TABLE IF NOT EXISTS rentals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "kanos_a_siya_ni_rent TEXT NOT NULL, " +
                "karaoke_unit TEXT NOT NULL, " +
                "price_per_day REAL NOT NULL, " +
                "days INTEGER NOT NULL, " +
                "rental_due REAL NOT NULL, " +
                "date_rented TEXT DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(rentalsTable);

            // Seed Default Admin if none exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE roles = 'ADMIN'";
            var rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (username, password, roles, status) VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";
                stmt.executeUpdate(insertAdmin);
            }
            System.out.println("==================================================");
            System.out.println(" [✔] Database & Tables Initialized Successfully! ");
            System.out.println("==================================================");
        } catch (SQLException e) {
            System.out.println("[!] Database Initialization Error: " + e.getMessage());
        }
    }
}