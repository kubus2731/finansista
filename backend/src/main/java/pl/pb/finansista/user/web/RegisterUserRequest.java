package pl.pb.finansista.user.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.pb.finansista.user.usecase.RegisterUserCommand;

public record RegisterUserRequest(
        @NotBlank
        String name,

        @NotBlank
        String surname,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "^\\d{9}$")
        String phoneNumber,

        @NotBlank
        @Size(min = 8)
        String rawPassword,

        @NotNull
        Long roleId,

        @NotNull
        Long departmentId
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
                name,
                surname,
                email,
                phoneNumber,
                rawPassword,
                roleId,
                departmentId
        );
    }
}
