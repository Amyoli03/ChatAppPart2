# ChatAppPart2 (QuickChat)

PROG5121 Programming 1A: Part 2, Sending Messages
Student: [Amyoli Louw] | Student number: [ST10535093]

A Java console application built with Maven. It extends the Part 1 registration and login system with QuickChat, a messaging feature that only a logged-in user can access.

## Features

**Part 1: Registration and login**
- Username must contain an underscore and be no more than 5 characters.
- Password must be at least 8 characters with a capital letter, a number and a special character.
- Cell phone number must be in South African international format (e.g. `+27838968976`).
- Login is checked against the registered details.

**Part 2: QuickChat**
- Welcome message and numeric menu: 1) Send Messages, 2) Show recently sent messages, 3) Quit.
- A `while` loop keeps the menu running until the user chooses Quit.
- The user sets how many messages they want to enter, and a `for` loop enforces that limit.
- Each message gets a random ten-digit message ID, a message number and an uppercase message hash (first two ID digits, message number, first and last words, e.g. `00:0:HITONIGHT`).
- The recipient number must be in international format, and the message must be 250 characters or fewer.
- For each message the user can send it, disregard it or store it.
- Sent messages are displayed in this order: Message ID, Message Hash, Recipient, Message. The total number sent is shown at the end.
- Stored messages are saved to `storedMessages.json`.
- Option 2 shows "Coming Soon." (not required in Part 2).

## Project structure

```
ChatAppPart2/
├── pom.xml
├── .github/workflows/TestJava.yml
└── src/
    ├── main/java/com/mycompany/chatapppart2/
    │   ├── Main.java        (registration, login, starts QuickChat)
    │   ├── Login.java       (Part 1 validation and login)
    │   ├── QuickChat.java   (menu and message entry flow)
    │   └── Message.java     (message validation, hash, send/store, JSON)
    └── test/java/com/mycompany/chatapppart2/
        ├── LoginTest.java
        └── MessageTest.java
```

## Requirements

- Java 17 (or newer)
- Maven
- Internet access on the first build (to download Gson and JUnit)

## How to run

From the project folder (the one containing `pom.xml`):

```
mvn clean compile exec:java -Dexec.mainClass=com.mycompany.chatapppart2.Main
```

Or open the project in NetBeans and run `Main.java`.

## How to run the tests

```
mvn clean test
```

The tests use JUnit 5 and cover successful and unsuccessful cases for login, recipient format, message length, message ID, hash creation, and the send, disregard and store choices.

## GitHub Actions

The workflow in `.github/workflows/TestJava.yml` runs `mvn -B test` on every push and pull request.

## Libraries and attribution

- Gson (https://github.com/google/gson) is used to write stored messages to JSON.
- JUnit 5 is used for unit testing.
- [Add any other websites, tutorials or sources you used.]

## Notes

- Message numbers in the app start at 1. The unit tests use a controlled ID and message number to check the sample hash `00:0:HITONIGHT`.
- Recipient numbers are accepted when they start with `+` followed by 10 to 13 digits.

