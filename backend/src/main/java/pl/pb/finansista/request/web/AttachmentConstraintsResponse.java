package pl.pb.finansista.request.web;

import java.util.Set;

public record AttachmentConstraintsResponse(
    Set<String> allowedContentTypes, long maxFileSizeBytes) {}
