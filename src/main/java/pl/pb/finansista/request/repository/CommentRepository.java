package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {

    Optional<Comment> findById(UUID id);

    Comment save(Comment comment);

    List<Comment> findByRequestId(UUID requestId);

    void deleteById(UUID id);
}
