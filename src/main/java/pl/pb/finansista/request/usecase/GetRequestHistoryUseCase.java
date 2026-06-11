package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.ActivityLog;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.ActivityLogRepository;
import pl.pb.finansista.request.repository.RequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRequestHistoryUseCase {

    private final RequestRepository requestRepository;
    private final ActivityLogRepository activityLogRepository;

    @Transactional(readOnly = true)
    public List<ActivityLog> execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(RequestNotFoundException::new);

        if (!query.isAdminOrDean() && !request.getUser().getEmail().equals(query.userEmail())) {
            throw UnauthorizedRequestAccessException.forAction("view history for");
        }

        return activityLogRepository.findByRequestIdOrderByCreatedAtDesc(request.getId());
    }
}
