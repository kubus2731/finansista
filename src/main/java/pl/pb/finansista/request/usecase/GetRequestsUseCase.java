package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRequestsUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecificationFactory;

    @Transactional(readOnly = true)
    public List<Request> execute(GetRequestsQuery query) {
        User currentUser = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = accessSpecificationFactory.createForUser(currentUser, query.userAuthorities());

        if (query.status() != null && !query.status().isBlank()) {
            spec = spec.and(RequestSpecifications.hasStatus(query.status()));
        }

        if (query.departmentId() != null) {
            spec = spec.and(RequestSpecifications.hasDepartment(query.departmentId()));
        }

        if (query.search() != null && !query.search().isBlank()) {
            spec = spec.and(RequestSpecifications.containsText(query.search()));
        }

        return requestRepository.findAll(spec);
    }
}
