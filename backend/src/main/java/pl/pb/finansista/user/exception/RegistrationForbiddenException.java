package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RegistrationForbiddenException extends BusinessException {

  public RegistrationForbiddenException(String roleName) {
    super(
        String.format("Role '%s' cannot be self-assigned during registration.", roleName),
        HttpStatus.FORBIDDEN);
  }
}
