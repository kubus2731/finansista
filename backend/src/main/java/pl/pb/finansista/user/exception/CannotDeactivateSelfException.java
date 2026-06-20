package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class CannotDeactivateSelfException extends BusinessException {

  public CannotDeactivateSelfException() {
    super("You cannot deactivate your own account.", HttpStatus.CONFLICT);
  }
}
