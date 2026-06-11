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

    @Transactional
    public Comment execute(AddCommentCommand command) {
        Request request = requestRepository.findByExternalId(command.requestExternalId())
                .orElseThrow(() -> RequestNotFoundException.withExternalId(command.requestExternalId()));

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> UserNotFoundException.withEmail(command.userEmail()));

        boolean isAdminOrDean = command.userAuthorities().stream()
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_DEAN_OFFICE"));

        if (!isAdminOrDean && !request.getUser().getEmail().equals(command.userEmail())) {
            throw UnauthorizedRequestAccessException.forAction("comment on");
        }

        Comment comment = new Comment(request, actor, command.content());
        return commentRepository.save(comment);
    }
}
