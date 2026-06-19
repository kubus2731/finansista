package pl.pb.finansista.request.usecase;

import org.springframework.core.io.Resource;

public record AttachmentDownload(
    String fileName, String contentType, long sizeBytes, Resource content) {}
