package com.example.bureau.exceptions;

public class EmailTakenException extends Exception{
    public EmailTakenException() {
        super("Error: Email is already in use!");
    }
}
