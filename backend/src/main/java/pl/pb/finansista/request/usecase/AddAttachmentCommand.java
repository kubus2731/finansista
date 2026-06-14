package pl.pb.finansista.request.usecase;

import java.util.List;
import java.util.UUID;

public record AddAttachmentCommand(
        UUID requestExternalId,
        String userEmail,
        String fileName,
        String contentType,
        byte[] content,
        List<String> userAuthorities
) {
}
