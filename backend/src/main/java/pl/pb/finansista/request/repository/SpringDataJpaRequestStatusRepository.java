package pl.pb.finansista.request.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.RequestStatus;

public interface SpringDataJpaRequestStatusRepository extends JpaRepository<RequestStatus, Long> {

  Optional<RequestStatus> findByName(String name);
}
