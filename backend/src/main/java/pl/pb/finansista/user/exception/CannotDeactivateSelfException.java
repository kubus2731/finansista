package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

/** Admin nie może dezaktywować konta, na którym jest aktualnie zalogowany. */
public class CannotDeactivateSelfException extends BusinessException {

  public CannotDeactivateSelfException() {
    super("You cannot deactivate your own account.", HttpStatus.CONFLICT);
  }
}
