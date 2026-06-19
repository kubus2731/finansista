package pl.pb.finansista.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "cost_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CostCategory extends BaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Lob private String description;

  public CostCategory(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
