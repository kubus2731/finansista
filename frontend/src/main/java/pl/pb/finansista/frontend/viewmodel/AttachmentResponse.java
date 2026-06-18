package pl.pb.finansista.frontend.viewmodel;

import java.time.ZonedDateTime;

public record AttachmentResponse(
        String id,
        String fileName,
        String contentType,
        long sizeBytes,
        ZonedDateTime createdAt
) {
    
}

