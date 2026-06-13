package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.Collection;

import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.user.RoleName;

@Service
@RequiredArgsConstructor
public class ChangeRequestStatusUseCase {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestAccessValidator accessValidator;
    private final RequestTransitionValidator transitionValidator;

    @Transactional
    public void execute(ChangeRequestStatusCommand command) {
        Request request = requestRepository.findByExternalId(command.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(UserNotFoundException::new);

        accessValidator.validateUserCanAccessRequest(request, actor, command.userAuthorities());

        RequestStatus newStatus = requestStatusRepository.findByName(command.newStatusName())
                .orElseThrow(() -> InvalidRequestStateException.withStatusName(command.newStatusName()));

        if (newStatus.getName().equals(RequestStatusName.SUBMITTED.name()) && request.getFundingSource() == null) {
            throw new IllegalStateException("Funding source must be provided before submitting the request.");
        }

        transitionValidator.validateTransition(request, actor, command.userAuthorities(), RequestStatusName.valueOf(newStatus.getName()));

        requestRepository.setActor(actor.getId());

        request.changeStatus(newStatus);
        requestRepository.save(request);

        if (command.description() != null && !command.description().isBlank()) {
            Comment comment = new Comment(request, actor, command.description());
            commentRepository.save(comment);
        }
    }
}
