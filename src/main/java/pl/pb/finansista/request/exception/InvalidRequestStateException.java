package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class InvalidRequestStateException extends BusinessException {
    public InvalidRequestStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
