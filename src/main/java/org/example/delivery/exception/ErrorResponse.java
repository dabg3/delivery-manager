package org.example.delivery.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ErrorResponse {

    private List<ErrorMessage> errors;

    public ErrorResponse(ErrorMessage errorMessage) {
        this.errors = List.of(errorMessage);
    }

    public ErrorResponse(List<ErrorMessage> errors) {
        this.errors = errors;
    }

    public ErrorResponse() {
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public static class ErrorMessage {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String path;
        private String message;

        public ErrorMessage(String path, String message) {
            this.path = path;
            this.message = message;
        }

        public ErrorMessage(String message) {
            this.path = null;
            this.message = message;
        }

        public ErrorMessage() {
        }

        public String getPath() {
            return path;
        }

        public String getMessage() {
            return message;
        }
    }
}
