package pl.pb.finansista.frontend.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }

  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }
}
