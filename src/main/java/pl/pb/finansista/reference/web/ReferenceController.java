package pl.pb.finansista.reference.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.user.repository.RoleRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final CostCategoryRepository costCategoryRepository;
    private final FundingSourceRepository fundingSourceRepository;

    @GetMapping("/roles")
    public ResponseEntity<List<ReferenceResponse>> getRoles() {
        return ResponseEntity.ok(
                roleRepository.findAll().stream()
                        .map(role -> new ReferenceResponse(role.getId(), role.getName()))
                        .toList()
        );
    }

    @GetMapping("/departments")
    public ResponseEntity<List<ReferenceResponse>> getDepartments() {
        return ResponseEntity.ok(
                departmentRepository.findAll().stream()
                        .map(dep -> new ReferenceResponse(dep.getId(), dep.getName()))
                        .toList()
        );
    }

    @GetMapping("/cost-categories")
    public ResponseEntity<List<ReferenceResponse>> getCostCategories() {
        return ResponseEntity.ok(
                costCategoryRepository.findAll().stream()
                        .map(cat -> new ReferenceResponse(cat.getId(), cat.getName()))
                        .toList()
        );
    }

    @GetMapping("/funding-sources")
    public ResponseEntity<List<ReferenceResponse>> getFundingSources() {
        return ResponseEntity.ok(
                fundingSourceRepository.findAll().stream()
                        .map(src -> new ReferenceResponse(src.getId(), src.getName()))
                        .toList()
        );
    }
}
