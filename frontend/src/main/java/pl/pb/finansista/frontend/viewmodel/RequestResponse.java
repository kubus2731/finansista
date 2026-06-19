package pl.pb.finansista.frontend.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        UUID externalId,
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
        RequestPermissions permissions,
        List<TaskResponse> tasks,
        List<CostItemResponse> costItems,
        List<FundingResponse> fundings
) {

    public record RequestPermissions(boolean canEdit, boolean canDelete,
                                     boolean canManageAttachments, boolean canRecordProvostOpinion) {}

    public record TaskResponse(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                               BigDecimal plannedCost, String actions) {}

    public record CostItemResponse(Integer taskNo, String itemName, Integer quantity,
                                   BigDecimal unitCost, String notes) {}

    public record FundingResponse(Long fundingSourceId, String sourceName,
                                  BigDecimal amountRequested, BigDecimal amountGranted,
                                  ZonedDateTime grantedAt, boolean canGrant) {}
}

