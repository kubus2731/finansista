package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record EditRequestCommand(
    UUID externalId,
    UUID userExternalId,
    List<String> userAuthorities,
    String title,
    String description,
    BigDecimal amount,
    UUID templateId,
    Long departmentId,
    Long costCategoryId,
    ProjectDetailsData projectDetails,
    SupervisorData supervisor,
    List<TaskData> tasks,
    List<CostItemData> costItems,
    List<FundingData> fundings,
    Long version) {

  public EditRequestCommand {
    tasks = tasks == null ? List.of() : tasks;
    costItems = costItems == null ? List.of() : costItems;
    fundings = fundings == null ? List.of() : fundings;
  }
}
