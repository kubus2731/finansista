package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.Request;
import java.util.UUID;

interface SpringDataJpaRequestRepository extends JpaRepository<Request, UUID> {
}
