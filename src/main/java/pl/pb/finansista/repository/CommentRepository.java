package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
}
