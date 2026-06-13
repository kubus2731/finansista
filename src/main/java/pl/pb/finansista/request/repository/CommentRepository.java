package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Optional<Comment> findById(Long id);

    Comment save(Comment comment);

    List<Comment> findByRequestId(Long requestId);

    void deleteById(Long id);
}
