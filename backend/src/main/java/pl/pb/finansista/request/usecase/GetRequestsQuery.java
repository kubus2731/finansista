package pl.pb.finansista.request.usecase;

import java.util.List;

public record GetRequestsQuery(
        String userEmail,
        List<String> userAuthorities,
        String status,
        Long departmentId,
        String search
) {
}
