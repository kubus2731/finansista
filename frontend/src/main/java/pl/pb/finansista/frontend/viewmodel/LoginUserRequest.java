package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
    
}
