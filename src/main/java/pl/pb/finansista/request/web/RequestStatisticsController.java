package pl.pb.finansista.request.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.finansista.request.usecase.GetDepartmentSummariesUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests/statistics")
@RequiredArgsConstructor
public class RequestStatisticsController {

    private final GetDepartmentSummariesUseCase getDepartmentSummariesUseCase;

    @GetMapping("/departments")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<DepartmentRequestsSummaryResponse>> getDepartmentSummaries() {
        List<DepartmentRequestsSummaryResponse> responses = getDepartmentSummariesUseCase.execute()
                .stream()
                .map(DepartmentRequestsSummaryResponse::of)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
