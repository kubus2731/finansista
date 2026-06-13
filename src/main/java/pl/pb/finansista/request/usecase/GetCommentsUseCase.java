package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.CommentRepository;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCommentsUseCase {

    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessValidator accessValidator;

    @Transactional(readOnly = true)
    public List<Comment> execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User user = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        accessValidator.validateUserCanAccessRequest(request, user, query.userAuthorities());

        return commentRepository.findByRequestId(request.getId());
    }
}
