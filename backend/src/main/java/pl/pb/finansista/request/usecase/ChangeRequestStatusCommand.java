package pl.pb.finansista.request.usecase;

import java.util.List;
import java.util.UUID;

public record ChangeRequestStatusCommand(
    UUID externalId,
    String newStatusName,
    String description,
    UUID userExternalId,
    List<String> userAuthorities,
    Long version) {}
