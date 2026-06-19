package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AddCommentUseCase {

  private final RequestRepository requestRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final RequestAccessSpecificationFactory accessSpecFactory;

  @Transactional
  public Comment execute(AddCommentCommand command) {
    User actor =
        userRepository
            .findByExternalId(command.userExternalId())
            .orElseThrow(UserNotFoundException::new);

    Specification<Request> spec =
        Specification.allOf(
            RequestSpecifications.hasExternalId(command.requestExternalId()),
            accessSpecFactory.createForUser(actor, command.userAuthorities()));

    Request request = requestRepository.findOne(spec).orElseThrow(RequestNotFoundException::new);

    Comment comment = new Comment(request, actor, command.content());
    return commentRepository.save(comment);
  }
}
