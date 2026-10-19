package com.mycompany.chatapppart2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() { login = new Login(); }

    @Test
    void usernameCorrectlyFormatted() { assertTrue(login.checkUserName("kyl_1")); }

    @Test
    void usernameIncorrectlyFormatted() { assertFalse(login.checkUserName("kyle!!!!!!!")); }

    @Test
    void passwordMeetsComplexity() { assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!")); }

    @Test
    void passwordFailsComplexity() { assertFalse(login.checkPasswordComplexity("password")); }

    @Test
    void cellNumberCorrectlyFormatted() { assertTrue(login.checkCellPhoneNumber("+27838968976")); }

    @Test
    void cellNumberIncorrectlyFormatted() { assertFalse(login.checkCellPhoneNumber("08966553")); }

    @Test
    void registrationSucceeds() {
        assertEquals("Username registered successfully.",
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    void registrationFailsOnUsername() {
        assertTrue(login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976")
                .startsWith("Username is not correctly formatted"));
    }

    @Test
    void loginSucceeds() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertEquals("Welcome kyl_1 it is great to see you again.",
                login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void loginFails() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrong"));
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus("kyl_1", "wrong"));
    }
}
