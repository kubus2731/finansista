package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import pl.pb.finansista.request.usecase.ChangeRequestStatusCommand;

public record ChangeRequestStatusRequest(
        @NotBlank String status,
        String description
) {
    public ChangeRequestStatusCommand toCommand(UUID externalId, UUID userExternalId, List<String> userAuthorities, Long version) {
        return new ChangeRequestStatusCommand(
                externalId,
                status,
                description,
                userExternalId,
                userAuthorities,
                version
        );
    }
}
