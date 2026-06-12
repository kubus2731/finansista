package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.repository.RequestRepository;

import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GetRequestsUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Request> execute(GetRequestsQuery query) {
        Specification<Request> spec = (root, cbQuery, cb) -> cb.conjunction();

        User currentUser = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        boolean isAdmin = query.userAuthorities().contains("ROLE_ADMIN");
        boolean isDeanOffice = query.userAuthorities().contains("ROLE_DEAN_OFFICE");
        boolean isFinanceOffice = query.userAuthorities().contains("ROLE_FINANCE_OFFICE");
        boolean isLegalCommission = query.userAuthorities().contains("ROLE_LEGAL_COMMISSION");
        boolean isWrss = query.userAuthorities().contains("ROLE_WRSS");

        if (!isAdmin) {
            Specification<Request> roleSpec = (root, cbQuery, cb) -> cb.disjunction();

            roleSpec = roleSpec.or(RequestSpecifications.hasUserEmail(query.userEmail()));

            if (isDeanOffice) {
                Specification<Request> deanSpec = RequestSpecifications.hasDepartment(currentUser.getDepartment().getId())
                        .and(RequestSpecifications.hasStatusIn(List.of("SUBMITTED", "FORMAL_EVALUATION", "UNDER_REVIEW", "ACCEPTED", "REJECTED", "CORRECTION_REQUIRED")));
                roleSpec = roleSpec.or(deanSpec);
            }

            if (isWrss || isLegalCommission) {
                Specification<Request> wrssSpec = RequestSpecifications.hasStatusIn(List.of("FORMAL_EVALUATION", "UNDER_REVIEW", "ACCEPTED", "REJECTED", "CORRECTION_REQUIRED"));
                roleSpec = roleSpec.or(wrssSpec);
            }

            if (isFinanceOffice) {
                Specification<Request> financeSpec = RequestSpecifications.hasStatusIn(List.of("UNDER_REVIEW", "ACCEPTED", "REJECTED", "CORRECTION_REQUIRED"));
                roleSpec = roleSpec.or(financeSpec);
            }

            spec = spec.and(roleSpec);
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
