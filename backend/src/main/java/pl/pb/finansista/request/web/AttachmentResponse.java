package pl.pb.finansista.request.web;

import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.Attachment;

import java.time.ZonedDateTime;

public record AttachmentResponse(
        String id,
        String fileName,
        String fileUrl,
        ZonedDateTime createdAt
) {
    public static AttachmentResponse of(Attachment attachment) {
        return new AttachmentResponse(
                ExternalIdEncoder.encode("att", attachment.getExternalId()),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getCreatedAt()
        );
    }
}
