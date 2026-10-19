package com.mycompany.chatapppart2;

//Gson library imports for JSON handling (reding/ writing message to file)
//Attribution: https://github.com/google/gson/blob/main/UserGuide.md
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Java standard library imports
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom; //used for secure random ID
import java.util.ArrayList;
import java.util.List;

/**
 * Message.java
 * Holds one QuickChat message and the behavior related to it.
 * Part 2 if ChatApp - handles message creation, storage and JSON persistence.
 * 
 * Attribution: 
 * -JSON reading/writing uses the Gson library by Google
 * Source: https://github.com/google/gson.com/google/blob/main/UserGuide.md
 * -SecueRandom for ID generation: https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html
 * -File I/O with NIO: https://docs.oracle.com/javase/tutorial/essential/io/file.html
 */
public class Message {

    // --- Constants ---
    // Maximum allowed length for a message body as per the speculations of the assignment
    public static final int MAX_LENGTH = 250;
    
    // Secure random generator used to create uninque message IDs/hashes
    private static final SecureRandom RANDOM = new SecureRandom();

    // ---Session-wide data ( shared across all Message objects) ---
    //Keeps track of all messages sent in this session
    private static final List<Message> sentMessages = new ArrayList<>();
    
    //Counter for total nu,ber of messages sent
    private static int totalSent = 0;
    
    // --- Instance variables ( uniques to each message) ---
    //Unique ID for for this specific message
    private final String messageId;
    
    //Sequencial number of this message (1,2,3...)
    private final int messageNumber;
    
    // --- Instance Fields ---
    private final String recipient; // Stores the recipient's cellphone number
    private final String text; // Stores the actual message content
    private final String messageHash; // Stores the unique hash created for the message
    private String storagePath = "storedMessages.json"; // File path where messages are saved as JSON

    /** 
     * Normal constructor: generates a random ten-digit ID. 
     * This is the constructor used when a user creates a new message.
     * @param messageNumber The sequential number of the message
     * @param recipient The recipient's phone phone number 
     * @param text The message content
     */
    public Message(int messageNumber, String recipient, String text) {
        // Call the other constructor with a newly generated ID
        this(generateMessageID(), messageNumber, recipient, text);
    }

