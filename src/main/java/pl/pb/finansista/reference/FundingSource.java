package pl.pb.finansista.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "funding_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingSource extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public FundingSource(String name) {
        this.name = name;
    }
}
