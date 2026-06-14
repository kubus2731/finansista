package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.pb.finansista.common.storage.FileStorage;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidAttachmentException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.AttachmentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.RoleName;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AddAttachmentUseCase {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final RequestRepository requestRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;

    @Transactional
    public Attachment execute(AddAttachmentCommand command) {
        Request request = requestRepository.findByExternalId(command.requestExternalId())
                .orElseThrow(RequestNotFoundException::new);

        boolean isAdmin = command.userAuthorities().contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getEmail().equals(command.userEmail());
        if (!isAdmin && !isAuthor) {
            throw UnauthorizedRequestAccessException.forAction("add an attachment");
        }

        if (!isEditableState(request)) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        validate(command);

        String storageKey = fileStorage.store(command.content(), command.fileName());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    fileStorage.delete(storageKey);
                }
            }
        });

        Attachment attachment = new Attachment(
                request, command.fileName(), storageKey, command.contentType(), command.content().length);
        return attachmentRepository.save(attachment);
    }

    private void validate(AddAttachmentCommand command) {
        if (command.fileName() == null || command.fileName().isBlank()) {
            throw InvalidAttachmentException.missingFileName();
        }
        if (command.content() == null || command.content().length == 0) {
            throw InvalidAttachmentException.empty();
        }
        if (!ALLOWED_CONTENT_TYPES.contains(command.contentType())) {
            throw InvalidAttachmentException.unsupportedType(command.contentType());
        }
    }

    private boolean isEditableState(Request request) {
        String status = request.getStatus().getName();
        return status.equals(RequestStatusName.DRAFT.name())
                || status.equals(RequestStatusName.CORRECTION_REQUIRED.name());
    }
}
