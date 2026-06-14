package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRequestsUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecificationFactory;

    @Transactional(readOnly = true)
    public List<Request> execute(GetRequestsQuery query) {
        return requestRepository.findAll(buildSpec(query));
    }

    @Transactional(readOnly = true)
    public Page<Request> execute(GetRequestsQuery query, Pageable pageable) {
        return requestRepository.findAll(buildSpec(query), pageable);
    }

    private Specification<Request> buildSpec(GetRequestsQuery query) {
        User currentUser = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        List<Specification<Request>> specs = new ArrayList<>();
        specs.add(accessSpecificationFactory.createForUser(currentUser, query.userAuthorities()));

        if (query.status() != null && !query.status().isBlank()) {
            specs.add(RequestSpecifications.hasStatus(query.status()));
        }

        if (query.departmentId() != null) {
            specs.add(RequestSpecifications.hasDepartment(query.departmentId()));
        }

        if (query.search() != null && !query.search().isBlank()) {
            specs.add(RequestSpecifications.containsText(query.search()));
        }

        return Specification.allOf(specs);
    }
}
