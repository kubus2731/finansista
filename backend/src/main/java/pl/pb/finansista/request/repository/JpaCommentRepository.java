package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Comment;

@Repository
class JpaCommentRepository implements CommentRepository {

  private final SpringDataJpaCommentRepository repository;

  public JpaCommentRepository(SpringDataJpaCommentRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Comment> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Comment save(Comment comment) {
    return repository.save(comment);
  }

  @Override
  public List<Comment> findByRequestId(Long requestId) {
    return repository.findByRequestId(requestId);
  }

  @Override
  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
