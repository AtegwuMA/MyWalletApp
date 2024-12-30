package com.example.mywalletapp.dto.responsedto;

// ErrorResponse.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;

    /**
     * Constructor for ErrorResponse with only message and status.
     * The timestamp will be set to the current time.
     *
     * @param message The error message.
     * @param status  The HTTP status code.
     * @param details Additional details about the error.
     */
    public ErrorResponse(String message, int status, String details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}
