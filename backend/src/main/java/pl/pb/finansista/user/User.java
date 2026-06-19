package pl.pb.finansista.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.ExposableModificationAuditedEntity;
import pl.pb.finansista.reference.Department;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends ExposableModificationAuditedEntity {
  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 50)
  private String surname;

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = false, unique = true, length = 15)
  private String phoneNumber;

  @Column(nullable = false)
  private String password;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "department_id", nullable = false)
  private Department department;

  /** Soft delete: false = konto dezaktywowane (nie loguje się, ukryte na listach). */
  @Column(nullable = false)
  private boolean active = true;

  public User(
      String name,
      String surname,
      String email,
      String phoneNumber,
      String password,
      Role role,
      Department department) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.password = password;
    this.role = role;
    this.department = department;
    this.active = true;
  }

  public void changeRole(Role newRole) {
    this.role = newRole;
  }

  public void changeDepartment(Department newDepartment) {
    this.department = newDepartment;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
