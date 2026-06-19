package pl.pb.finansista.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Comment;

interface SpringDataJpaCommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByRequestId(Long requestId);
}
