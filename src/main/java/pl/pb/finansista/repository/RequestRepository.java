package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>{ }
