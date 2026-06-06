package pl.pb.finansista.request.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaCommentRepository implements CommentRepository {

    private final SpringDataJpaCommentRepository repository;

    public JpaCommentRepository(SpringDataJpaCommentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Comment> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public List<Comment> findByRequestId(UUID requestId) {
        return repository.findByRequestId(requestId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}

