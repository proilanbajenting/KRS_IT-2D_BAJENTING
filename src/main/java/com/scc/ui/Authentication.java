package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.user;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("     KARAOKE RENTAL SYSTEM (IT2D)      ");
            System.out.println("=======================================");
            System.out.println("1. Login");
            System.out.println("2. Register New Account");
            System.out.println("3. Exit");
            System.out.print("Select an option (1-3): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("[!] Invalid option! Please enter 1, 2, or 3.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        user user = userDao.login(username, password);
        if (user != null) {
            System.out.println("\nWelcome, " + user.username() + "!");
            if ("ADMIN".equalsIgnoreCase(user.roles())) {
                AdminMenu adminMenu = new AdminMenu(user, userDao, scanner);
                adminMenu.display();
            } else {
                System.out.println("Standard user dashboard not implemented yet.");
            }
        }
    }

    private void handleRegister() {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Choose Username: ");
        String username = scanner.nextLine();
        System.out.print("Choose Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Role (STUDENT, TEACHER, ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        userDao.registerUser(username, password, role);
    }
}