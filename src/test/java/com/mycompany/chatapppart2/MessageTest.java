package com.mycompany.chatapppart2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    private static final String TEST_FILE = "testStored.json";
    private static final String MSG1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() { Message.resetSession(); new File(TEST_FILE).delete(); }

    @AfterEach
    void tearDown() { new File(TEST_FILE).delete(); }

    @Test
    void hashMatchesExpected() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    void validRecipient() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    void invalidRecipient() {
        Message m = new Message("0012345678", 1, "08575975889", MSG2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    @Test
    void messageOf250IsAccepted() {
        String text = "a".repeat(250);
        Message m = new Message("0012345678", 0, "+27718693002", text);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    void messageTooLongReportsExcess() {
        String text = "a".repeat(257);
        Message m = new Message("0012345678", 0, "+27718693002", text);
        assertEquals("Message exceeds 250 characters by 7; please reduce the size.",
                m.checkMessageLength());
    }

    @Test
    void generatedIdIsTenDigits() {
        Message m = new Message(1, "+27718693002", MSG1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageId().length());
    }

    @Test
    void badIdFailsCheck() {
        Message m = new Message("12345", 1, "+27718693002", MSG1);
        assertFalse(m.checkMessageID());
    }

    @Test
    void sendChoiceCountsAsSent() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Message successfully sent.", m.SentMessage(1));
        assertEquals(1, Message.returnTotalMessagess());
        assertTrue(Message.printMessages().contains("Message ID: 0012345678"));
    }

    @Test
    void disregardDoesNotCount() {
        Message m = new Message("0012345678", 1, "08575975889", MSG2);
        assertEquals("Press 0 to delete the message.", m.SentMessage(2));
        assertEquals(0, Message.returnTotalMessagess());
    }

    @Test
    void storeWritesJsonAndDoesNotCount() throws Exception {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        m.setStoragePath(TEST_FILE);
        assertEquals("Message successfully stored.", m.SentMessage(3));
        assertEquals(0, Message.returnTotalMessagess());
        String json = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(json.contains("00:0:HITONIGHT"));
        assertTrue(json.contains("STORED"));
    }

    @Test
    void multipleMessagesHaveCorrectNumberAndHash() {
        String[] texts = {"Hello there friend.", "See you soon!", "Call me later?"};
        String[] expected = {"00:1:HELLOFRIEND", "00:2:SEESOON", "00:3:CALLLATER"};
        for (int i = 0; i < texts.length; i++) {
            Message m = new Message("0099999999", i + 1, "+27718693002", texts[i]);
            assertEquals(i + 1, m.getMessageNumber());
            assertEquals(expected[i], m.createMessageHash());
        }
    }
}
