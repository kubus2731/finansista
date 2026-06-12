package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import pl.pb.finansista.user.usecase.ChangeUserDepartmentCommand;

import java.util.UUID;

public record ChangeUserDepartmentRequest(
        @NotNull Long departmentId
) {
    public ChangeUserDepartmentCommand toCommand(UUID userExternalId) {
        return new ChangeUserDepartmentCommand(userExternalId, departmentId);
    }
}