    /** 
     * Constructor with a known ID (used by unit tests for predictable hashes). 
     * Allows us to inject a specific ID (used by unit tests for predictable hashes).
     * @param messageId The predefined 10-digit ID
     * @param messageNumber The message number 
     * @param recipient The recipient
     * @param text The message content
     */
    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId; //Declare the provided ID
        this.messageNumber = messageNumber; // Declare the message number
        this.recipient = recipient; //Declare the recipient
        this.text = text; //Assign the mesasage text
        this.messageHash = createMessageHash(); //Generate hash based on message details
    }

    // ---------- ID ----------

    /** 
     * Random ten-digit ID kept as a String so leading zeroes survive. 
     * If we used an int, an ID like "0012345678" would become "12345678".
     * @retun A 10-digit random numeric String
     */
    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder(); //Buidler to effciently build the ID string
        for (int i = 0; i < 10; i++) { // Loop 10 times for 10 digits
            sb.append(RANDOM.nextInt(10)); //Append a random digit (0-9)
        }
        return sb.toString(); //Return the completed 10-digit ID
    }

    /**
     * True when the ID is exactly ten digits.
     * Validates the format of the message ID.
     * @return true if ID is 10 digits, false otherwise
     */
    public boolean checkMessageID() {
        // Check if ID is not null and matches regex for exactly 10 digits
        return messageId != null && messageId.matches("\\d{10}");
    }

    // ---------- Recipient ----------

    /** 
     * International format: '+' then digits (e.g. +27718693002). 
     * Validates if recipient number has international code.
     * @return Success or error message about cell number format
     */
    public String checkRecipientCell() {
        //Regex check: starts with +, then 10-13 digits total after +
        //Example of valid: +27718693002(South Africa has 11 digits after +)
        if (recipient != null && recipient.matches("\\+\\d{10,13}")) {
            return "Cell phone number successfully captured.";
        }
        //Return error if format is wrong
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // ---------- Length ----------

    /**
     * Checks if message text is within the allowed 250 character limit.
     * @return Message status - ready to send or how many characters over
     */
    public String checkMessageLength() {
        if (text.length() <= MAX_LENGTH) { //Check if within 250 character limit
            return "Message ready to send.";
        }
        //Calculate how many characters over the limit
        int excess = text.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    // ---------- Hash ----------

    /** 
     * Format  first2OfID:messageNumber:FIRSTLAST  in uppercase. 
     * Example: If ID=0012345678,msgNum=0,text="Hi mos" -> 00:00:HIMOS -> but using first/last words
     * Creates a hash for quick identification of the message.
     */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+"); //Split message into words by whithespace
        String first = stripPunctuation(words[0]); //Get first word and removepunctuation
        String last = stripPunctuation(words[words.length - 1]); //Get last word and remove punctuation
        String prefix = messageId.substring(0, 2); //Get first 2 digits of the message ID
        //Combine format: prefix:messageNumber:first+last and convert to uppercase
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /**
     * Removes non-letter/digit characters from both ends of a word. 
     * Used to clean first and last words before creating hash.
     * Example: "hello!!" becomes "hello"
     * @param word The Word to clean
     * @return Cleaned word with only alphanumeric characters
     */
    private static String stripPunctuation(String word) {
        // Replace non-alphanumeric at start(^)and ends ($) with empty string
        return word.replaceAll("^[^A-Za-z0-9]+", "").replaceAll("[^A-Za-z0-9]+$", "");
    }

    // ---------- Send / Disregard / Store ----------

    /** 
     * choice: 1 = send, 2 = disregard, 3 = store.
     * Handles user decision after typing a message.
     * @param choice User's menu choice (1,2,3)
     * @return Status message confirming the action
     */
    public String SentMessage(int choice) {
        switch (choice) { //Switch based on user choice
            case 1: //User chose to send the message 
                sentMessages.add(this); // Add this message to session list
                totalSent++; //Increment total counter for the run
                return "Message successfully sent.";
            case 2: //User chose to disregard/delete
                return "Press 0 to delete the message.";
            case 3: // User chose to store for later 
                storeMessage(); //Call method to save JSON file
                return "Message successfully stored.";
            default: //Any invslid number
                return "Invalid choice.";
        }
    }

    // ---------- Reporting ----------

     /** 
     * Returns every message sent in this run, in the required order. 
     * Used to show user what was sent.
     * @return Formatted string with ID,Hash,Recipient and MEssage 
     */
    public String getDetails() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }
   
    /**
     * Returns every message sent in this run, in the required order.
     * Loops through sentMessages list and concatenates all details.
     * Returned for the final report after user quits.
     * @return All sent messages as one big string
     */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder(); //User StringBuilder for efficient concatenation
        for (Message m : sentMessages) { //Enchanced for loop through all sent messages
            sb.append(m.getDetails()).append("\n\n"); //Add each message details + blank line 
        }
        return sb.toString(); //Return the complete report
    }

    public static int returnTotalMessagess() {
        return totalSent;
    }

    /** 
     * Clears session data (useful for tests).
     * Return the program when running JUnit tests so old messages don't carry over.
     */
    public static void resetSession() {
        sentMessages.clear(); //Empty the list of sent messages
        totalSent = 0; //Reset total counter back to 0
    }

    // ---------- JSON storage ----------

    /**
     * Allows changing where JSON file is saved (used for testing).
     * @param storagePath New file path to use
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath; //Set custom path for storedMessages.json
    }

    /** 
     * Appends this message to the JSON array file; returns true on success. 
     * This is the method that saves messages when user chooses "store".
     * @return true if file write succeeded, false if failed
     */
    public boolean storeMessage() {
        Path path = Paths.get(storagePath); //Convert file path Stringto Path object
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Create Gson with pretty print(nice formatting)
        try {
            JsonArray array = new JsonArray();// Create new empty JSON array
            if (Files.exists(path)) { //Check if file already exists
                String existing = new String(Files.readAllBytes(path), StandardCharsets.UTF_8); //Read entire file as String
                if (!existing.trim().isEmpty()) { //Check if file is not empty
                    array = JsonParser.parseString(existing).getAsJsonArray(); //Parse existing content into JSON array
                }
            }
            JsonObject obj = new JsonObject(); //Create a new JSON object for this message
            obj.addProperty("messageId", messageId); // Add message ID property
            obj.addProperty("messageHash", messageHash);//Add message hash property
            obj.addProperty("recipient", recipient); //Add recipient number
            obj.addProperty("message", text); //Add actual message text
            obj.addProperty("status", "STORED"); //Mark status as STORED
            array.add(obj); //Add this message object to the array
            
            //Write the whole array back to file in UTF-8 format
            Files.write(path, gson.toJson(array).getBytes(StandardCharsets.UTF_8));
            return true; //Return true if successful
        } catch (IOException | RuntimeException e) { //Catch file errors JSON errors
            System.out.println("Could not store message: " + e.getMessage()); //Print error to console
            return false; //Return false if failed
        }
    }

    // ---------- Getters ----------

    /**
     * Getter for messageId.
     * Needed for testing and for getDetails() report.
     * @return The unique 10-digit message ID
     */
    public String getMessageId() { return messageId; }
    
    /**
     * Getter far messageNumber.
     * @return The sequential number of the message (1,2,3..)
     */
    public int getMessageNumber() { return messageNumber; }
    
    /**
     * Getter for recipient
     * @return The actual recipient's cell number with international code
     */
    public String getRecipient() { return recipient; }
    
    /** 
     * Getter for text/ message content.
     * @return The actual message body
     */
    public String getText() { return text; }
    
    /**
     * Getter for messageHash.
     * Hash is used for quick searching and for the final report.
     * @return The message hash in format ID:NUMBER:FIRSTLAST
     */
    public String getMessageHash() { return messageHash; }
}
