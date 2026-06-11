package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Attachment;

interface SpringDataJpaAttachmentRepository extends JpaRepository<Attachment, Long> {
}
