package pl.pb.finansista.user.usecase;

import org.springframework.util.Assert;

public record LoginUserCommand(String email, String password) {

    public LoginUserCommand {
        Assert.hasText(email, "Email cannot be blank");
        Assert.hasText(password, "Password cannot be blank");
    }
}