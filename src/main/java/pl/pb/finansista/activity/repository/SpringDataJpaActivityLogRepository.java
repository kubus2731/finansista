package pl.pb.finansista.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.activity.ActivityLog;

interface SpringDataJpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
