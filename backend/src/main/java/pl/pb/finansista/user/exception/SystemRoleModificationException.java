package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

/** Built-in roles back the authorization logic (RoleName), so they are immutable. */
public class SystemRoleModificationException extends BusinessException {
  public SystemRoleModificationException() {
    super("Built-in system roles cannot be modified or deleted.", HttpStatus.CONFLICT);
  }
}
