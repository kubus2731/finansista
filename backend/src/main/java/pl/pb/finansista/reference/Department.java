package pl.pb.finansista.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  /** Dział nadrzędny — dla dziekanatu jest to jego wydział. NULL dla działów bez nadrzędnego. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_department_id")
  private Department parent;

  public Department(String name) {
    this.name = name;
  }

  public void rename(String name) {
    this.name = name;
  }
}
