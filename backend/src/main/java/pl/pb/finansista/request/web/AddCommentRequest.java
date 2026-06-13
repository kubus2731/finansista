package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import pl.pb.finansista.request.usecase.AddCommentCommand;

import java.util.List;
import java.util.UUID;

public record AddCommentRequest(
        @NotBlank String content
) {
    public AddCommentCommand toCommand(UUID requestExternalId, String userEmail, List<String> userAuthorities) {
        return new AddCommentCommand(
                requestExternalId,
                userEmail,
                content,
                userAuthorities
        );
    }
}
