package pl.pb.finansista.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    public Department(String name) {
        this.name = name;
    }

    public void rename(String name) {
        this.name = name;
    }
}