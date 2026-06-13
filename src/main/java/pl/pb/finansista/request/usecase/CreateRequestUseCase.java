package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.DepartmentNotFoundException;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.reference.CostCategoryNotFoundException;
import pl.pb.finansista.reference.FundingSourceNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;
import pl.pb.finansista.user.RoleName;

@Service
@RequiredArgsConstructor
public class CreateRequestUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CostCategoryRepository costCategoryRepository;
    private final FundingSourceRepository fundingSourceRepository;
    private final RequestTemplateRepository requestTemplateRepository;
    private final RequestStatusRepository requestStatusRepository;

    @Transactional
    public Request execute(CreateRequestCommand command) {
        var user = userRepository.findByEmail(command.userEmail())
                .orElseThrow(UserNotFoundException::new);

        String userRole = user.getRole().getName();
        if (userRole.equals(RoleName.ROLE_DEAN_OFFICE.name()) || userRole.equals(RoleName.ROLE_FINANCE_OFFICE.name())) {
            throw UnauthorizedRequestAccessException.forAction("create a request as an administrative employee");
        }

        var department = departmentRepository.findById(command.departmentId())
                .orElseThrow(DepartmentNotFoundException::new);

        var costCategory = costCategoryRepository.findById(command.costCategoryId())
                .orElseThrow(CostCategoryNotFoundException::new);

        RequestTemplate template = command.templateId() != null
                ? requestTemplateRepository.findById(command.templateId()).orElseThrow(RequestTemplateNotFoundException::new)
                : null;

        FundingSource fundingSource = command.fundingSourceId() != null
                ? fundingSourceRepository.findById(command.fundingSourceId()).orElseThrow(FundingSourceNotFoundException::new)
                : null;

        RequestStatus status = requestStatusRepository.findByName("DRAFT")
                .orElseThrow(() -> InvalidRequestStateException.withStatusName("DRAFT"));

        Request request = new Request(
                command.title(),
                command.description(),
                command.amount(),
                user,
                status,
                template,
                department,
                costCategory,
                fundingSource
        );

        return requestRepository.save(request);
    }
}
