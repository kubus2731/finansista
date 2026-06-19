package pl.pb.finansista.request.exception;

import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class FundingAmountMismatchException extends BusinessException {

  public FundingAmountMismatchException(BigDecimal fundingTotal, BigDecimal requestAmount) {
    super(
        String.format(
            "The sum of funding sources (%s) must equal the request amount (%s).",
            fundingTotal, requestAmount),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
