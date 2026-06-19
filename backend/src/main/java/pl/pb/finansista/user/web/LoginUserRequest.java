package pl.pb.finansista.user.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pl.pb.finansista.user.usecase.LoginUserCommand;

public record LoginUserRequest(@NotBlank @Email String email, @NotBlank String password) {

  public LoginUserCommand toCommand() {
    return new LoginUserCommand(email, password);
  }
}
