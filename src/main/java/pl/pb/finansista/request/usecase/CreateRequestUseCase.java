package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.CostCategoryNotFoundException;
import pl.pb.finansista.reference.DepartmentNotFoundException;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.FundingSourceNotFoundException;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.request.ProjectDetails;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.SupervisorInfo;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.UserNotFoundException;
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

        // Załącznik 1, sekcje I-II
        request.fillDetails(
                new ProjectDetails(
                        command.realizerType(), command.projectKind(), command.projectKindOther(),
                        command.projectScope(), command.projectScopeOther(),
                        command.projectNature(), command.projectNatureOther(),
                        command.plannedDateFrom(), command.plannedDateTo(), command.location(),
                        command.participantsInvolved(), command.participantsBenefiting()),
                new SupervisorInfo(
                        command.supervisorName(), command.supervisorEmail(),
                        command.supervisorPhone(), command.supervisorDepartment()));

        // sekcja IV i VI - tabele-dzieci
        if (command.tasks() != null) {
            command.tasks().forEach(t ->
                    request.addTask(t.taskNo(), t.name(), t.dateFrom(), t.dateTo(), t.plannedCost(), t.actions()));
        }
        if (command.costItems() != null) {
            command.costItems().forEach(c ->
                    request.addCostItem(c.taskNo(), c.itemName(), c.quantity(), c.unitCost(), c.notes()));
        }
        if (command.fundings() != null) {
            command.fundings().forEach(f ->
                    request.addFunding(f.sourceName(), f.amountRequested(), f.amountGranted()));
        }

        return requestRepository.save(request);
    }
}
