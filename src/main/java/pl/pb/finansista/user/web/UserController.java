package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.user.usecase.ChangeUserDepartmentUseCase;
import pl.pb.finansista.user.usecase.ChangeUserRoleUseCase;
import pl.pb.finansista.user.usecase.GetUsersUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final GetUsersUseCase getUsersUseCase;
    private final ChangeUserRoleUseCase changeUserRoleUseCase;
    private final ChangeUserDepartmentUseCase changeUserDepartmentUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
        log.info("Admin user {} is fetching all users", authentication.getName());
        return ResponseEntity.ok(
                getUsersUseCase.execute().stream()
                        .map(UserResponse::of)
                        .toList()
        );
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
}
