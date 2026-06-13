package pl.pb.finansista.request.web;

import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.ProjectDetails;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.SupervisorInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record RequestResponse(
        String id,
        String title,
        String description,
        BigDecimal amount,
        String status,
        String templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        // pola pomocnicze dla klienta frontowego (warstwa Thymeleaf konsumuje REST)
        UUID externalId,
        String departmentName,
        String applicantName,
        // Załącznik 1, sekcje I-III
        String realizerType,
        String projectKind,
        String projectKindOther,
        String projectScope,
        String projectScopeOther,
        String projectNature,
        String projectNatureOther,
        LocalDate plannedDateFrom,
        LocalDate plannedDateTo,
        String location,
        Integer participantsInvolved,
        Integer participantsBenefiting,
        String supervisorName,
        String supervisorEmail,
        String supervisorPhone,
        String supervisorDepartment,
        String provostOpinion,
        // sekcja IV i VI
        List<TaskResponse> tasks,
        List<CostItemResponse> costItems,
        List<FundingResponse> fundings
) {
    public static RequestResponse of(Request request) {
        ProjectDetails pd = request.getProjectDetails() != null ? request.getProjectDetails() : ProjectDetails.empty();
        SupervisorInfo sup = request.getSupervisor() != null ? request.getSupervisor() : SupervisorInfo.empty();
        return new RequestResponse(
                ExternalIdEncoder.encode("req", request.getExternalId()),
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getStatus().getName(),
                request.getTemplate() != null ? ExternalIdEncoder.encode("tpl", request.getTemplate().getExternalId()) : null,
                request.getDepartment().getId(),
                request.getCostCategory().getId(),
                request.getFundingSource() != null ? request.getFundingSource().getId() : null,
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getExternalId(),
                request.getDepartment().getName(),
                request.getUser().getName() + " " + request.getUser().getSurname(),
                pd.getRealizerType(),
                pd.getProjectKind(),
                pd.getProjectKindOther(),
                pd.getProjectScope(),
                pd.getProjectScopeOther(),
                pd.getProjectNature(),
                pd.getProjectNatureOther(),
                pd.getPlannedDateFrom(),
                pd.getPlannedDateTo(),
                pd.getLocation(),
                pd.getParticipantsInvolved(),
                pd.getParticipantsBenefiting(),
                sup.getName(),
                sup.getEmail(),
                sup.getPhone(),
                sup.getDepartment(),
                request.getProvostOpinion(),
                request.getTasks().stream()
                        .map(t -> new TaskResponse(t.getTaskNo(), t.getName(), t.getDateFrom(),
                                t.getDateTo(), t.getPlannedCost(), t.getActions()))
                        .toList(),
                request.getCostItems().stream()
                        .map(c -> new CostItemResponse(c.getTaskNo(), c.getItemName(),
                                c.getQuantity(), c.getUnitCost(), c.getNotes()))
                        .toList(),
                request.getFundings().stream()
                        .map(f -> new FundingResponse(f.getSourceName(), f.getAmountRequested(), f.getAmountGranted()))
                        .toList()
        );
    }

    public record TaskResponse(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                               BigDecimal plannedCost, String actions) {}

    public record CostItemResponse(Integer taskNo, String itemName, Integer quantity,
                                   BigDecimal unitCost, String notes) {}

    public record FundingResponse(String sourceName, BigDecimal amountRequested, BigDecimal amountGranted) {}
}
