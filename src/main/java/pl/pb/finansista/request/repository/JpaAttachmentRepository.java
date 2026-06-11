package pl.pb.finansista.request.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Attachment;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaAttachmentRepository implements AttachmentRepository {

    private final SpringDataJpaAttachmentRepository repository;

    public JpaAttachmentRepository(SpringDataJpaAttachmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Attachment> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Attachment save(Attachment attachment) {
        return repository.save(attachment);
    }
}
