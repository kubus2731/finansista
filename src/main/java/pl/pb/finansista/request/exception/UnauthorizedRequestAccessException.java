package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UnauthorizedRequestAccessException extends BusinessException {
    public UnauthorizedRequestAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
