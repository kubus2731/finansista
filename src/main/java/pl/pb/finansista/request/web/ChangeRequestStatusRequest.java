package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pb.finansista.request.usecase.ChangeRequestStatusCommand;

import java.util.List;
import java.util.UUID;

public record ChangeRequestStatusRequest(
        @NotBlank String status,
        String description
) {
    public ChangeRequestStatusCommand toCommand(UUID externalId, String userEmail, List<String> userAuthorities) {
        return new ChangeRequestStatusCommand(
                externalId,
                status,
                description,
                userEmail,
                userAuthorities
        );
    }
}
