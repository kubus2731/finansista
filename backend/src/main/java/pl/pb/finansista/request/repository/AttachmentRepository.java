package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Attachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository {

    Optional<Attachment> findById(Long id);

    Optional<Attachment> findByExternalId(UUID externalId);

    List<Attachment> findByRequestId(Long requestId);

    Attachment save(Attachment attachment);

    void delete(Attachment attachment);
}
