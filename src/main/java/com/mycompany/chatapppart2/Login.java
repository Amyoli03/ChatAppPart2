package com.mycompany.chatapppart2;

/** Part 1: registration and login validation. */
public class Login {

    private String username;
    private String password;
    private String phoneNumber;

    /** Username must contain an underscore and be no more than 5 characters. */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /** Password: at least 8 characters, a capital letter, a number and a special character. */
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        boolean longEnough = password.length() >= 8;
        return hasUpper && hasDigit && hasSpecial && longEnough;
    }

    /** South African international format, e.g. +27838968976. */
    public boolean checkCellPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\+27\\d{9}");
    }

    public String registerUser(String username, String password, String phoneNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username "
                    + "contains an underscore and is no more than 5 characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least 8 characters, a capital letter, a number and a special character.";
        }
        if (!checkCellPhoneNumber(phoneNumber)) {
            return "Cell phone number is incorrectly formatted or does not contain an "
                    + "international code, please correct the number and try again.";
        }
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        return "Username registered successfully.";
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (this.username == null || this.password == null) {
            return false;
        }
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }

    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + enteredUsername + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
