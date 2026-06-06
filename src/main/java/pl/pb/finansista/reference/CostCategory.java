package pl.pb.finansista.reference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cost_category")
@Getter
@Setter
@NoArgsConstructor
public class CostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cc")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Lob
    private String description;
}