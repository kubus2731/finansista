package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.MissingFundingSourceException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChangeRequestStatusUseCase {

  private final RequestRepository requestRepository;
  private final RequestStatusRepository requestStatusRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final RequestAccessSpecificationFactory accessSpecFactory;
  private final RequestTransitionValidator transitionValidator;

  @Transactional
  public void execute(ChangeRequestStatusCommand command) {
    User actor =
        userRepository
            .findByExternalId(command.userExternalId())
            .orElseThrow(UserNotFoundException::new);

    Specification<Request> spec =
        Specification.allOf(
            RequestSpecifications.hasExternalId(command.externalId()),
            accessSpecFactory.createForUser(actor, command.userAuthorities()));

    Request request = requestRepository.findOne(spec).orElseThrow(RequestNotFoundException::new);

    request.assertVersion(command.version());

    RequestStatus newStatus =
        requestStatusRepository
            .findByName(command.newStatusName())
            .orElseThrow(
                () -> InvalidRequestStateException.withStatusName(command.newStatusName()));

    if (newStatus.getName().equals(RequestStatusName.SUBMITTED.name())
        && request.getFundings().isEmpty()) {
      throw new MissingFundingSourceException();
    }

    transitionValidator.validateTransition(
        request, actor, command.userAuthorities(), RequestStatusName.valueOf(newStatus.getName()));

    requestRepository.setActor(actor.getId());

    request.changeStatus(newStatus);
    requestRepository.save(request);

    if (command.description() != null && !command.description().isBlank()) {
      Comment comment = new Comment(request, actor, command.description());
      commentRepository.save(comment);
    }
  }
}
