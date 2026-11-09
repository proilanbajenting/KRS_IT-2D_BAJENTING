package com.scc.ui;

import com.scc.dao.RentalDao;
import com.scc.dao.UserDao;
import com.scc.model.Rental;
import com.scc.model.user;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final user currentUser;
    private final UserDao userDao;
    private final RentalDao rentalDao;
    private final Scanner scanner;

    public AdminMenu(user currentUser, UserDao userDao, RentalDao rentalDao, Scanner scanner) {
        this.currentUser = currentUser;
        this.userDao = userDao;
        this.rentalDao = rentalDao;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("                    ADMIN DASHBOARD                    ");
            System.out.println("=======================================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role & Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. View All Karaoke Rentals");
            System.out.println("8. Logout");
            System.out.print("Choose an option (1-8): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingUsers();
                    break;
                case "3":
                    approveUser();
                    break;
                case "4":
                    addNewUser();
                    break;
                case "5":
                    updateUserRoleAndStatus();
                    break;
                case "6":
                    deleteUser();
                    break;
                case "7":
                    viewAllRentals();
                    break;
                case "8":
                    System.out.println("\nLogging out...");
                    return;
                default:
                    System.out.println("\n[!] Invalid option! Please choose between 1 and 8.");
            }
        }
    }

    private void viewAllUsers() {
        List<user> userList = userDao.getAllUsers();
        System.out.println("\n=======================================================");
        System.out.println("               *** ALL REGISTERED USER ***");
        System.out.println("=======================================================");
        System.out.println(" ID | Username        | Role       | Status            ");
        System.out.println("-------------------------------------------------------");
        if (userList.isEmpty()) {
            System.out.println(" No users found.");
        } else {
            for (user u : userList) {
                System.out.printf(" %-2d | %-15s | %-10s | %-15s \n",
                        u.id(), u.username(), u.roles(), u.status());
            }
        }
        System.out.println("=======================================================");
    }

    private void viewPendingUsers() {
        List<user> pendingList = userDao.getPendingUsers();
        System.out.println("\n=======================================================");
        System.out.println("             *** PENDING USER APPROVALS ***");
        System.out.println("=======================================================");
        System.out.println(" ID | Username        | Role       | Status            ");
        System.out.println("-------------------------------------------------------");
        if (pendingList.isEmpty()) {
            System.out.println(" No pending accounts found.");
        } else {
            for (user u : pendingList) {
                System.out.printf(" %-2d | %-15s | %-10s | %-15s \n",
                        u.id(), u.username(), u.roles(), u.status());
            }
        }
        System.out.println("=======================================================");
    }

    private void approveUser() {
        viewPendingUsers();
        List<user> pendingList = userDao.getPendingUsers();
        if (pendingList.isEmpty()) return;

        int id = readIntInput("Enter User ID to approve (0 to cancel): ");
        if (id == 0) return;

        boolean exists = pendingList.stream().anyMatch(u -> u.id() == id);
        if (exists) {
            userDao.approveUser(id);
        } else {
            System.out.println("[!] User ID not found in pending list.");
        }
    }

    private void addNewUser() {
        System.out.println("\n--- ADD NEW USER ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        String role = readValidRole();
        String status = readValidStatus();

        userDao.addUserByAdmin(username, password, role, status);
    }

    private void updateUserRoleAndStatus() {
        viewAllUsers();
        int targetId = readIntInput("Enter Target User ID to update (0 to cancel): ");
        if (targetId == 0) return;

        if (targetId == currentUser.id()) {
            System.out.println("[!] WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        userDao.updateUserRoleAndStatus(targetId, newRole, newStatus);
    }

    private void deleteUser() {
        viewAllUsers();
        int targetId = readIntInput("Enter User ID to delete (0 to cancel): ");
        if (targetId == 0) return;

        if (targetId == currentUser.id()) {
            System.out.println("[!] WARNING: You cannot delete your own active admin account.");
            return;
        }

        userDao.deleteUser(targetId);
    }

    private void viewAllRentals() {
        List<Rental> rentals = rentalDao.getAllRentals();
        System.out.println("\n=================================================================================");
        System.out.println("                     *** KARAOKE RENTALS RECORDS ***");
        System.out.println("=================================================================================");
        System.out.println(" ID | Renter Name (Rental Date) | Karaoke Unit      | Price/Day | Days | Rental Due");
        System.out.println("---------------------------------------------------------------------------------");
        if (rentals.isEmpty()) {
            System.out.println(" No rental transactions recorded yet.");
        } else {
            for (Rental r : rentals) {
                System.out.printf(" %-2d | %-21s | %-17s | ₱%-8.2f | %-4d | ₱%.2f\n",
                        r.id(), r.renterName(), r.karaokeUnit(), r.pricePerDay(), r.days(), r.totalDue());
            }
        }
        System.out.println("=================================================================================");
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter role (CUSTOMER, STAFF, ADMIN): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("CUSTOMER") || role.equals("STAFF") || role.equals("ADMIN")) {
                return role;
            }
            System.out.println("[!] Invalid role. Please try again.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter status (APPROVED, PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("[!] Invalid status. Please try again.");
        }
    }
}