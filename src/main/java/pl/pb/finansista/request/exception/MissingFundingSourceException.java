package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class MissingFundingSourceException extends BusinessException {

    public MissingFundingSourceException() {
        super("Funding source must be provided before submitting the request.", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
