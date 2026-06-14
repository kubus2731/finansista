package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.exception.CostCategoryNotFoundException;
import pl.pb.finansista.reference.exception.DepartmentNotFoundException;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.exception.FundingSourceNotFoundException;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.request.ProjectDetails;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.SupervisorInfo;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

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
                ? requestTemplateRepository.findByExternalId(command.templateId()).orElseThrow(RequestTemplateNotFoundException::new)
                : null;

        RequestStatus status = requestStatusRepository.findByName(RequestStatusName.DRAFT.name())
                .orElseThrow(() -> InvalidRequestStateException.withStatusName(RequestStatusName.DRAFT.name()));

        Request request = new Request(
                command.title(),
                command.description(),
                command.amount(),
                user,
                status,
                template,
                department,
                costCategory
        );

        request.fillDetails(
                command.projectDetails() != null ? command.projectDetails().toDomain() : ProjectDetails.empty(),
                command.supervisor() != null ? command.supervisor().toDomain() : SupervisorInfo.empty());

        command.tasks().forEach(t ->
                request.addTask(t.taskNo(), t.name(), t.dateFrom(), t.dateTo(), t.plannedCost(), t.actions()));
        command.costItems().forEach(c ->
                request.addCostItem(c.taskNo(), c.itemName(), c.quantity(), c.unitCost(), c.notes()));
        command.fundings().forEach(f -> {
            FundingSource source = fundingSourceRepository.findById(f.fundingSourceId())
                    .orElseThrow(FundingSourceNotFoundException::new);
            request.addFunding(source, f.amountRequested());
        });

        return requestRepository.save(request);
    }
}
