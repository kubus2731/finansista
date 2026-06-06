package pl.pb.finansista.activity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_al")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_req", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_u", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_old_status")
    private RequestStatus oldStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_new_status", nullable = false)
    private RequestStatus newStatus;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;
}