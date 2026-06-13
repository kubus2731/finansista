package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Comment;

import java.util.List;

interface SpringDataJpaCommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRequestId(Long requestId);
}

