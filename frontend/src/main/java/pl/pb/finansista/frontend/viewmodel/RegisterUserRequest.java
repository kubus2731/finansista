package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record RegisterUserRequest(
        @NotBlank
        String name,

        @NotBlank
        String surname,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String phoneNumber,

        @NotBlank
        String rawPassword,

        @NotNull
        Long roleId,

        @NotNull
        Long departmentId
) {
    
}
