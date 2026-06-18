package pl.pb.finansista.request.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.usecase.AddAttachmentCommand;
import pl.pb.finansista.request.usecase.AddAttachmentUseCase;
import pl.pb.finansista.request.usecase.AttachmentDownload;
import pl.pb.finansista.request.usecase.DeleteAttachmentUseCase;
import pl.pb.finansista.request.usecase.GetAttachmentContentUseCase;
import pl.pb.finansista.request.usecase.GetAttachmentsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
    private final GetAttachmentContentUseCase getAttachmentContentUseCase;
    private final DeleteAttachmentUseCase deleteAttachmentUseCase;

    @PostMapping(path = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AttachmentResponse> addAttachment(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Adding attachment to request ID: {} by user: {}", id, userId);

        try (InputStream content = file.getInputStream()) {
            AddAttachmentCommand command = new AddAttachmentCommand(
                    id,
                    userId,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    content,
                    file.getSize(),
                    authorities(authentication));

            Attachment attachment = addAttachmentUseCase.execute(command);
            log.info("Successfully added attachment with ID: {}", attachment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(AttachmentResponse.of(attachment));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read uploaded file", e);
        }
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Fetching attachments for request ID: {} by user: {}", id, userId);
        GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities(authentication));

        List<AttachmentResponse> attachments = getAttachmentsUseCase.execute(query).stream()
                .map(AttachmentResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/{id}/attachments/{attachmentId}/content")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable UUID id,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Downloading attachment {} of request {} by user: {}", attachmentId, id, userId);
        GetSingleRequestQuery query = new GetSingleRequestQuery(id, userId, authorities(authentication));

        AttachmentDownload download = getAttachmentContentUseCase.execute(query, attachmentId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .contentLength(download.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(download.content());
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID id,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication
    ) {
        log.info("Deleting attachment {} from request {} by user: {}", attachmentId, id, userId);
        deleteAttachmentUseCase.execute(id, attachmentId, userId, authorities(authentication));
        return ResponseEntity.noContent().build();
    }

    private List<String> authorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
