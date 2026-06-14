package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class InvalidRequestStateException extends BusinessException {
    private InvalidRequestStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public static InvalidRequestStateException withStatusName(String statusName) {
        return new InvalidRequestStateException(String.format("Status %s not found or transition is invalid.", statusName));
    }

    public static InvalidRequestStateException notInDraft() {
        return new InvalidRequestStateException("Action requires the request to be in DRAFT status.");
    }
}
