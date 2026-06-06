package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Request;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository {

    Optional<Request> findById(UUID id);

    Request save(Request request);
}
