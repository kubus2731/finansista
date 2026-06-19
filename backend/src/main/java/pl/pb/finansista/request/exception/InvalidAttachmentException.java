package pl.pb.finansista.request.exception;

import org.springframework.http.HttpStatus;
import pl.pb.finansista.common.exception.BusinessException;

public class InvalidAttachmentException extends BusinessException {

  private InvalidAttachmentException(String message, HttpStatus status) {
    super(message, status);
  }

  public static InvalidAttachmentException empty() {
    return new InvalidAttachmentException("Uploaded file is empty.", HttpStatus.BAD_REQUEST);
  }

  public static InvalidAttachmentException missingFileName() {
    return new InvalidAttachmentException("Uploaded file has no name.", HttpStatus.BAD_REQUEST);
  }

  public static InvalidAttachmentException unsupportedType(String contentType) {
    return new InvalidAttachmentException(
        "Unsupported file type: " + contentType, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }
}
