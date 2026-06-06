package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Comment;

import java.util.List;
import java.util.UUID;

interface SpringDataJpaCommentRepository extends JpaRepository<Comment, UUID> {
    
    List<Comment> findByRequestId(UUID requestId);
}

