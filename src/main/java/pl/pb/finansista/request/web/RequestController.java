package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.CreateRequestUseCase;

import pl.pb.finansista.request.usecase.GetRequestsUseCase;
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
            @RequestParam(required = false) UUID departmentId,
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
}
