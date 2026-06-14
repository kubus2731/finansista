package pl.pb.finansista.user.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super("User not found.", HttpStatus.NOT_FOUND);
    }
}
