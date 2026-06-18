package pl.pb.finansista.frontend.web;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import pl.pb.finansista.frontend.exception.BusinessException;

@ControllerAdvice(basePackages = "pl.pb.finansista.web")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler {

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
