package pl.pb.finansista.user;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class RoleNotFoundException extends BusinessException {

    public RoleNotFoundException() {
        super("Role not found.", HttpStatus.NOT_FOUND);
    }
}
