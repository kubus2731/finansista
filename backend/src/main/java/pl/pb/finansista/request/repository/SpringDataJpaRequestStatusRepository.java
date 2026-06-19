package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.RequestStatus;

import java.util.Optional;

public interface SpringDataJpaRequestStatusRepository extends JpaRepository<RequestStatus, Long> {

  Optional<RequestStatus> findByName(String name);
}
