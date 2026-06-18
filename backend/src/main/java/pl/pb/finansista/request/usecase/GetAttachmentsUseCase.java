package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Attachment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.AttachmentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAttachmentsUseCase {

    private final AttachmentRepository attachmentRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecFactory;

    @Transactional(readOnly = true)
    public List<Attachment> execute(GetSingleRequestQuery query) {
        User user = userRepository.findByExternalId(query.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = Specification.allOf(
                RequestSpecifications.hasExternalId(query.externalId()),
                accessSpecFactory.createForUser(user, query.userAuthorities())
        );

        Request request = requestRepository.findOne(spec)
                .orElseThrow(RequestNotFoundException::new);

        return attachmentRepository.findByRequestId(request.getId());
    }
}
