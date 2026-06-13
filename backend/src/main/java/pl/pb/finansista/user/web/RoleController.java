package pl.pb.finansista.user.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.finansista.user.usecase.GetAllRolesUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final GetAllRolesUseCase getAllRolesUseCase;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(
                getAllRolesUseCase.execute().stream()
                        .map(RoleResponse::of)
                        .toList()
        );
    }
}
