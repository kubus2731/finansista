package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.usecase.RegisterUserCommand;
import pl.pb.finansista.user.usecase.RegisterUserUseCase;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {

        RegisterUserCommand command = request.toCommand();

        User newUser = registerUserUseCase.execute(command);

        return UserResponse.of(newUser);
    }
}