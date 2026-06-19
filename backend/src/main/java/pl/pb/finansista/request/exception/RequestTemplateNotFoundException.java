package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RequestTemplateNotFoundException extends BusinessException {

  public RequestTemplateNotFoundException() {
    super("Request Template not found.", HttpStatus.NOT_FOUND);
  }
}
