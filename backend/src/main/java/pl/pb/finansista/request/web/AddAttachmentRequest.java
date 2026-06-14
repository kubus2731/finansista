package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import pl.pb.finansista.request.usecase.AddAttachmentCommand;

import java.util.List;
import java.util.UUID;

public record AddAttachmentRequest(
        @NotBlank String fileName,
        @NotBlank String fileUrl
) {
    public AddAttachmentCommand toCommand(UUID requestExternalId, String userEmail, List<String> userAuthorities) {
        return new AddAttachmentCommand(
                requestExternalId,
                userEmail,
                fileName,
                fileUrl,
                userAuthorities
        );
    }
}
