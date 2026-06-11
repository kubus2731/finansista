package pl.pb.finansista.request.usecase;

import java.util.UUID;
import java.util.List;

public record AddCommentCommand(
        UUID requestExternalId,
        String userEmail,
        String content,
        List<String> userAuthorities
) {
}
