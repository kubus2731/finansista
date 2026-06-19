package pl.pb.finansista.request;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Załącznik 1, sekcja II: opiekun naukowy (opcjonalny, dla kół naukowych). */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisorInfo {

  @Column(name = "supervisor_name")
  private String name;

  @Column(name = "supervisor_email")
  private String email;

  @Column(name = "supervisor_phone")
  private String phone;

  @Column(name = "supervisor_department")
  private String department;

  public SupervisorInfo(String name, String email, String phone, String department) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.department = department;
  }

  public static SupervisorInfo empty() {
    return new SupervisorInfo(null, null, null, null);
  }
}
