package pl.pb.finansista.request;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_templates")
@Getter
@Setter
@NoArgsConstructor
public class RequestTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rt")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}