package pl.pb.finansista.reference.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.reference.usecase.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
@Slf4j
public class ReferenceController {

  private final GetAllDepartmentsUseCase getAllDepartmentsUseCase;
  private final CreateDepartmentUseCase createDepartmentUseCase;
  private final DeleteDepartmentUseCase deleteDepartmentUseCase;
  private final EditDepartmentUseCase editDepartmentUseCase;
  private final GetAllCostCategoriesUseCase getAllCostCategoriesUseCase;
  private final GetAllFundingSourcesUseCase getAllFundingSourcesUseCase;

  @GetMapping("/departments")
  public ResponseEntity<List<ReferenceResponse>> getDepartments() {
    return ResponseEntity.ok(
        getAllDepartmentsUseCase.execute().stream()
            .map(dep -> new ReferenceResponse(dep.getId(), dep.getName()))
            .toList());
  }

  @PostMapping("/departments")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ReferenceResponse> createDepartment(
      @Valid @RequestBody DepartmentRequest request) {
    log.info("Admin user is creating department: {}", request.name());
    return ResponseEntity.ok(ReferenceResponse.of(createDepartmentUseCase.execute(request.name())));
  }

  @DeleteMapping("/departments/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    log.info("Admin user is deleting department with ID: {}", id);
    deleteDepartmentUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/departments/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ReferenceResponse> editDepartment(
      @PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
    log.info("Admin user is editing department with ID: {}", id);
    return ResponseEntity.ok(
        ReferenceResponse.of(editDepartmentUseCase.execute(id, request.name())));
  }

  @GetMapping("/cost-categories")
  public ResponseEntity<List<ReferenceResponse>> getCostCategories() {
    return ResponseEntity.ok(
        getAllCostCategoriesUseCase.execute().stream()
            .map(cat -> new ReferenceResponse(cat.getId(), cat.getName()))
            .toList());
  }

  @GetMapping("/funding-sources")
  public ResponseEntity<List<ReferenceResponse>> getFundingSources() {
    return ResponseEntity.ok(
        getAllFundingSourcesUseCase.execute().stream()
            .map(src -> new ReferenceResponse(src.getId(), src.getName()))
            .toList());
  }
}
