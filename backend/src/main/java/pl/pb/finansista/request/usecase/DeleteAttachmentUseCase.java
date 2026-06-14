package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.AttachmentNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.AttachmentRepository;
import pl.pb.finansista.user.RoleName;

import java.util.Collection;
import java.util.UUID;

/**
 * Removing an attachment is gated like editing the request: author (or admin),
 * and only while the request is in DRAFT / CORRECTION_REQUIRED.
 */
@Service
@RequiredArgsConstructor
public class DeleteAttachmentUseCase {

    private final AttachmentRepository attachmentRepository;

    @Transactional
    public void execute(UUID attachmentExternalId, String userEmail, Collection<String> userAuthorities) {
        Attachment attachment = attachmentRepository.findByExternalId(attachmentExternalId)
                .orElseThrow(AttachmentNotFoundException::new);
        Request request = attachment.getRequest();

        boolean isAdmin = userAuthorities.contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getEmail().equals(userEmail);
        if (!isAdmin && !isAuthor) {
            throw UnauthorizedRequestAccessException.forAction("delete attachment");
        }

        String status = request.getStatus().getName();
        boolean editableState = status.equals(RequestStatusName.DRAFT.name())
                || status.equals(RequestStatusName.CORRECTION_REQUIRED.name());
        if (!editableState) {
            throw InvalidRequestStateException.withStatusName(status);
        }

        attachmentRepository.delete(attachment);
    }
}
