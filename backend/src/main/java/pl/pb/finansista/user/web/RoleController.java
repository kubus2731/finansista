package pl.pb.finansista.user.web;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.user.usecase.CreateRoleUseCase;
import pl.pb.finansista.user.usecase.DeleteRoleUseCase;
import pl.pb.finansista.user.usecase.EditRoleUseCase;
import pl.pb.finansista.user.usecase.GetAllRolesUseCase;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

  private final GetAllRolesUseCase getAllRolesUseCase;
  private final CreateRoleUseCase createRoleUseCase;
  private final DeleteRoleUseCase deleteRoleUseCase;
  private final EditRoleUseCase editRoleUseCase;

  @GetMapping
  public ResponseEntity<List<RoleResponse>> getRoles() {
    return ResponseEntity.ok(getAllRolesUseCase.execute().stream().map(RoleResponse::of).toList());
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
    log.info("Admin user is creating role: {}", request.name());
    return ResponseEntity.ok(RoleResponse.of(createRoleUseCase.execute(request.name())));
  }

  @DeleteMapping("{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    log.info("Admin user is deleting role ID: {}", id);
    deleteRoleUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<RoleResponse> editRole(
      @PathVariable Long id, @Valid @RequestBody RoleRequest request) {
    log.info("Admin user is editing role ID: {}", id);
    return ResponseEntity.ok(RoleResponse.of(editRoleUseCase.execute(id, request.name())));
  }
}
