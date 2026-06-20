package pl.pb.finansista.user;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  public Role(String name) {
    this.name = name;
  }

  public void rename(String name) {
    this.name = name;
  }

  public boolean isBuiltIn() {
    return Arrays.stream(RoleName.values()).anyMatch(r -> r.name().equals(name));
  }

  public List<GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(name));
  }
}
