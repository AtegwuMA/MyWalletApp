package com.example.mywalletapp.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {
    private int code;
    private String message;
    @JsonIgnore
    private HttpStatus httpStatus;
    private Object data;
    private List<String> datat;
    private Map<String, Object> metadata;

    public GenericResponse(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public GenericResponse(int code, String message, HttpStatus httpStatus, Object data) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
        this.data = data;

    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}



