package pl.pb.finansista.user;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException(String.format("User with email %s not found.", email));
    }

    public static UserNotFoundException withId(String id) {
        return new UserNotFoundException(String.format("User with id %s not found.", id));
    }

    public static UserNotFoundException withUsername(String username) {
        return new UserNotFoundException(String.format("User with username %s not found.", username));
    }
}
