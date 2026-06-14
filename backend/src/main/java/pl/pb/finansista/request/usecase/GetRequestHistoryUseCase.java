package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.ActivityLog;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.ActivityLogRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRequestHistoryUseCase {

    private final ActivityLogRepository activityLogRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecFactory;

    @Transactional(readOnly = true)
    public List<ActivityLog> execute(GetSingleRequestQuery query) {
        User user = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = Specification.allOf(
                RequestSpecifications.hasExternalId(query.externalId()),
                accessSpecFactory.createForUser(user, query.userAuthorities())
        );

        Request request = requestRepository.findOne(spec)
                .orElseThrow(RequestNotFoundException::new);

        return activityLogRepository.findByRequestIdOrderByCreatedAtDesc(request.getId());
    }
}
