package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.ActivityLog;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.ActivityLogRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChangeRequestStatusUseCase {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(ChangeRequestStatusCommand command) {
        Request request = requestRepository.findByExternalId(command.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(UserNotFoundException::new);

        RequestStatus newStatus = requestStatusRepository.findByName(command.newStatusName())
                .orElseThrow(() -> InvalidRequestStateException.withStatusName(command.newStatusName()));

        boolean isAdminOrDean = command.userAuthorities().stream()
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_DEAN_OFFICE"));

        // Basic permission check
        if (!isAdminOrDean && !request.getUser().getEmail().equals(command.userEmail())) {
            throw UnauthorizedRequestAccessException.forAction("change the status of");
        }

        // Create Activity Log
        ActivityLog log = new ActivityLog();
        log.setRequest(request);
        log.setUser(actor);
        log.setOldStatus(request.getStatus());
        log.setNewStatus(newStatus);
        log.setDescription(command.description());

        // Update Request
        request.changeStatus(newStatus);

        // Save
        activityLogRepository.save(log);
        requestRepository.save(request);
    }
}
