package pl.pb.finansista.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "request_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestStatus extends BaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  public RequestStatus(String name) {
    this.name = name;
  }
}
