package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.common.web.ETags;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.usecase.*;

@RestController
@RequestMapping("/api/v1/request-templates")
@RequiredArgsConstructor
public class RequestTemplateController {

    private final GetActiveRequestTemplatesUseCase getActiveRequestTemplatesUseCase;
    private final GetAllRequestTemplatesUseCase getAllRequestTemplatesUseCase;
    private final GetSingleRequestTemplateUseCase getSingleRequestTemplateUseCase;
    private final CreateRequestTemplateUseCase createRequestTemplateUseCase;
    private final EditRequestTemplateUseCase editRequestTemplateUseCase;
    private final DeleteRequestTemplateUseCase deleteRequestTemplateUseCase;

    @GetMapping
    public ResponseEntity<List<RequestTemplateResponse>> getActiveTemplates() {
        return ResponseEntity.ok(
                getActiveRequestTemplatesUseCase.execute().stream()
                        .map(RequestTemplateResponse::of)
                        .toList()
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RequestTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(
                getAllRequestTemplatesUseCase.execute().stream()
                        .map(RequestTemplateResponse::of)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestTemplateResponse> getTemplate(@PathVariable UUID id) {
        RequestTemplate template = getSingleRequestTemplateUseCase.execute(id);
        return ResponseEntity.ok()
                .eTag(ETags.format(template.getVersion()))
                .body(RequestTemplateResponse.of(template));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RequestTemplateResponse> createTemplate(@Valid @RequestBody CreateRequestTemplateRequest request) {
        RequestTemplate template = createRequestTemplateUseCase.execute(request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(RequestTemplateResponse.of(template));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RequestTemplateResponse> editTemplate(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @RequestBody EditRequestTemplateRequest request
    ) {
        Long version = ETags.parseIfMatch(ifMatch);
        RequestTemplate template = editRequestTemplateUseCase.execute(id, request.title(), request.description(), request.active(), version);
        return ResponseEntity.ok()
                .eTag(ETags.format(template.getVersion()))
                .body(RequestTemplateResponse.of(template));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        deleteRequestTemplateUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
