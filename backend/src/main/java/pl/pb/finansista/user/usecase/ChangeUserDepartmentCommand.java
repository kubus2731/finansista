package pl.pb.finansista.user.usecase;

import java.util.UUID;

public record ChangeUserDepartmentCommand(UUID userExternalId, Long newDepartmentId) {}
