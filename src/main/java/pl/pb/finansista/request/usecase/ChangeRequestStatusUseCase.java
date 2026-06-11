package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.ActivityLog;
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
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RequestStatus newStatus = requestStatusRepository.findByName(command.newStatusName())
                .orElseThrow(() -> new IllegalArgumentException("Status not found: " + command.newStatusName()));

        boolean isAdminOrDean = command.userAuthorities().stream()
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_DEAN_OFFICE"));

        // Basic permission check
        if (!isAdminOrDean && !request.getUser().getEmail().equals(command.userEmail())) {
            throw new AccessDeniedException("You do not have permission to change the status of this request");
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
