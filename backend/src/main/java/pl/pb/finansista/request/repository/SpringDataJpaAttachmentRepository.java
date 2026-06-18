package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Attachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataJpaAttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByExternalId(UUID externalId);

    List<Attachment> findByRequestId(Long requestId);
}
