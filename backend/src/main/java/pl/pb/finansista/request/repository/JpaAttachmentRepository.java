package pl.pb.finansista.request.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Attachment;

import java.util.List;
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
    public Optional<Attachment> findByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId);
    }

    @Override
    public List<Attachment> findByRequestId(Long requestId) {
        return repository.findByRequestId(requestId);
    }

    @Override
    public Attachment save(Attachment attachment) {
        return repository.save(attachment);
    }

    @Override
    public void delete(Attachment attachment) {
        repository.delete(attachment);
    }
}
