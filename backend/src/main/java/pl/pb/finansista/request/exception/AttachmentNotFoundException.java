package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class AttachmentNotFoundException extends BusinessException {
  public AttachmentNotFoundException() {
    super("Attachment not found.", HttpStatus.NOT_FOUND);
  }
}
