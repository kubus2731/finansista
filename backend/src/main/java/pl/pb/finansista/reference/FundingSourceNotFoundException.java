package pl.pb.finansista.reference;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class FundingSourceNotFoundException extends BusinessException {

    public FundingSourceNotFoundException() {
        super("Funding Source not found.", HttpStatus.NOT_FOUND);
    }
}
