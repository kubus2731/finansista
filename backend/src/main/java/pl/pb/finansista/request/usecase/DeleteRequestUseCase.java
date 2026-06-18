package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteRequestUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecFactory;

    @Transactional
    public void execute(GetSingleRequestQuery query) {
        User user = userRepository.findByExternalId(query.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = Specification.allOf(
                RequestSpecifications.hasExternalId(query.externalId()),
                accessSpecFactory.createForUser(user, query.userAuthorities())
        );

        Request request = requestRepository.findOne(spec)
                .orElseThrow(RequestNotFoundException::new);

        boolean isAdmin = query.userAuthorities().contains(RoleName.ROLE_ADMIN.name());

        if (!isAdmin && !request.getStatus().getName().equals(RequestStatusName.DRAFT.name())) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        requestRepository.delete(request);
    }
}
