package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AddCommentUseCase {

    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestAccessValidator accessValidator;

    @Transactional
    public Comment execute(AddCommentCommand command) {
        Request request = requestRepository.findByExternalId(command.requestExternalId())
                .orElseThrow(RequestNotFoundException::new);

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(UserNotFoundException::new);

        accessValidator.validateUserCanAccessRequest(request, actor, command.userAuthorities());

        Comment comment = new Comment(request, actor, command.content());
        return commentRepository.save(comment);
    }
}
