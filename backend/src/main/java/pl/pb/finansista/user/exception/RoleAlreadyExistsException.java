package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RoleAlreadyExistsException extends BusinessException {
  public RoleAlreadyExistsException(String name) {
    super("A role named '" + name + "' already exists.", HttpStatus.CONFLICT);
  }
}
