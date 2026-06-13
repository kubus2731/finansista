package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteRequestUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessValidator accessValidator;

    @Transactional
    public void execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User user = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        accessValidator.validateUserCanAccessRequest(request, user, query.userAuthorities());

        boolean isAdmin = query.userAuthorities().contains(RoleName.ROLE_ADMIN.name());

        if (!isAdmin && !request.getStatus().getName().equals(RequestStatusName.DRAFT.name())) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        requestRepository.delete(request);
    }
}
