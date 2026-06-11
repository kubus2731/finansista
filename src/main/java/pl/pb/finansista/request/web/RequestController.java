package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.AddCommentUseCase;
import pl.pb.finansista.request.usecase.ChangeRequestStatusUseCase;
import pl.pb.finansista.request.usecase.CreateRequestUseCase;
import pl.pb.finansista.request.usecase.EditRequestUseCase;
import pl.pb.finansista.request.usecase.GetCommentsUseCase;
import pl.pb.finansista.request.usecase.GetRequestHistoryUseCase;
import pl.pb.finansista.request.usecase.GetRequestsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;
import pl.pb.finansista.request.usecase.GetSingleRequestUseCase;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final CreateRequestUseCase createRequestUseCase;
    private final GetRequestsUseCase getRequestsUseCase;
    private final GetSingleRequestUseCase getSingleRequestUseCase;
    private final EditRequestUseCase editRequestUseCase;
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
        Request request = createRequestUseCase.execute(payload.toCommand(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(RequestResponse.of(request));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            Authentication authentication
    ) {
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));
        
        String filterEmail = isAdminOrDean ? null : authentication.getName();

        List<Request> requests = getRequestsUseCase.execute(filterEmail, status, departmentId);
        
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
        Request request = editRequestUseCase.execute(payload.toCommand(id, authentication.getName()));
        return ResponseEntity.ok(RequestResponse.of(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRequestStatusRequest payload,
            Authentication authentication
    ) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        changeRequestStatusUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ActivityLogResponse>> getHistory(
            @PathVariable UUID id,
            Authentication authentication
    ) {
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
        List<String> authorities = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        pl.pb.finansista.request.Comment comment = addCommentUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        boolean isAdminOrDean = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEAN_OFFICE"));

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        
        List<CommentResponse> comments = getCommentsUseCase.execute(query).stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(comments);
    }
}
