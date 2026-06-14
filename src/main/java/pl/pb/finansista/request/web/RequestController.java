package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.common.web.ETags;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final CreateRequestUseCase createRequestUseCase;
    private final GetRequestsUseCase getRequestsUseCase;
    private final GetSingleRequestUseCase getSingleRequestUseCase;
    private final EditRequestUseCase editRequestUseCase;
    private final DeleteRequestUseCase deleteRequestUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody CreateRequestRequest payload,
            Authentication authentication
    ) {
        log.info("Creating new request for user: {}", authentication.getName());
        Request request = createRequestUseCase.execute(payload.toCommand(authentication.getName()));
        log.info("Successfully created request with ID: {}", request.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(RequestResponse.of(request));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 1000, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ) {
        log.info("Fetching requests for user: {} (page={}, size={})",
                authentication.getName(), pageable.getPageNumber(), pageable.getPageSize());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetRequestsQuery query = new GetRequestsQuery(
                authentication.getName(),
                authorities,
                status,
                departmentId,
                search
        );

        var page = getRequestsUseCase.execute(query, pageable);
        log.info("Found {} requests on page {} (total elements={}) for user: {}",
                page.getNumberOfElements(), page.getNumber(), page.getTotalElements(),
                authentication.getName());

        List<RequestResponse> response = page.getContent().stream()
                .map(RequestResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequest(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching details for request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), authorities);
        Request request = getSingleRequestUseCase.execute(query);
        return ResponseEntity.ok()
                .eTag(ETags.format(request.getVersion()))
                .body(RequestResponse.of(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponse> editRequest(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @RequestBody EditRequestRequest payload,
            Authentication authentication
    ) {
        log.info("Editing request ID: {} by user: {}", id, authentication.getName());
        Long version = ETags.parseIfMatch(ifMatch);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Request request = editRequestUseCase.execute(payload.toCommand(id, authentication.getName(), authorities, version));
        log.info("Successfully edited request ID: {}", id);
        return ResponseEntity.ok()
                .eTag(ETags.format(request.getVersion()))
                .body(RequestResponse.of(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Deleting request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), authorities);
        deleteRequestUseCase.execute(query);
        log.info("Successfully deleted request ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
