package com.example.smoking_cessation_platform.exception;

public class UserNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;  // Giúp IDE ngừng cảnh báo

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
