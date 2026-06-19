package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RequestNotFoundException extends BusinessException {

  public RequestNotFoundException() {
    super("Request not found.", HttpStatus.NOT_FOUND);
  }
}
