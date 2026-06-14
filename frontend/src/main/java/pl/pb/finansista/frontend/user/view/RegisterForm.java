package pl.pb.finansista.frontend.user.view;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterForm(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String rawPassword,
        @NotNull Long departmentId
) {
}
