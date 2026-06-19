package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RequestFundingNotFoundException extends BusinessException {
  public RequestFundingNotFoundException() {
    super("This request has no funding line for the given source.", HttpStatus.NOT_FOUND);
  }
}
