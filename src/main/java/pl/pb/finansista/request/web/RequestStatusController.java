package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.usecase.ChangeRequestStatusUseCase;
import pl.pb.finansista.request.usecase.GetAllRequestStatusesUseCase;
import pl.pb.finansista.request.usecase.GetAvailableTransitionsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestStatusController {

    private final ChangeRequestStatusUseCase changeRequestStatusUseCase;
    private final GetAvailableTransitionsUseCase getAvailableTransitionsUseCase;
    private final GetAllRequestStatusesUseCase getAllRequestStatusesUseCase;

    @GetMapping("/statuses")
    public ResponseEntity<List<RequestStatusResponse>> getAllStatuses() {
        return ResponseEntity.ok(
                getAllRequestStatusesUseCase.execute().stream()
                        .map(st -> new RequestStatusResponse(st.getId(), st.getName()))
                        .toList()
        );
    }

    @GetMapping("/{id}/status/available-transitions")
    public ResponseEntity<List<String>> getAvailableTransitions(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching available transitions for request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), authorities);
        List<String> available = getAvailableTransitionsUseCase.execute(query);
        return ResponseEntity.ok(available);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @RequestBody ChangeRequestStatusRequest payload,
            Authentication authentication
    ) {
        log.info("Changing status for request ID: {} to {} by user: {}", id, payload.status(), authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long version = parseIfMatch(ifMatch);
        changeRequestStatusUseCase.execute(payload.toCommand(id, authentication.getName(), authorities, version));
        log.info("Successfully changed status for request ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    private Long parseIfMatch(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) throw new IllegalArgumentException("If-Match header is required");
        try {
            return Long.parseLong(ifMatch.replace("\"", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid If-Match header format");
        }
    }
}