package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.usecase.CostItemData;
import pl.pb.finansista.request.usecase.CreateRequestCommand;
import pl.pb.finansista.request.usecase.FundingData;
import pl.pb.finansista.request.usecase.ProjectDetailsData;
import pl.pb.finansista.request.usecase.SupervisorData;
import pl.pb.finansista.request.usecase.TaskData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateRequestRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        String templateId,
        @NotNull Long departmentId,
        @NotNull Long costCategoryId,
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
        @PositiveOrZero Integer participantsInvolved,
        @PositiveOrZero Integer participantsBenefiting,
        String supervisorName,
        @Email String supervisorEmail,
        String supervisorPhone,
        String supervisorDepartment,
        @Valid List<TaskItem> tasks,
        @Valid List<CostItemEntry> costItems,
        @Valid List<FundingEntry> fundings
) {
    public CreateRequestCommand toCommand(UUID userExternalId) {
        return new CreateRequestCommand(
                title, description, amount, userExternalId,
                templateId != null ? ExternalIdEncoder.decode(templateId) : null,
                departmentId, costCategoryId,
                new ProjectDetailsData(realizerType, projectKind, projectKindOther,
                        projectScope, projectScopeOther, projectNature, projectNatureOther,
                        plannedDateFrom, plannedDateTo, location,
                        participantsInvolved, participantsBenefiting),
                new SupervisorData(supervisorName, supervisorEmail, supervisorPhone, supervisorDepartment),
                mapTasks(), mapCostItems(), mapFundings()
        );
    }

    private List<TaskData> mapTasks() {
        return tasks == null ? List.of() : tasks.stream()
                .map(t -> new TaskData(t.taskNo(), t.name(), t.dateFrom(),
                        t.dateTo(), t.plannedCost(), t.actions()))
                .toList();
    }

    private List<CostItemData> mapCostItems() {
        return costItems == null ? List.of() : costItems.stream()
                .map(c -> new CostItemData(c.taskNo(), c.itemName(),
                        c.quantity(), c.unitCost(), c.notes()))
                .toList();
    }

    private List<FundingData> mapFundings() {
        return fundings == null ? List.of() : fundings.stream()
                .map(f -> new FundingData(f.fundingSourceId(), f.amountRequested()))
                .toList();
    }

    public record TaskItem(@NotNull Integer taskNo, @NotBlank String name,
                           @NotNull LocalDate dateFrom, @NotNull LocalDate dateTo,
                           @NotNull @PositiveOrZero BigDecimal plannedCost, String actions) {}

    public record CostItemEntry(@NotNull Integer taskNo, @NotBlank String itemName,
                                @NotNull @Positive Integer quantity,
                                @NotNull @PositiveOrZero BigDecimal unitCost, String notes) {}

    public record FundingEntry(@NotNull Long fundingSourceId,
                               @NotNull @Positive BigDecimal amountRequested) {}
}
