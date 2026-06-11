package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.*;

import java.util.List;
import java.util.Objects;
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
    private final ChangeRequestStatusUseCase changeRequestStatusUseCase;
    private final GetRequestHistoryUseCase getRequestHistoryUseCase;
    private final AddCommentUseCase addCommentUseCase;
    private final GetCommentsUseCase getCommentsUseCase;

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
            Authentication authentication
    ) {
        log.info("Fetching requests for user: {}", authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetRequestsQuery query = new GetRequestsQuery(
                authentication.getName(),
                authorities,
                status,
                departmentId
        );

        List<Request> requests = getRequestsUseCase.execute(query);
        log.info("Found {} requests for user: {}", requests.size(), authentication.getName());
        
        List<RequestResponse> response = requests.stream()
                .map(RequestResponse::of)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequest(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching details for request ID: {} by user: {}", id, authentication.getName());
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        Request request = getSingleRequestUseCase.execute(query);
        return ResponseEntity.ok(RequestResponse.of(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponse> editRequest(
            @PathVariable UUID id,
            @Valid @RequestBody EditRequestRequest payload,
            Authentication authentication
    ) {
        log.info("Editing request ID: {} by user: {}", id, authentication.getName());
        Request request = editRequestUseCase.execute(payload.toCommand(id, authentication.getName()));
        log.info("Successfully edited request ID: {}", id);
        return ResponseEntity.ok(RequestResponse.of(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRequestStatusRequest payload,
            Authentication authentication
    ) {
        log.info("Changing status for request ID: {} to {} by user: {}", id, payload.status(), authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        changeRequestStatusUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        log.info("Successfully changed status for request ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ActivityLogResponse>> getHistory(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching history for request ID: {} by user: {}", id, authentication.getName());
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        
        List<ActivityLogResponse> history = getRequestHistoryUseCase.execute(query).stream()
                .map(ActivityLogResponse::of)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody AddCommentRequest payload,
            Authentication authentication
    ) {
        log.info("Adding comment to request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Comment comment = addCommentUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        log.info("Successfully added comment with ID: {}", comment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching comments for request ID: {} by user: {}", id, authentication.getName());
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        
        List<CommentResponse> comments = getCommentsUseCase.execute(query).stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Deleting request ID: {} by user: {}", id, authentication.getName());
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        deleteRequestUseCase.execute(query);
        log.info("Successfully deleted request ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
