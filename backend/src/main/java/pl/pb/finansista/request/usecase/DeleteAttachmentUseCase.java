package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.common.storage.FileStorage;
import pl.pb.finansista.common.AfterTransaction;
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

@Service
@RequiredArgsConstructor
public class DeleteAttachmentUseCase {

    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;

    @Transactional
    public void execute(UUID requestExternalId, UUID attachmentExternalId, UUID userExternalId, Collection<String> userAuthorities) {
        Attachment attachment = attachmentRepository.findByExternalId(attachmentExternalId)
                .orElseThrow(AttachmentNotFoundException::new);
        Request request = attachment.getRequest();

        if (!request.getExternalId().equals(requestExternalId)) {
            throw new AttachmentNotFoundException();
        }

        boolean isAdmin = userAuthorities.contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getExternalId().equals(userExternalId);
        if (!isAdmin && !isAuthor) {
            throw UnauthorizedRequestAccessException.forAction("delete attachment");
        }

        String status = request.getStatus().getName();
        boolean editableState = status.equals(RequestStatusName.DRAFT.name())
                || status.equals(RequestStatusName.CORRECTION_REQUIRED.name());
        if (!editableState) {
            throw InvalidRequestStateException.withStatusName(status);
        }

        String storageKey = attachment.getStorageKey();
        attachmentRepository.delete(attachment);

        AfterTransaction.onCommit(() -> fileStorage.delete(storageKey));
    }
}
