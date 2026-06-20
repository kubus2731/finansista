package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {

  private UserAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public static UserAlreadyExistsException withEmail(String email) {
    return new UserAlreadyExistsException(
        String.format("User with email %s already exists.", email));
  }
}
