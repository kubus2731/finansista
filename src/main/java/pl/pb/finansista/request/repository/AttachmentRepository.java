package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Attachment;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository {

    Optional<Attachment> findById(UUID id);

    Attachment save(Attachment attachment);
}
