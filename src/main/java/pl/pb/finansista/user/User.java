package pl.pb.finansista.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pb.finansista.reference.Department;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_u")
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max=50, message = "Imię musi mieć od 2 do 50 znaków")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Size(min = 2, max=50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    @NotBlank(message = "E-mail jest wymagany!")
    @Email(message = "Podaj poprawny format e-mail")
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank(message = "Numer telefonu jest wymagany")
    @Pattern(regexp = "^\\\\+?[0-9]{9,15}$", message = "Nieprawidłowy format numeru telefonu")
    @Column(name = "phone_number",nullable = false, unique = true, length = 50)
    private String phone;

    @NotBlank(message = "Hasło jest wymagane!")
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_r", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_d", nullable = false)
    private Department department;
}