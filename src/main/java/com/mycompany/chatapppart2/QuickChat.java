package com.mycompany.chatapppart2;

import java.util.Scanner;

/** QuickChat menu and message-entry flow (Part 2). Called from Main after a successful login. */
public class QuickChat {

    /** Runs the QuickChat menu. Only call this when login succeeded. */
    public static void run(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");
        int limit = readInt(scanner, "Enter the number of messages you wish to send: ");
        int processed = 0;
        int choice = 0;

        // while loop: keeps the menu running until the user chooses Quit
        while (choice != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            choice = readInt(scanner, "Enter your choice: ");

            if (choice == 1) {
                if (processed >= limit) {
                    System.out.println("Message limit reached.");
                } else {
                    processed = enterMessages(scanner, processed, limit);
                }
            } else if (choice == 2) {
                System.out.println("Coming Soon.");
            } else if (choice == 3) {
                System.out.println("Goodbye.");
            } else {
                System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }

    /** for loop: allows only the user-defined number of messages. */
    private static int enterMessages(Scanner scanner, int processed, int limit) {
        for (int i = processed; i < limit; i++) {
            System.out.println("Creating message " + (i + 1) + " of " + limit + " ...");

            String recipient = askRecipient(scanner);
            String text = askText(scanner);
            Message msg = new Message(i + 1, recipient, text);

            System.out.println("Message ID generated: " + msg.getMessageId());
            int action = readInt(scanner, "1) Send  2) Disregard  3) Store : ");
            System.out.println(msg.SentMessage(action));

            if (action == 1) {
                System.out.println(msg.getDetails());
            } else if (action == 2) {
                readInt(scanner, "Enter 0 to confirm: ");
            }
            processed++;
        }
        System.out.println("Message limit reached.");
        System.out.println("Total messages successfully sent: " + Message.returnTotalMessagess());
        return processed;
    }

    private static String askRecipient(Scanner scanner) {
        while (true) {
            System.out.print("Enter recipient cell number (with international code, e.g. +27...): ");
            String number = scanner.nextLine().trim();
            Message check = new Message("0000000000", 0, number, "check");
            String result = check.checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) {
                return number;
            }
        }
    }

    private static String askText(Scanner scanner) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            Message check = new Message("0000000000", 0, "+000000000000", text);
            String result = check.checkMessageLength();
            System.out.println(result);
            if (text.length() <= Message.MAX_LENGTH) {
                return text;
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
