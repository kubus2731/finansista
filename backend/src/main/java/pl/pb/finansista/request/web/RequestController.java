package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final GrantFundingUseCase grantFundingUseCase;
    private final RecordProvostOpinionUseCase recordProvostOpinionUseCase;
    private final RequestResponseAssembler responseAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody CreateRequestRequest payload,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Creating new request for user: {}", userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Request request = createRequestUseCase.execute(payload.toCommand(userId));
        log.info("Successfully created request with ID: {}", request.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseAssembler.toResponse(request, userId, authorities));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 1000, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Fetching requests for user: {} (page={}, size={})",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetRequestsQuery query = new GetRequestsQuery(
                userId,
                authorities,
                status,
                departmentId,
                search
        );

        Page<Request> page = getRequestsUseCase.execute(query, pageable);
        log.info("Found {} requests on page {} (total elements={}) for user: {}",
                page.getNumberOfElements(), page.getNumber(), page.getTotalElements(),
                userId);

        List<RequestResponse> response =
                responseAssembler.toResponses(page.getContent(), userId, authorities);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Fetching details for request ID: {} by user: {}", id, userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities);
        Request request = getSingleRequestUseCase.execute(query);
        return ResponseEntity.ok()
                .eTag(ETags.format(request.getVersion()))
                .body(responseAssembler.toResponse(request, userId, authorities));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponse> editRequest(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @RequestBody EditRequestRequest payload,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Editing request ID: {} by user: {}", id, userId);
        Long version = ETags.parseIfMatch(ifMatch);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Request request = editRequestUseCase.execute(payload.toCommand(id, userId, authorities, version));
        log.info("Successfully edited request ID: {}", id);
        return ResponseEntity.ok()
                .eTag(ETags.format(request.getVersion()))
                .body(responseAssembler.toResponse(request, userId, authorities));
    }

    @PutMapping("/{id}/fundings/{fundingSourceId}/grant")
    public ResponseEntity<Void> grantFunding(
            @PathVariable UUID id,
            @PathVariable Long fundingSourceId,
            @Valid @RequestBody GrantFundingRequest payload,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Granting funding source {} on request {} by user: {}", fundingSourceId, id, userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        grantFundingUseCase.execute(new GrantFundingCommand(
                id, fundingSourceId, payload.amountGranted(), userId, authorities));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/provost-opinion")
    public ResponseEntity<Void> recordProvostOpinion(
            @PathVariable UUID id,
            @Valid @RequestBody RecordProvostOpinionRequest payload,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Recording provost opinion on request {} by user: {}", id, userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        recordProvostOpinionUseCase.execute(new RecordProvostOpinionCommand(
                id, payload.opinion(), userId, authorities));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Deleting request ID: {} by user: {}", id, userId);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities);
        deleteRequestUseCase.execute(query);
        log.info("Successfully deleted request ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
