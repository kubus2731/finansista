package pl.pb.finansista.request.web;

import java.time.ZonedDateTime;
import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.Attachment;

public record AttachmentResponse(
    String id, String fileName, String contentType, long sizeBytes, ZonedDateTime createdAt) {
  public static AttachmentResponse of(Attachment attachment) {
    return new AttachmentResponse(
        ExternalIdEncoder.encode("att", attachment.getExternalId()),
        attachment.getFileName(),
        attachment.getContentType(),
        attachment.getSizeBytes(),
        attachment.getCreatedAt());
  }
}
