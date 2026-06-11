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
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.request.repository.RequestStatusRepository;
import pl.pb.finansista.request.repository.RequestTemplateRepository;
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
                .orElseThrow(() -> new UserNotFoundException(command.userEmail()));

        var department = departmentRepository.findById(command.departmentId())
                .orElseThrow(DepartmentNotFoundException::new);

        var costCategory = costCategoryRepository.findById(command.costCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Cost Category not found"));

        RequestTemplate template = null;
        if (command.templateId() != null) {
            template = requestTemplateRepository.findById(command.templateId())
                    .orElseThrow(() -> new IllegalArgumentException("Request Template not found"));
        }

        FundingSource fundingSource = null;
        if (command.fundingSourceId() != null) {
            fundingSource = fundingSourceRepository.findById(command.fundingSourceId())
                    .orElseThrow(() -> new IllegalArgumentException("Funding Source not found"));
        }

        RequestStatus status = requestStatusRepository.findByName("DRAFT")
                .orElseThrow(() -> new IllegalStateException("DRAFT status not found in database"));

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
