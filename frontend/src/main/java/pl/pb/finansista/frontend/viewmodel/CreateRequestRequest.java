package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateRequestRequest(
    @NotBlank String title,
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
    Integer participantsInvolved,
    Integer participantsBenefiting,
    String supervisorName,
    String supervisorEmail,
    String supervisorPhone,
    String supervisorDepartment,
    List<TaskItem> tasks,
    List<CostItemEntry> costItems,
    List<FundingEntry> fundings) {

  public record TaskItem(
      Integer taskNo,
      String name,
      LocalDate dateFrom,
      LocalDate dateTo,
      BigDecimal plannedCost,
      String actions) {}

  public record CostItemEntry(
      Integer taskNo, String itemName, Integer quantity, BigDecimal unitCost, String notes) {}

  public record FundingEntry(Long fundingSourceId, BigDecimal amountRequested) {}
}
