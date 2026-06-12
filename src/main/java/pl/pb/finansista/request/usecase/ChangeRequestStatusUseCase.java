package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ChangeRequestStatusUseCase {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestAccessValidator accessValidator;

    @Transactional
    public void execute(ChangeRequestStatusCommand command) {
        Request request = requestRepository.findByExternalId(command.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(UserNotFoundException::new);

        accessValidator.validateUserCanAccessRequest(request, actor, command.userAuthorities());

        RequestStatus newStatus = requestStatusRepository.findByName(command.newStatusName())
                .orElseThrow(() -> InvalidRequestStateException.withStatusName(command.newStatusName()));

        boolean isAdmin = command.userAuthorities().stream()
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            validateRoutingRules(request, actor, command.userAuthorities(), newStatus);
        }

        request.changeStatus(newStatus);
        requestRepository.save(request);

        if (command.description() != null && !command.description().isBlank()) {
            Comment comment = new Comment(request, actor, command.description());
            commentRepository.save(comment);
        }
    }

    private void validateRoutingRules(Request request, User actor, Collection<String> roles, RequestStatus newStatus) {
        String oldStatus = request.getStatus().getName();
        String targetStatus = newStatus.getName();
        String fundingSource = request.getFundingSource() != null ? request.getFundingSource().getName() : "";
        boolean isAuthor = request.getUser().getId().equals(actor.getId());
        boolean isSameDepartment = request.getDepartment().getId().equals(actor.getDepartment().getId());

        if (targetStatus.equals("SUBMITTED")) {
            if (!isAuthor) {
                throw UnauthorizedRequestAccessException.forAction("submit");
            }
            return;
        }

        if (oldStatus.equals("SUBMITTED")) {
            if (!roles.contains("ROLE_DEAN_OFFICE") || !isSameDepartment) {
                throw UnauthorizedRequestAccessException.forAction("formally evaluate");
            }
            return;
        }

        if (oldStatus.equals("FORMAL_EVALUATION")) {
            if (fundingSource.equals("Dziekan Wydziału")) {
                if (!roles.contains("ROLE_DEAN_OFFICE") || !isSameDepartment) {
                    throw UnauthorizedRequestAccessException.forAction("review merit for Dean's funds");
                }
            } else {
                if (!roles.contains("ROLE_WRSS") && !roles.contains("ROLE_LEGAL_COMMISSION")) {
                    throw UnauthorizedRequestAccessException.forAction("review merit");
                }
            }
            return;
        }

        if (oldStatus.equals("UNDER_REVIEW")) {
            if (fundingSource.equals("Dziekan Wydziału")) {
                if (!roles.contains("ROLE_FINANCE_OFFICE") && !roles.contains("ROLE_DEAN_OFFICE")) {
                    throw UnauthorizedRequestAccessException.forAction("accept Dean's funds");
                }
            } else {
                if (!roles.contains("ROLE_FINANCE_OFFICE")) {
                    throw UnauthorizedRequestAccessException.forAction("accept this request");
                }
            }
            return;
        }

        throw UnauthorizedRequestAccessException.forAction("perform this status transition");
    }
}
