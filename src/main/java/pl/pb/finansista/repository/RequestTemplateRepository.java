package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.RequestTemplate;

@Repository
public interface RequestTemplateRepository extends JpaRepository<RequestTemplate, Long>{
}
