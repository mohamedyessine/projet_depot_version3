package com.example.bureau.payload.response;

public class ArticleBureauResponse {
    private String message;
    private int statusCode;
    private String statusDescription;

    public ArticleBureauResponse(String message, int statusCode, String statusDescription) {
        this.message = message;
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
