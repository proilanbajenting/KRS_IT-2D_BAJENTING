package com.scc.dao;

import com.scc.model.Rental;
import com.scc.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDao {

    public boolean addRental(String renterName, String unit, double pricePerDay, int days) {
        double totalDue = pricePerDay * days;
        String sql = "INSERT INTO rentals (kanos_a_siya_ni_rent, karaoke_unit, price_per_day, days, rental_due) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, renterName);
            pstmt.setString(2, unit);
            pstmt.setDouble(3, pricePerDay);
            pstmt.setInt(4, days);
            pstmt.setDouble(5, totalDue);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("[!] Error saving rental: " + e.getMessage());
            return false;
        }
    }

    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, kanos_a_siya_ni_rent, karaoke_unit, price_per_day, days, rental_due, date_rented FROM rentals";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rentals.add(new Rental(
                        rs.getInt("id"),
                        rs.getString("kanos_a_siya_ni_rent"),
                        rs.getString("karaoke_unit"),
                        rs.getDouble("price_per_day"),
                        rs.getInt("days"),
                        rs.getDouble("rental_due"),
                        rs.getString("date_rented")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[!] Error fetching rentals: " + e.getMessage());
        }
        return rentals;
    }
}