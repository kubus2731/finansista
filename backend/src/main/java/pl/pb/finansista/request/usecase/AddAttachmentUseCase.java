package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.AttachmentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.RoleName;

/**
 * Attachments are the applicant's supporting documents (Załącznik, sekcja V), i.e.
 * request content - so adding one follows the same rule as editing: only the author
 * (or admin), and only while the request is still in their hands (DRAFT / CORRECTION_REQUIRED).
 */
@Service
@RequiredArgsConstructor
public class AddAttachmentUseCase {

    private final RequestRepository requestRepository;
    private final AttachmentRepository attachmentRepository;

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

        Attachment attachment = new Attachment(request, command.fileName(), command.fileUrl());
        return attachmentRepository.save(attachment);
    }

    private boolean isEditableState(Request request) {
        String status = request.getStatus().getName();
        return status.equals(RequestStatusName.DRAFT.name())
                || status.equals(RequestStatusName.CORRECTION_REQUIRED.name());
    }
}
