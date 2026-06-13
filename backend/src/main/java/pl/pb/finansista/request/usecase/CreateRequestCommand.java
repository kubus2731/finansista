package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateRequestCommand(
        String title,
        String description,
        BigDecimal amount,
        String userEmail,
        UUID templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId,
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
        List<TaskData> tasks,
        List<CostItemData> costItems,
        List<FundingData> fundings
) {

    public CreateRequestCommand(String title, String description, BigDecimal amount, String userEmail,
                                UUID templateId, Long departmentId, Long costCategoryId, Long fundingSourceId) {
        this(title, description, amount, userEmail, templateId, departmentId, costCategoryId, fundingSourceId,
                null, null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null,
                List.of(), List.of(), List.of());
    }

    public record TaskData(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                           BigDecimal plannedCost, String actions) {}

    public record CostItemData(Integer taskNo, String itemName, Integer quantity,
                               BigDecimal unitCost, String notes) {}

    public record FundingData(String sourceName, BigDecimal amountRequested, BigDecimal amountGranted) {}
}
