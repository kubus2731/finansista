package pl.pb.finansista.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
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
        if (userRepository.existsByEmail("j.matusiewicz@student.pb.edu.pl")) {
            log.info("Users already seeded. Skipping...");
            return;
        }

        log.info("Seeding initial mock users...");

        // Fetch dictionary entities initialized by V2 Flyway script
        Role adminRole = roleRepository.findById(1L).orElseThrow(() -> new IllegalStateException("Role 1 not found"));
        Role studentRole = roleRepository.findById(2L).orElseThrow(() -> new IllegalStateException("Role 2 not found"));
        Role commissionRole = roleRepository.findById(4L).orElseThrow(() -> new IllegalStateException("Role 4 not found"));

        Department itDept = departmentRepository.findById(1L).orElseThrow(() -> new IllegalStateException("Department 1 not found"));
        Department mechDept = departmentRepository.findById(3L).orElseThrow(() -> new IllegalStateException("Department 3 not found"));

        String adminPass = passwordEncoder.encode("admin123");
        String commissionPass = passwordEncoder.encode("komisja123");
        String studentPass = passwordEncoder.encode("student123");

        User admin1 = new User("Jakub", "Matusiewicz", "j.matusiewicz@student.pb.edu.pl", "500111222", adminPass, adminRole, mechDept);
        User admin2 = new User("Jakub", "Borkowski", "j.borkowski@student.pb.edu.pl", "500333444", adminPass, adminRole, itDept);
        User commission1 = new User("Anna", "Zgodna", "a.zgodna@pb.edu.pl", "857460001", commissionPass, commissionRole, mechDept);
        User student1 = new User("Jan", "Wnioskodawca", "j.wnioskodawca@student.pb.edu.pl", "500999888", studentPass, studentRole, itDept);

        userRepository.save(admin1);
        userRepository.save(admin2);
        userRepository.save(commission1);
        userRepository.save(student1);

        log.info("Mock users seeded successfully.");
    }
}
