package pl.pb.finansista.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.RoleRepository;
import pl.pb.finansista.user.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final DepartmentRepository departmentRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    log.info("Seeding users...");

    // Role i działy ze słownika V2
    Role adminRole = role(1L);
    Role studentRole = role(2L);
    Role wrssRole = role(3L);
    Role commissionRole = role(4L);
    Role deanOfficeRole = role(5L);

    Department itDept = department(1L); // Wydział Informatyki PB
    Department itDeanOffice = department(2L); // Dziekanat Wydziału Informatyki
    Department mechDept = department(3L); // Wydział Mechaniczny PB
    Department samorzadDept = department(13L); // Samorząd Studentów PB

    // Konta pokrywające całą ścieżkę wniosku z Zarządzenia 13
    seedUser(
        "Jakub",
        "Matusiewicz",
        "j.matusiewicz@student.pb.edu.pl",
        "500111222",
        "admin123",
        adminRole,
        mechDept);
    seedUser(
        "Jakub",
        "Borkowski",
        "j.borkowski@student.pb.edu.pl",
        "500333444",
        "admin123",
        adminRole,
        itDept);
    seedUser(
        "Anna",
        "Zgodna",
        "a.zgodna@pb.edu.pl",
        "857460001",
        "komisja123",
        commissionRole,
        mechDept);
    seedUser(
        "Jan",
        "Wnioskodawca",
        "j.wnioskodawca@student.pb.edu.pl",
        "500999888",
        "student123",
        studentRole,
        itDept);
    seedUser(
        "Kamil",
        "Samorzadowy",
        "k.samorzad@pb.edu.pl",
        "500777111",
        "wrss123",
        wrssRole,
        samorzadDept);
    seedUser(
        "Ewa",
        "Dziekanska",
        "e.dziekan@pb.edu.pl",
        "500666222",
        "dziekanat123",
        deanOfficeRole,
        itDeanOffice);

    log.info("Seeding completed.");
  }

  /** Tworzy konto tylko, jeśli e-mail jeszcze nie istnieje (bezpieczne przy każdym starcie). */
  private void seedUser(
      String name,
      String surname,
      String email,
      String phone,
      String rawPassword,
      Role role,
      Department department) {
    if (userRepository.existsByEmail(email)) {
      return;
    }
    userRepository.save(
        new User(
            name, surname, email, phone, passwordEncoder.encode(rawPassword), role, department));
    log.info("Added account: {} ({})", email, role.getName());
  }

  private Role role(Long id) {
    return roleRepository
        .findById(id)
        .orElseThrow(() -> new IllegalStateException("Role " + id + " not found"));
  }

  private Department department(Long id) {
    return departmentRepository
        .findById(id)
        .orElseThrow(() -> new IllegalStateException("Department " + id + " not found"));
  }
}
