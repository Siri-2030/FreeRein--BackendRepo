package com.hr.freerein.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private HttpStatus status;
    private String timestamp;
    private Map<String, Object> metadata;
    
    // ✅ MANUAL CONSTRUCTOR
    public ApiResponse() {}
    
    public ApiResponse(boolean success, T data, String message, HttpStatus status, 
                      String timestamp, Map<String, Object> metadata) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }
    
    // ✅ MANUAL GETTERS/SETTERS
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public HttpStatus getStatus() { return status; }
    public void setStatus(HttpStatus status) { this.status = status; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    // ✅ STATIC FACTORY METHODS (UNCHANGED)
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, HttpStatus.OK, 
            LocalDateTime.now().toString(), null);
    }
    
    public static <T> ApiResponse<T> success(T data, String message, Map<String, Object> metadata) {
        return new ApiResponse<>(true, data, message, HttpStatus.OK, 
            LocalDateTime.now().toString(), metadata);
    }
    
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(false, null, message, status, 
            LocalDateTime.now().toString(), null);
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, "Resource created successfully", 
            HttpStatus.CREATED, LocalDateTime.now().toString(), null);
    }
    
    public static <T> ApiResponse<T> updated(T data) {
        return new ApiResponse<>(true, data, "Resource updated successfully", 
            HttpStatus.OK, LocalDateTime.now().toString(), null);
    }
    
    public static <T> ApiResponse<T> deleted() {
        return new ApiResponse<>(true, null, "Resource deleted successfully", 
            HttpStatus.OK, LocalDateTime.now().toString(), null);
    }
    
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, null, message, HttpStatus.NOT_FOUND, 
            LocalDateTime.now().toString(), null);
    }
    
 // ✅ ADD THESE TO YOUR EXISTING ApiResponse class

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(
            false, null, message, HttpStatus.UNAUTHORIZED,
            LocalDateTime.now().toString(), null
        );
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(
            false, null, message, HttpStatus.FORBIDDEN,
            LocalDateTime.now().toString(), null
        );
    }


}