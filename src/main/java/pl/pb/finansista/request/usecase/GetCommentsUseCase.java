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

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCommentsUseCase {

    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<Comment> execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(() -> RequestNotFoundException.withExternalId(query.externalId()));

        if (!query.isAdminOrDean() && !request.getUser().getEmail().equals(query.userEmail())) {
            throw UnauthorizedRequestAccessException.forAction("view comments for");
        }

        return commentRepository.findByRequestId(request.getId());
    }
}
