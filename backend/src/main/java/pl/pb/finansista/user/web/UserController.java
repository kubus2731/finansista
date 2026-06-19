package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import pl.pb.finansista.user.usecase.ChangeUserDepartmentUseCase;
import pl.pb.finansista.user.usecase.ChangeUserRoleUseCase;
import pl.pb.finansista.user.usecase.GetMyProfileUseCase;
import pl.pb.finansista.user.usecase.GetUserByIdUseCase;
import pl.pb.finansista.user.usecase.GetUsersUseCase;
import pl.pb.finansista.user.usecase.SetUserActiveUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final GetUsersUseCase getUsersUseCase;
  private final GetMyProfileUseCase getMyProfileUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;
  private final ChangeUserRoleUseCase changeUserRoleUseCase;
  private final ChangeUserDepartmentUseCase changeUserDepartmentUseCase;
  private final SetUserActiveUseCase setUserActiveUseCase;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
    log.info("Admin user {} is fetching all users", authentication.getName());
    return ResponseEntity.ok(getUsersUseCase.execute().stream().map(UserResponse::of).toList());
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(UserResponse.of(getMyProfileUseCase.execute(userId)));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    return ResponseEntity.ok(UserResponse.of(getUserByIdUseCase.execute(id)));
  }

  @PatchMapping("/{id}/role")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> changeUserRole(
      @PathVariable UUID id,
      @RequestBody @Valid ChangeUserRoleRequest request,
      Authentication authentication) {
    log.info("Admin user {} is changing role for user ID: {}", authentication.getName(), id);
    changeUserRoleUseCase.execute(request.toCommand(id));
    log.info("Successfully updated role for user ID: {}", id);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/department")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> changeUserDepartment(
      @PathVariable UUID id,
      @RequestBody @Valid ChangeUserDepartmentRequest request,
      Authentication authentication) {

    log.info("Admin user {} is changing department for user ID: {}", authentication.getName(), id);
    changeUserDepartmentUseCase.execute(request.toCommand(id));
    log.info("Successfully updated department for user ID: {}", id);

    return ResponseEntity.noContent().build();
  }

  /** Soft delete: aktywacja / dezaktywacja konta użytkownika (zamiast fizycznego DELETE). */
  @PatchMapping("/{id}/active")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> setUserActive(
      @PathVariable UUID id,
      @RequestBody @Valid ChangeUserActiveRequest request,
      Authentication authentication) {

    log.info(
        "Admin user {} is setting active={} for user ID: {}",
        authentication.getName(),
        request.active(),
        id);
    setUserActiveUseCase.execute(request.toCommand(id, authentication.getName()));
    log.info("Successfully updated active flag for user ID: {}", id);

    return ResponseEntity.noContent().build();
  }
}
