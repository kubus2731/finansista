package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UnauthorizedRequestAccessException extends BusinessException {
  private UnauthorizedRequestAccessException(String message) {
    super(message, HttpStatus.FORBIDDEN);
  }

  public static UnauthorizedRequestAccessException forAction(String action) {
    return new UnauthorizedRequestAccessException(
        String.format("You do not have permission to %s this request.", action));
  }
}
