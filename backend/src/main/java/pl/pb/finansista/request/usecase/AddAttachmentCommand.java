package pl.pb.finansista.request.usecase;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public record AddAttachmentCommand(
        UUID requestExternalId,
        UUID userExternalId,
        String fileName,
        String contentType,
        InputStream content,
        long sizeBytes,
        List<String> userAuthorities
) {
}
