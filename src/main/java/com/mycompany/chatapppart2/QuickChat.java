package com.mycompany.chatapppart2;

import java.util.Scanner;

/** 
 * QuickChat menu and message-entry flow (Part 2). Called from Main after a successful login. 
 * This class controls the main menu: 1)Send Message, 2)Show recently sent messages, 3)Quit.
 */
public class QuickChat {

    /** 
     * Runs the QuickChat menu. Only call this when login succeeded.
     * This is the entry point for Part 2 of the POE.
     * @param scanner Shared Scanner object from Main.java to read user input
     */
    public static void run(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat."); //Welcome message after login
        
        //Ask user how many messages they want to send in total
        int limit = readInt(scanner, "Enter the number of messages you wish to send: ");
        int processed = 0; // Counter for how many messages have been attempted
        int choice = 0; //Menu choice variable

        // while loop: keeps the menu running until the user chooses Quit
        while (choice != 3) {
            System.out.println("\n1) Send Messages"); //Option 1 - main feature
            System.out.println("2) Show recently sent messages"); // Option 2 - for Part 3 (coming soon)
            System.out.println("3) Quit"); //Option 3 - exit
            choice = readInt(scanner, "Enter your choice: "); // Read menu choice

            if (choice == 1) {
                if (processed >= limit) { // Check if user already reached their message limit
                    System.out.println("Message limit reached."); // Stop them from sending more
                } else {
                    //Call helper method to entire messages, updates processed count
                    processed = enterMessages(scanner, processed, limit);
                }
            } else if (choice == 2) {
                System.out.println("Coming Soon."); //Placeholder for Part 3 - it will show reports
            } else if (choice == 3) {
                System.out.println("Goodbye."); // exit message
            } else {
                System.out.println("Invalid option. Please choose 1, 2 or 3."); // Input validation
            }
        }
    }

    /** 
     * for loop: allows only the user-defined number of messages. 
     * This is the main sending flow that loops until limit is reached.
     * @param scanner Scanner for input
     * @param processed How many messages already processed
     * @param limit Max messages user wants to send
     * @return Updated processed count
     */
    private static int enterMessages(Scanner scanner, int processed, int limit) {
        //Loop from current count up to the limit user set
        for (int i = processed; i < limit; i++) {
            System.out.println("Creating message " + (i + 1) + " of " + limit + " ...");

            String recipient = askRecipient(scanner); // Step 1: Get and validate recipient number
            String text = askText(scanner); // Step 2: Get and validate message text (250 characters)
            Message msg = new Message(i + 1, recipient, text);

            // Step 3: Create new Message object - this auto-generates ID and Hash inside constructor
            System.out.println("Message ID generated: " + msg.getMessageId()); // Show generated ID
            
            // Step 4: Ask user what to do: Send / Diregard / Store
            int action = readInt(scanner, "1) Send  2) Disregard  3) Store : ");
            System.out.println(msg.SentMessage(action)); // Call SentMessage which handles add/store logic

            if (action == 1) { //If user chose Send
                System.out.println(msg.getDetails()); //Show full details: ID, Hash, Recipient, Message
            } else if (action == 2) { //If user chose Disregard
                readInt(scanner, "Enter 0 to confirm: "); //Confirm delete with 0 as per the speculations of the POE
            }
            processed++; // Increment counter - this message is (sent/disregarded/stored)
        }
        System.out.println("Message limit reached."); //Loop finished
        //Shows total sent - returnTotalMessages() is static from Message class
        System.out.println("Total messages successfully sent: " + Message.returnTotalMessagess());
        return processed; // Return new total to main menu
    }

    /**
     * Helper to get valid recipient number. Loops until correct international format.
     * @param scanner Scanner for input
     * @return Valid cell number with +
     */
    private static String askRecipient(Scanner scanner) {
        while (true) { //Infinite loop until valid number is entered
            System.out.print("Enter recipient cell number (with international code, e.g. +27...): ");
            String number = scanner.nextLine().trim(); //Read and remove spaces
            
            //Create a dummy message to test the validation method checkRecipientCell()
            //We use fake ID "0000000000" because we only want to test the number, not save it
            Message check = new Message("0000000000", 0, number, "check");
            String result = check.checkRecipientCell(); //This does the regex check ^\+\d{10,13}$
            
            System.out.println(result); //Show "successfuly captured" or error message
            
            //If validation returned successf message, return the number
            if (result.startsWith("Cell phone number successfully")) {
                return number; //Breaks the while loop, valid number found
            }
            //If not valid, while loop continues and asks again
        }
    }

    /**
     * Helper to get valid message text. Loops until text is not empty and <= 250 characters.
     * Return validation logic from Message.checkMessageLength()
     * @param scanner Scanner for input
     * @param Valid message text
     * @return 
     */

    private static String askText(Scanner scanner) {
        while (true) { //Keep asking until valid message entered
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine(); //Read full line including spaces
            
            if (text.trim().isEmpty()) { //Check if user just pressed or spaces
                System.out.println("Message cannot be empty.");
                continue; // Skipn rest and ask again
            }
            
            //Create dummy Message object just totest length validation
            //Using fake ID and fake recipient because we only wan to rest text length
            Message check = new Message("0000000000", 0, "+000000000000", text);
            String result = check.checkMessageLength(); //Calls logic: text.length() <= MAX_LENGTH
            System.out.println(result); //Shows "Message ready to send." or "exceeds by x chars"
            
            if (text.length() <= Message.MAX_LENGTH) { // If within 250 limit
                return text; // Valid - return and exit loop
            }
            //If too long, loop continues
        }
    }
    
    /**
     * Safe integer input reader.Prevents program from crashing if user types letters.
     * Uses try-catch with NumberFormatException
     * @param scanner Scanner object
     * @param prompt Text to show user (e.g. "Enter your choice: ")
     * @return Valid integer entered by user
     */
    private static int readInt(Scanner scanner, String prompt) {
        while (true) { //Loop until user enters a proper number
            System.out.print(prompt); //Show the prompt
            try {
                
                //Trim removes spaces, parselnt converts String to int
                //Using nextLine() not nextInt() avoids Scanner bug wherenewline stays in buffer
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) { //If user typed "abc" instead of number
                System.out.println("Please enter a number."); //Friendly error, then loop again
            }
        }
    }
}
//The end of the QuickChat class