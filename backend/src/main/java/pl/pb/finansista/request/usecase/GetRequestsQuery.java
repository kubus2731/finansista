package pl.pb.finansista.request.usecase;

import java.util.List;
import java.util.UUID;

public record GetRequestsQuery(
        UUID userExternalId,
        List<String> userAuthorities,
        String status,
        Long departmentId,
        String search
) {
}
