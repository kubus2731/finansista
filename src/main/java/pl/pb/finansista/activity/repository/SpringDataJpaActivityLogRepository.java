package pl.pb.finansista.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.activity.ActivityLog;
import java.util.UUID;

interface SpringDataJpaActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
}
