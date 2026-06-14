package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.usecase.AddAttachmentUseCase;
import pl.pb.finansista.request.usecase.DeleteAttachmentUseCase;
import pl.pb.finansista.request.usecase.GetAttachmentsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestAttachmentController {

    private final AddAttachmentUseCase addAttachmentUseCase;
    private final GetAttachmentsUseCase getAttachmentsUseCase;
    private final DeleteAttachmentUseCase deleteAttachmentUseCase;

    @PostMapping("/{id}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AttachmentResponse> addAttachment(
            @PathVariable UUID id,
            @Valid @RequestBody AddAttachmentRequest payload,
            Authentication authentication
    ) {
        log.info("Adding attachment to request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authorities(authentication);

        Attachment attachment = addAttachmentUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        log.info("Successfully added attachment with ID: {}", attachment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AttachmentResponse.of(attachment));
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching attachments for request ID: {} by user: {}", id, authentication.getName());
        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), authorities(authentication));

        List<AttachmentResponse> attachments = getAttachmentsUseCase.execute(query).stream()
                .map(AttachmentResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID id,
            @PathVariable UUID attachmentId,
            Authentication authentication
    ) {
        log.info("Deleting attachment {} from request {} by user: {}", attachmentId, id, authentication.getName());
        deleteAttachmentUseCase.execute(attachmentId, authentication.getName(), authorities(authentication));
        return ResponseEntity.noContent().build();
    }

    private List<String> authorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
