package com.scc.ui;

import com.scc.dao.RentalDao;
import com.scc.model.user;

import java.util.Scanner;

public class UserMenu {
    private final user currentUser;
    private final RentalDao rentalDao;
    private final Scanner scanner;

    public UserMenu(user currentUser, RentalDao rentalDao, Scanner scanner) {
        this.currentUser = currentUser;
        this.rentalDao = rentalDao;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("       🎵 USER DASHBOARD [" + currentUser.username().toUpperCase() + "]       ");
            System.out.println("==================================================");
            System.out.println(" 1. Rent a Karaoke Unit");
            System.out.println(" 2. Logout");
            System.out.println("==================================================");
            System.out.print("Select an option (1-2): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleNewRental();
                    break;
                case "2":
                    System.out.println("\nLogging out...");
                    return;
                default:
                    System.out.println("\n[!] Invalid option! Choose 1 or 2.");
            }
        }
    }

    private void handleNewRental() {
        System.out.println("\n--- NEW KARAOKE RENTAL FORM ---");

        System.out.print("Enter Renter Name (Date Rented): ");
        String renterName = scanner.nextLine().trim();
        if (renterName.isEmpty()) {
            System.out.println("[!] Name cannot be empty.");
            return;
        }

        System.out.println("\nAvailable Karaoke Units:");
        System.out.println("1. Platinum Junior Lite (₱500.00 / day)");
        System.out.println("2. Megapro Plus Pro (₱800.00 / day)");
        System.out.println("3. Grand Evolution HD (₱1,200.00 / day)");
        System.out.print("Select Unit (1-3): ");

        String unitChoice = scanner.nextLine();
        String unitName;
        double pricePerDay;

        switch (unitChoice) {
            case "1":
                unitName = "Platinum Junior Lite";
                pricePerDay = 500.00;
                break;
            case "2":
                unitName = "Megapro Plus Pro";
                pricePerDay = 800.00;
                break;
            case "3":
                unitName = "Grand Evolution HD";
                pricePerDay = 1200.00;
                break;
            default:
                System.out.println("[!] Invalid unit selection. Transaction cancelled.");
                return;
        }

        int days = readIntInput("Enter number of rental days: ");
        if (days <= 0) {
            System.out.println("[!] Days must be greater than 0.");
            return;
        }

        double totalRentalDue = pricePerDay * days;

        System.out.println("\n----------------- RENTAL SUMMARY -----------------");
        System.out.println(" Renter Name (Kanos a): " + renterName);
        System.out.println(" Karaoke Unit         : " + unitName);
        System.out.println(" Price per Day        : ₱" + pricePerDay);
        System.out.println(" Rental Duration      : " + days + " day(s)");
        System.out.println(" --------------------------------------------------");
        System.out.printf(" TOTAL RENTAL DUE     : ₱%.2f\n", totalRentalDue);
        System.out.println("--------------------------------------------------\n");

        System.out.print("Confirm rental transaction? (y/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            boolean success = rentalDao.addRental(renterName, unitName, pricePerDay, days);
            if (success) {
                System.out.println("\n[✔] Karaoke successfully rented and recorded in SQLite database!");
            }
        } else {
            System.out.println("\n[!] Rental transaction cancelled.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int val = Integer.parseInt(input.trim());
                if (val > 0) return val;
                System.out.println("[!] Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid number.");
            }
        }
    }
}