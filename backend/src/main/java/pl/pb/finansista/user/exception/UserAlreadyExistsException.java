package pl.pb.finansista.user.exception;

import pl.pb.finansista.common.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {

  public UserAlreadyExistsException(String message) {
    super(message, org.springframework.http.HttpStatus.CONFLICT);
  }

  public static UserAlreadyExistsException withEmail(String email) {
    return new UserAlreadyExistsException(
        String.format("User with email %s already exists.", email));
  }
}
