package pl.pb.finansista.reference.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.finansista.reference.usecase.GetAllCostCategoriesUseCase;
import pl.pb.finansista.reference.usecase.GetAllDepartmentsUseCase;
import pl.pb.finansista.reference.usecase.GetAllFundingSourcesUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final GetAllDepartmentsUseCase getAllDepartmentsUseCase;
    private final GetAllCostCategoriesUseCase getAllCostCategoriesUseCase;
    private final GetAllFundingSourcesUseCase getAllFundingSourcesUseCase;

    @GetMapping("/departments")
    public ResponseEntity<List<ReferenceResponse>> getDepartments() {
        return ResponseEntity.ok(
                getAllDepartmentsUseCase.execute().stream()
                        .map(dep -> new ReferenceResponse(dep.getId(), dep.getName()))
                        .toList()
        );
    }

    @GetMapping("/cost-categories")
    public ResponseEntity<List<ReferenceResponse>> getCostCategories() {
        return ResponseEntity.ok(
                getAllCostCategoriesUseCase.execute().stream()
                        .map(cat -> new ReferenceResponse(cat.getId(), cat.getName()))
                        .toList()
        );
    }

    @GetMapping("/funding-sources")
    public ResponseEntity<List<ReferenceResponse>> getFundingSources() {
        return ResponseEntity.ok(
                getAllFundingSourcesUseCase.execute().stream()
                        .map(src -> new ReferenceResponse(src.getId(), src.getName()))
                        .toList()
        );
    }
}
