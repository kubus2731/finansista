package pl.pb.finansista.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class ApiErrorMessageResolver {

  private final MessageSource messageSource;

  public String resolve(RestClientResponseException e, String fallback) {
    String code = null;
    String detail = null;
    try {
      ProblemDetail pd = e.getResponseBodyAs(ProblemDetail.class);
      if (pd != null) {
        detail = pd.getDetail();
        if (pd.getProperties() != null) {
          Object c = pd.getProperties().get("code");
          code = c != null ? c.toString() : null;
        }
      }
    } catch (RuntimeException ignored) {
      // fallback
    }

    String genericFallback =
        StringUtils.hasText(detail)
            ? detail
            : fallback + " (kod " + e.getStatusCode().value() + ").";
    if (code == null) {
      return genericFallback;
    }
    return messageSource.getMessage(
        "error." + code, null, genericFallback, LocaleContextHolder.getLocale());
  }
}
