package pl.pb.finansista.user.usecase;

import org.springframework.util.Assert;

import java.util.UUID;

public record RegisterUserCommand(
        String name,
        String surname,
        String email,
        String phoneNumber,
        String rawPassword,
        UUID roleId,
        UUID departmentId) {

    public RegisterUserCommand {
        Assert.hasText(name, "Name cannot be blank");
        Assert.hasText(surname, "Surname cannot be blank");
        Assert.hasText(email, "Email cannot be blank");
        Assert.hasText(phoneNumber, "Phone number cannot be blank");
        Assert.hasText(rawPassword, "Password cannot be blank");
        Assert.notNull(roleId, "Role ID must be provided");
        Assert.notNull(departmentId, "Department ID must be provided");
    }

}
