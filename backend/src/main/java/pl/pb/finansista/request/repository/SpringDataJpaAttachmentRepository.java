package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Attachment;

interface SpringDataJpaAttachmentRepository extends JpaRepository<Attachment, Long> {

  Optional<Attachment> findByExternalId(UUID externalId);

  List<Attachment> findByRequestId(Long requestId);
}
