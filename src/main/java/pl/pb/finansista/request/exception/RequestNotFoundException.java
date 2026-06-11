package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

import java.util.UUID;

public class RequestNotFoundException extends BusinessException {
    private RequestNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static RequestNotFoundException withExternalId(UUID externalId) {
        return new RequestNotFoundException(String.format("Request with ID %s not found.", externalId));
    }
}
