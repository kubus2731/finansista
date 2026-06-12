package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.user.usecase.ChangeUserRoleUseCase;
import pl.pb.finansista.user.usecase.GetUsersUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUsersUseCase getUsersUseCase;
    private final ChangeUserRoleUseCase changeUserRoleUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
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
            @RequestBody @Valid ChangeUserRoleRequest request) {

        changeUserRoleUseCase.execute(request.toCommand(id));

        return ResponseEntity.noContent().build();
    }
}
