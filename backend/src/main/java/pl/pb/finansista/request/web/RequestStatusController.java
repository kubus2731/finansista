package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.common.web.ETags;
import pl.pb.finansista.request.usecase.ChangeRequestStatusUseCase;
import pl.pb.finansista.request.usecase.GetAllRequestStatusesUseCase;
import pl.pb.finansista.request.usecase.GetAvailableTransitionsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

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
            .toList());
  }

  @GetMapping("/{id}/status/available-transitions")
  public ResponseEntity<List<String>> getAvailableTransitions(
      @PathVariable UUID id, @AuthenticationPrincipal UUID userId, Authentication authentication) {
    log.info("Fetching available transitions for request ID: {} by user: {}", id, userId);
    List<String> authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities);
    List<String> available = getAvailableTransitionsUseCase.execute(query);
    return ResponseEntity.ok(available);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> changeStatus(
      @PathVariable UUID id,
      @RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
      @Valid @RequestBody ChangeRequestStatusRequest payload,
      @AuthenticationPrincipal UUID userId,
      Authentication authentication) {
    log.info("Changing status for request ID: {} to {} by user: {}", id, payload.status(), userId);
    List<String> authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    Long version = ETags.parseIfMatch(ifMatch);
    changeRequestStatusUseCase.execute(payload.toCommand(id, userId, authorities, version));
    log.info("Successfully changed status for request ID: {}", id);
    return ResponseEntity.noContent().build();
  }
}
