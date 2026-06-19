package pl.pb.finansista.request.usecase;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.common.storage.FileStorage;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.AttachmentNotFoundException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.AttachmentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GetAttachmentContentUseCase {

  private final AttachmentRepository attachmentRepository;
  private final RequestRepository requestRepository;
  private final UserRepository userRepository;
  private final RequestAccessSpecificationFactory accessSpecFactory;
  private final FileStorage fileStorage;

  @Transactional(readOnly = true)
  public AttachmentDownload execute(GetSingleRequestQuery query, UUID attachmentExternalId) {
    User user =
        userRepository
            .findByExternalId(query.userExternalId())
            .orElseThrow(UserNotFoundException::new);

    Specification<Request> spec =
        Specification.allOf(
            RequestSpecifications.hasExternalId(query.externalId()),
            accessSpecFactory.createForUser(user, query.userAuthorities()));

    Request request = requestRepository.findOne(spec).orElseThrow(RequestNotFoundException::new);

    Attachment attachment =
        attachmentRepository
            .findByExternalId(attachmentExternalId)
            .orElseThrow(AttachmentNotFoundException::new);

    if (!attachment.getRequest().getId().equals(request.getId())) {
      throw new AttachmentNotFoundException();
    }

    Resource content = fileStorage.load(attachment.getStorageKey());
    return new AttachmentDownload(
        attachment.getFileName(), attachment.getContentType(), attachment.getSizeBytes(), content);
  }
}
