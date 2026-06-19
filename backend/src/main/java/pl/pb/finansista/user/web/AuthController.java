package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.common.security.JwtService;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.usecase.LoginUserUseCase;
import pl.pb.finansista.user.usecase.RegisterUserUseCase;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUserUseCase loginUserUseCase;
  private final JwtService jwtService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {

    User newUser = registerUserUseCase.execute(request.toCommand());
    String token = jwtService.generateToken(newUser);
    ResponseCookie jwtCookie = jwtService.generateJwtCookie(token);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(UserResponse.of(newUser));
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginUserRequest request) {

    User user = loginUserUseCase.execute(request.toCommand());
    String token = jwtService.generateToken(user);
    ResponseCookie jwtCookie = jwtService.generateJwtCookie(token);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(UserResponse.of(user));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
  }
}
