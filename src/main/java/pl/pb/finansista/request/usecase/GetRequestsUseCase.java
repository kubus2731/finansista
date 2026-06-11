package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.repository.RequestRepository;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GetRequestsUseCase {

    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<Request> execute(GetRequestsQuery query) {
        Specification<Request> spec = (root, cbQuery, cb) -> cb.conjunction();

        boolean isAdmin = query.userAuthorities().contains("ROLE_ADMIN");
        boolean isStudent = query.userAuthorities().contains("ROLE_STUDENT");
        boolean isDeanOffice = query.userAuthorities().contains("ROLE_DEAN_OFFICE");
        boolean isFinanceOffice = query.userAuthorities().contains("ROLE_FINANCE_OFFICE");
        boolean isLegalCommission = query.userAuthorities().contains("ROLE_LEGAL_COMMISSION");

        if (isStudent && !isAdmin) {
            // Students can only see their own requests
            spec = spec.and(RequestSpecifications.hasUserEmail(query.userEmail()));
        } else if (!isAdmin) {
            // Role-based visibility for administrative staff
            List<String> allowedStatuses = new ArrayList<>();
            
            if (isDeanOffice) {
                allowedStatuses.addAll(List.of("SUBMITTED", "CORRECTION_REQUIRED", "ACCEPTED", "REJECTED"));
            }
            if (isFinanceOffice) {
                allowedStatuses.addAll(List.of("UNDER_REVIEW", "ACCEPTED", "REJECTED"));
            }
            if (isLegalCommission) {
                allowedStatuses.add("FORMAL_EVALUATION");
            }
            
            if (!allowedStatuses.isEmpty()) {
                spec = spec.and(RequestSpecifications.hasStatusIn(allowedStatuses));
            }
        }

        if (query.status() != null && !query.status().isBlank()) {
            spec = spec.and(RequestSpecifications.hasStatus(query.status()));
        }

        if (query.departmentId() != null) {
            spec = spec.and(RequestSpecifications.hasDepartment(query.departmentId()));
        }

        return requestRepository.findAll(spec);
    }
}
