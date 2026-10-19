package com.mycompany.chatapppart2;

import java.util.Scanner;

/** Entry point: Part 1 registration and login, then Part 2 QuickChat. */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // ----- Registration -----
        System.out.println("REGISTRATION");
        System.out.print("Enter username (must contain _ and be no more than 5 characters): ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password (8+ characters, capital letter, number, special character): ");
        String password = scanner.nextLine();
        System.out.print("Enter cell phone number (e.g. +27838968976): ");
        String phone = scanner.nextLine().trim();

        String registerMessage = login.registerUser(username, password, phone);
        System.out.println(registerMessage);

        // ----- Login -----
        System.out.println("\nLOGIN");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        System.out.println(login.returnLoginStatus(loginUsername, loginPassword));

        // ----- Part 2: only a logged-in user reaches QuickChat -----
        if (login.loginUser(loginUsername, loginPassword)) {
            QuickChat.run(scanner);
        }

        scanner.close();
    }
}
