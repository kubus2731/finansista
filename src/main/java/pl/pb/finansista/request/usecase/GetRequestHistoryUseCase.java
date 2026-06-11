package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.history.ActivityLog;
import pl.pb.finansista.request.history.repository.ActivityLogRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!query.isAdminOrDean() && !request.getUser().getEmail().equals(query.userEmail())) {
            throw new AccessDeniedException("You do not have permission to view this request's history");
        }

        return activityLogRepository.findByRequestIdOrderByCreatedAtDesc(request.getId());
    }
}
