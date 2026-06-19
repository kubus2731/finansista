package pl.pb.finansista.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.frontend.exception.BusinessException;

@ControllerAdvice(basePackages = "pl.pb.finansista.frontend.web")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler {

  /**
   * Załącznik przekroczył limit rozmiaru — zamiast 500/zawieszenia wracamy na stronę wniosku z
   * czytelnym komunikatem.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public String handleMaxUploadSize(
      HttpServletRequest request, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "errorMessage", "Załącznik jest za duży — maksymalny rozmiar pliku to 10 MB.");
    String uri = request.getRequestURI();
    int idx = uri.indexOf("/attachments");
    if (uri.startsWith("/requests/") && idx > 0) {
      return "redirect:" + uri.substring(0, idx);
    }
    return "redirect:/requests";
  }

  @ExceptionHandler(BusinessException.class)
  public ModelAndView handleBusiness(BusinessException ex) {
    return errorView(ex.getHttpStatus());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ModelAndView handleAccessDenied(AccessDeniedException ex) {
    return errorView(HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ModelAndView handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return errorView(HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleUnexpected(Exception ex) {
    return errorView(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ModelAndView errorView(HttpStatus status) {
    ModelAndView modelAndView = new ModelAndView("error/" + status.value());
    modelAndView.setStatus(status);
    return modelAndView;
  }
}
