package com.example.bureau.exceptions;

public class PasswordTooLongException extends Exception  {
    public PasswordTooLongException() {
        super("Error: Password should be less than or equal to 120 characters!");
    }
}