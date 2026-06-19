package pl.pb.finansista.frontend.web;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotFoundViewHandler {

  @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
  public ModelAndView handleNotFound() {
    ModelAndView modelAndView = new ModelAndView("error/404");
    modelAndView.setStatus(HttpStatus.NOT_FOUND);
    return modelAndView;
  }
}
