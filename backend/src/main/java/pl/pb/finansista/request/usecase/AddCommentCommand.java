package pl.pb.finansista.request.usecase;

import java.util.List;
import java.util.UUID;

public record AddCommentCommand(
    UUID requestExternalId, UUID userExternalId, String content, List<String> userAuthorities) {}
