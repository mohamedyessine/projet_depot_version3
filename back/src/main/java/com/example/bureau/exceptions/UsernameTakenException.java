package com.example.bureau.exceptions;

public class UsernameTakenException extends Exception{
    public UsernameTakenException() {
        super("Error: Username is already taken!");
    }
}
