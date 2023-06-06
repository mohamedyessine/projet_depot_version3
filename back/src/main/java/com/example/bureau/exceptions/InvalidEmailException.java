package com.example.bureau.exceptions;

public class InvalidEmailException extends Exception{
    public InvalidEmailException() {
        super("Error: Email should be valid!");
    }
}

