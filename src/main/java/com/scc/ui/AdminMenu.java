package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.user;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final user currentUser;
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(user currentUser, UserDao userDao, Scanner scanner) {
        this.currentUser = currentUser;
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("       ADMIN DASHBOARD (" + currentUser.username() + ")     ");
            System.out.println("=======================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role & Status");
            System.out.println("5. Logout");
            System.out.print("Select an option (1-5): ");

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
                    updateUserRoleAndStatus();
                    break;
                case "5":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("[!] Invalid option! Please choose between 1 and 5.");
            }
        }
    }

    private void viewAllUsers() {
        List<user> userList = userDao.getAllUsers();
        System.out.println("\n--- ALL REGISTERED USERS ---");
        if (userList.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (user u : userList) {
            System.out.println(u);
        }
    }

    private void viewPendingUsers() {
        List<user> pendingList = userDao.getPendingUsers();
        System.out.println("\n--- PENDING USER APPROVALS ---");
        if (pendingList.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }
        for (user u : pendingList) {
            System.out.println(u);
        }
    }

    private void approveUser() {
        viewPendingUsers();
        List<user> pendingList = userDao.getPendingUsers();
        if (pendingList.isEmpty()) return;

        int id = readIntInput("Enter User ID to approve (0 to cancel): ");
        if (id == 0) {
            System.out.println("Operation canceled.");
            return;
        }

        boolean exists = pendingList.stream().anyMatch(u -> u.id() == id);
        if (exists) {
            userDao.approveUser(id);
        } else {
            System.out.println("[!] User ID not found in pending list.");
        }
    }

    private void updateUserRoleAndStatus() {
        viewAllUsers();
        System.out.println("\n--- UPDATE USER ROLE & STATUS ---");
        int targetId = readIntInput("Enter Target User ID (0 to cancel): ");
        if (targetId == 0) {
            System.out.println("Operation canceled.");
            return;
        }

        // Self-Edit Restriction Check
        if (targetId == currentUser.id()) {
            System.out.println("[!] WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        userDao.updateUserRoleAndStatus(targetId, newRole, newStatus);
    }

    // --- Input Validation Wrappers ---

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid numeric number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (STUDENT, TEACHER, ADMIN): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("STUDENT") || role.equals("TEACHER") || role.equals("ADMIN")) {
                return role;
            }
            System.out.println("[!] Invalid role. Please try again.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("[!] Invalid status. Please try again.");
        }
    }
}