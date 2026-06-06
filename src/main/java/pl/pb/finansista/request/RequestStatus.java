package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "request_status")
@Getter
@Setter
@NoArgsConstructor
public class RequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rs")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}