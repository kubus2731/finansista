package pl.pb.finansista.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidExternalIdException extends BusinessException {

    public InvalidExternalIdException() {
        super("Invalid identifier format.", HttpStatus.BAD_REQUEST);
    }
}
