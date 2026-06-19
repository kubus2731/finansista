package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.request.RequestStatus;

public interface RequestStatusRepository {

  Optional<RequestStatus> findById(Long id);

  Optional<RequestStatus> findByName(String name);

  List<RequestStatus> findAll();

  RequestStatus save(RequestStatus requestStatus);
}
