package pl.pb.finansista.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {}