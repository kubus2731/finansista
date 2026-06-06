package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.RequestStatus;
import java.util.UUID;

interface SpringDataJpaRequestStatusRepository extends JpaRepository<RequestStatus, UUID> {
}
