package pl.pb.finansista.frontend.request.view;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateRequestForm {

    @NotBlank(message = "Tytuł jest wymagany")
    @Size(max = 100, message = "Tytuł może mieć najwyżej 100 znaków")
    private String title;

    private String realizerType;
    private String projectKind;
    private String projectKindOther;
    private String projectScope;
    private String projectScopeOther;
    private String projectNature;
    private String projectNatureOther;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate plannedDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate plannedDateTo;

    private String location;
    private Integer participantsInvolved;
    private Integer participantsBenefiting;

    private String supervisorName;
    private String supervisorEmail;
    private String supervisorPhone;
    private String supervisorDepartment;

    @NotBlank(message = "Uzasadnienie jest wymagane")
    private String description;

    @NotNull(message = "Kategoria nie może być pusta")
    private Long costCategoryId;

    @NotNull(message = "Wartość nie może być pusta")
    @Positive(message = "Wartość nie może być ujemna")
    private BigDecimal amount;

    private List<TaskRow> tasks = new ArrayList<>();
    private List<CostItemRow> costItems = new ArrayList<>();

    private List<FundingRow> fundings = new ArrayList<>();

    @Data
    public static class TaskRow {
        private Integer taskNo;
        private String name;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate dateFrom;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate dateTo;
        private BigDecimal plannedCost;
        private String actions;
    }

    @Data
    public static class CostItemRow {
        private Integer taskNo;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitCost;
        private String notes;
    }

    @Data
    public static class FundingRow {
        private String sourceName;
        private BigDecimal amountRequested;
        private BigDecimal amountGranted;
    }
}
