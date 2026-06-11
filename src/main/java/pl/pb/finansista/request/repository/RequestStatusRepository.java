package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.RequestStatus;
import java.util.Optional;
import java.util.UUID;

public interface RequestStatusRepository {

    Optional<RequestStatus> findById(Long id);
    Optional<RequestStatus> findByName(String name);

    RequestStatus save(RequestStatus requestStatus);
}
