package pl.pb.finansista.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidIfMatchHeaderException extends BusinessException {

  private InvalidIfMatchHeaderException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public static InvalidIfMatchHeaderException required() {
    return new InvalidIfMatchHeaderException("If-Match header is required.");
  }

  public static InvalidIfMatchHeaderException invalidFormat() {
    return new InvalidIfMatchHeaderException("Invalid If-Match header format.");
  }
}
