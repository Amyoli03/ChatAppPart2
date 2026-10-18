package com.mycompany.chatapppart2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds one QuickChat message and the behaviour related to it.
 *
 * Attribution: JSON reading/writing uses the Gson library
 * (https://github.com/google/gson/blob/main/UserGuide.md).
 * Add any other source you researched here.
 */
public class Message {

    public static final int MAX_LENGTH = 250;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Session-wide data
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalSent = 0;

    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;
    private String storagePath = "storedMessages.json";

    /** Normal constructor: generates a random ten-digit ID. */
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(), messageNumber, recipient, text);
    }

    /** Constructor with a known ID (used by unit tests for predictable hashes). */
    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // ---------- ID ----------

    /** Random ten-digit ID kept as a String so leading zeroes survive. */
    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** True when the ID is exactly ten digits. */
    public boolean checkMessageID() {
        return messageId != null && messageId.matches("\\d{10}");
    }

    // ---------- Recipient ----------

    /** International format: '+' then digits (e.g. +27718693002). */
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+\\d{10,13}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // ---------- Length ----------

    public String checkMessageLength() {
        if (text.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    // ---------- Hash ----------

    /** Format  first2OfID:messageNumber:FIRSTLAST  in uppercase. */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String first = stripPunctuation(words[0]);
        String last = stripPunctuation(words[words.length - 1]);
        String prefix = messageId.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Removes non-letter/digit characters from both ends of a word. */
    private static String stripPunctuation(String word) {
        return word.replaceAll("^[^A-Za-z0-9]+", "").replaceAll("[^A-Za-z0-9]+$", "");
    }

    // ---------- Send / Disregard / Store ----------

    /** choice: 1 = send, 2 = disregard, 3 = store. */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    // ---------- Reporting ----------

    public String getDetails() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    /** Returns every message sent in this run, in the required order. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessagess() {
        return totalSent;
    }

    /** Clears session data (useful for tests). */
    public static void resetSession() {
        sentMessages.clear();
        totalSent = 0;
    }

    // ---------- JSON storage ----------

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /** Appends this message to the JSON array file; returns true on success. */
    public boolean storeMessage() {
        Path path = Paths.get(storagePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonArray array = new JsonArray();
            if (Files.exists(path)) {
                String existing = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                if (!existing.trim().isEmpty()) {
                    array = JsonParser.parseString(existing).getAsJsonArray();
                }
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("messageId", messageId);
            obj.addProperty("messageHash", messageHash);
            obj.addProperty("recipient", recipient);
            obj.addProperty("message", text);
            obj.addProperty("status", "STORED");
            array.add(obj);
            Files.write(path, gson.toJson(array).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not store message: " + e.getMessage());
            return false;
        }
    }

    // ---------- Getters ----------

    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }
}
