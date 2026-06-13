package pl.pb.finansista.reference;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class DepartmentNotFoundException extends BusinessException {

    public DepartmentNotFoundException() {
        super("Department not found.", HttpStatus.NOT_FOUND);
    }
}
