package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RequestNotFoundException extends BusinessException {
    public RequestNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
