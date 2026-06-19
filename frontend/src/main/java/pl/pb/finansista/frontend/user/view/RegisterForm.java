package pl.pb.finansista.frontend.user.view;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record RegisterForm(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{9}$") String phoneNumber,
        @NotBlank @Size(min = 8) String rawPassword,
        @NotBlank String passwordConfirm,
        @NotNull Long departmentId
) {
}
