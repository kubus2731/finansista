package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.repository.RequestRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRequestsUseCase {

    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<Request> execute(String userEmail, String status, UUID departmentId) {
        Specification<Request> spec = (root, query, cb) -> cb.conjunction();

        if (userEmail != null && !userEmail.isBlank()) {
            spec = spec.and(RequestSpecifications.hasUserEmail(userEmail));
        }
        
        if (status != null && !status.isBlank()) {
            spec = spec.and(RequestSpecifications.hasStatus(status));
        }

        if (departmentId != null) {
            spec = spec.and(RequestSpecifications.hasDepartment(departmentId));
        }

        return requestRepository.findAll(spec);
    }
}
