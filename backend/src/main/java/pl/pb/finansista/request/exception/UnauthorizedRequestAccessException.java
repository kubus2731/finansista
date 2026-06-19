package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class UnauthorizedRequestAccessException extends BusinessException {

  private UnauthorizedRequestAccessException(String message) {
    super(message, HttpStatus.FORBIDDEN);
  }

  private static UnauthorizedRequestAccessException forAction(String action) {
    return new UnauthorizedRequestAccessException(
        String.format("You do not have permission to %s.", action));
  }

  public static UnauthorizedRequestAccessException forCreatingRequest() {
    return forAction("create a request");
  }

  public static UnauthorizedRequestAccessException forEditingRequest() {
    return forAction("edit this request");
  }

  public static UnauthorizedRequestAccessException forAddingAttachment() {
    return forAction("add an attachment to this request");
  }

  public static UnauthorizedRequestAccessException forDeletingAttachment() {
    return forAction("delete this attachment");
  }

  public static UnauthorizedRequestAccessException forRecordingProvostOpinion() {
    return forAction("record the provost opinion for this request");
  }

  public static UnauthorizedRequestAccessException forGrantingFundingFrom(String source) {
    return forAction("grant funding from " + source);
  }

  public static UnauthorizedRequestAccessException forTransitionTo(String status) {
    return forAction("perform transition to " + status);
  }
}
