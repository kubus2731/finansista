package pl.pb.finansista.reference.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class DepartmentAlreadyExistsException extends BusinessException {
  public DepartmentAlreadyExistsException(String name) {
    super("A department named '" + name + "' already exists.", HttpStatus.CONFLICT);
  }
}
