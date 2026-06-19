package pl.pb.finansista.user.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pb.finansista.user.usecase.RegisterUserCommand;

public record RegisterUserRequest(
    @NotBlank String name,
    @NotBlank String surname,
    @NotBlank @Email String email,
    @NotBlank String phoneNumber,
    @NotBlank String rawPassword,
    @NotNull Long roleId,
    @NotNull Long departmentId) {
  public RegisterUserCommand toCommand() {
    return new RegisterUserCommand(
        name, surname, email, phoneNumber, rawPassword, roleId, departmentId);
  }
}
