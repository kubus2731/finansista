package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.repository.CostCategoryRepository;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.reference.repository.FundingSourceRepository;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getUser().getEmail().equals(command.userEmail())) {
            throw new AccessDeniedException("You do not have permission to edit this request");
        }

        String currentStatus = request.getStatus().getName();
        if (!currentStatus.equals("DRAFT") && !currentStatus.equals("CORRECTION_REQUIRED")) {
            throw new IllegalStateException("Only requests in DRAFT or CORRECTION_REQUIRED status can be edited");
        }

        Department department = departmentRepository.findById(command.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        CostCategory costCategory = costCategoryRepository.findById(command.costCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Cost Category not found"));

        FundingSource fundingSource = null;
        if (command.fundingSourceId() != null) {
            fundingSource = fundingSourceRepository.findById(command.fundingSourceId())
                    .orElseThrow(() -> new IllegalArgumentException("Funding Source not found"));
        }

        RequestTemplate template = null;
        if (command.templateId() != null) {
            template = requestTemplateRepository.findById(command.templateId())
                    .orElseThrow(() -> new IllegalArgumentException("Request Template not found"));
        }

        request.update(
                command.title(),
                command.description(),
                command.amount(),
                template,
                department,
                costCategory,
                fundingSource
        );

        return requestRepository.save(request);
    }
}
