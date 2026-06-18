package pl.pb.finansista.request.web;

import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.ProjectDetails;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestFunding;
import pl.pb.finansista.request.SupervisorInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public record RequestResponse(
        String id,
        String title,
        String description,
        BigDecimal amount,
        BigDecimal totalRequested,
        BigDecimal totalGranted,
        String status,
        String templateId,
        Long departmentId,
        Long costCategoryId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        String departmentName,
        String applicantName,
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
        boolean canEdit,
        boolean canDelete,
        boolean canManageAttachments,
        boolean canRecordProvostOpinion,
        List<TaskResponse> tasks,
        List<CostItemResponse> costItems,
        List<FundingResponse> fundings
) {
    /**
     * Authorization flags (canEdit/canDelete/... and per-row canGrant) are decided by the
     * caller — see {@link RequestResponseAssembler}, which mirrors the rules enforced by the
     * mutating use cases. This factory only shapes the payload; it does not decide permissions.
     */
    public static RequestResponse of(Request request,
                                     boolean canEdit,
                                     boolean canDelete,
                                     boolean canManageAttachments,
                                     boolean canRecordProvostOpinion,
                                     Predicate<RequestFunding> canGrant) {
        ProjectDetails pd = request.getProjectDetails() != null ? request.getProjectDetails() : ProjectDetails.empty();
        SupervisorInfo sup = request.getSupervisor() != null ? request.getSupervisor() : SupervisorInfo.empty();

        BigDecimal totalRequested = request.getFundings().stream()
                .map(RequestFunding::getAmountRequested)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGranted = request.getFundings().stream()
                .map(RequestFunding::getAmountGranted)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RequestResponse(
                ExternalIdEncoder.encode("req", request.getExternalId()),
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                totalRequested,
                totalGranted,
                request.getStatus().getName(),
                request.getTemplate() != null ? ExternalIdEncoder.encode("tpl", request.getTemplate().getExternalId()) : null,
                request.getDepartment().getId(),
                request.getCostCategory().getId(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
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
                canEdit,
                canDelete,
                canManageAttachments,
                canRecordProvostOpinion,
                request.getTasks().stream()
                        .map(t -> new TaskResponse(t.getTaskNo(), t.getName(), t.getDateFrom(),
                                t.getDateTo(), t.getPlannedCost(), t.getActions()))
                        .toList(),
                request.getCostItems().stream()
                        .map(c -> new CostItemResponse(c.getTaskNo(), c.getItemName(),
                                c.getQuantity(), c.getUnitCost(), c.getNotes()))
                        .toList(),
                request.getFundings().stream()
                        .map(f -> new FundingResponse(f.getSource().getId(), f.getSource().getName(),
                                f.getAmountRequested(), f.getAmountGranted(), f.getGrantedAt(),
                                canGrant.test(f)))
                        .toList()
        );
    }

    public record TaskResponse(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                               BigDecimal plannedCost, String actions) {}

    public record CostItemResponse(Integer taskNo, String itemName, Integer quantity,
                                   BigDecimal unitCost, String notes) {}

    public record FundingResponse(Long fundingSourceId, String sourceName,
                                  BigDecimal amountRequested, BigDecimal amountGranted,
                                  ZonedDateTime grantedAt, boolean canGrant) {}
}
