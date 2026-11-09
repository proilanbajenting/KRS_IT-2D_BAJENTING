package com.scc.model;

public record Rental(int id, String renterName, String karaokeUnit, double pricePerDay, int days, double totalDue, String dateRented) {
}