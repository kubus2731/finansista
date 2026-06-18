package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.*;
import pl.pb.finansista.reference.exception.CostCategoryNotFoundException;
import pl.pb.finansista.reference.exception.DepartmentNotFoundException;
import pl.pb.finansista.reference.exception.FundingSourceNotFoundException;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;
import pl.pb.finansista.user.RoleName;

@Service
@RequiredArgsConstructor
public class EditRequestUseCase {

    private final RequestRepository requestRepository;
    private final DepartmentRepository departmentRepository;
    private final CostCategoryRepository costCategoryRepository;
    private final FundingSourceRepository fundingSourceRepository;
    private final RequestTemplateRepository requestTemplateRepository;

    @Transactional
    public Request execute(EditRequestCommand command) {
        Request request = requestRepository.findByExternalId(command.externalId())
                .orElseThrow(RequestNotFoundException::new);

        request.assertVersion(command.version());

        boolean isAdmin = command.userAuthorities().contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getExternalId().equals(command.userExternalId());
        boolean statusAllowsEdit = request.getStatus().getName().equals(RequestStatusName.DRAFT.name())
                || request.getStatus().getName().equals(RequestStatusName.CORRECTION_REQUIRED.name());

        if (!isAdmin && !isAuthor) {
            throw UnauthorizedRequestAccessException.forAction("edit");
        }

        if (!statusAllowsEdit) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        Department department = departmentRepository.findById(command.departmentId())
                .orElseThrow(DepartmentNotFoundException::new);

        CostCategory costCategory = costCategoryRepository.findById(command.costCategoryId())
                .orElseThrow(CostCategoryNotFoundException::new);

        RequestTemplate template = command.templateId() != null
                ? requestTemplateRepository.findByExternalId(command.templateId()).orElseThrow(RequestTemplateNotFoundException::new)
                : null;

        request.update(
                command.title(),
                command.description(),
                command.amount(),
                template,
                department,
                costCategory
        );

        request.fillDetails(
                ProjectDetailsData.toDomainOrEmpty(command.projectDetails()),
                SupervisorData.toDomainOrEmpty(command.supervisor()));

        request.clearTasks();
        command.tasks().forEach(t ->
                request.addTask(t.taskNo(), t.name(), t.dateFrom(), t.dateTo(), t.plannedCost(), t.actions()));
        request.clearCostItems();
        command.costItems().forEach(c ->
                request.addCostItem(c.taskNo(), c.itemName(), c.quantity(), c.unitCost(), c.notes()));
        request.clearFunding();
        command.fundings().forEach(f -> {
            FundingSource fundingSource = fundingSourceRepository.findById(f.fundingSourceId())
                    .orElseThrow(FundingSourceNotFoundException::new);
            request.addFunding(fundingSource, f.amountRequested());
        });

        return requestRepository.save(request);
    }
}
