package pl.pb.finansista.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pb.finansista.repository.*;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.user.User;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public Comment addCommentToRequest(Comment newComment, Long requestId, Long userId){

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Błąd: Nie znaleziono wniosku o ID: " + requestId));
        newComment.setRequest(request);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd: nie znaleziono użytkownika o ID: " + userId));
        newComment.setUser(author);

        return  commentRepository.save(newComment);
    }
}
