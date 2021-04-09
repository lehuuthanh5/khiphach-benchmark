package com.khiphach.benchmark.model;

import com.khiphach.benchmark.enumeration.Status;

public class CheckResponse {
    private Status status;
    private String message;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
