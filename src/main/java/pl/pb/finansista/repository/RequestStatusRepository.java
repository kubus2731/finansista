package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.RequestStatus;

@Repository
public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long>{
}
