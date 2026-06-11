package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
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
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        User actor = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdminOrDean = command.userAuthorities().stream()
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_DEAN_OFFICE"));

        if (!isAdminOrDean && !request.getUser().getEmail().equals(command.userEmail())) {
            throw new AccessDeniedException("You do not have permission to comment on this request");
        }

        Comment comment = new Comment(request, actor, command.content());
        return commentRepository.save(comment);
    }
}
