package pl.pb.finansista.reference.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class CostCategoryNotFoundException extends BusinessException {

  public CostCategoryNotFoundException() {
    super("Cost Category not found.", HttpStatus.NOT_FOUND);
  }
}
