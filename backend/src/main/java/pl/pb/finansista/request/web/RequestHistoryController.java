package pl.pb.finansista.request.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import pl.pb.finansista.request.usecase.GetRequestHistoryUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestHistoryController {
    private final GetRequestHistoryUseCase getRequestHistoryUseCase;

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ActivityLogResponse>> getHistory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Fetching history for request ID: {} by user: {}", id, userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities);

        List<ActivityLogResponse> history = getRequestHistoryUseCase.execute(query).stream()
                .map(ActivityLogResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }
}
