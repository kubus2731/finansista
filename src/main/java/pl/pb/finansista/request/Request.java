package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.ExposableModificationAuditedEntity;
import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Request extends ExposableModificationAuditedEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_status_id", nullable = false)
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_template_id")
    private RequestTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_category_id", nullable = false)
    private CostCategory costCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_source_id")
    private FundingSource fundingSource;

    // opis kategorii, gdy wybrano "Inne"
    private String costCategoryOther;

    // --- Załącznik 1, sekcja I: dane przedsięwzięcia ---
    private String realizerType;
    private String projectKind;
    private String projectKindOther;
    private String projectScope;
    private String projectScopeOther;
    private String projectNature;
    private String projectNatureOther;
    private LocalDate plannedDateFrom;
    private LocalDate plannedDateTo;
    private String location;
    private Integer participantsInvolved;
    private Integer participantsBenefiting;

    private String supervisorName;
    private String supervisorEmail;
    private String supervisorPhone;
    private String supervisorDepartment;

    @Lob
    private String provostOpinion;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestCostItem> costItems = new ArrayList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestFunding> fundings = new ArrayList<>();

    public Request(String title, String description, BigDecimal amount, User user, RequestStatus status, RequestTemplate template, Department department, CostCategory costCategory, FundingSource fundingSource) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.user = user;
        this.status = status;
        this.template = template;
        this.department = department;
        this.costCategory = costCategory;
        this.fundingSource = fundingSource;
    }

    public void update(String title, String description, BigDecimal amount, RequestTemplate template, Department department, CostCategory costCategory, FundingSource fundingSource) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.template = template;
        this.department = department;
        this.costCategory = costCategory;
        this.fundingSource = fundingSource;
    }

    public void changeStatus(RequestStatus newStatus) {
        this.status = newStatus;
    }

    public void fillDetails(String realizerType, String projectKind, String projectKindOther,
                            String projectScope, String projectScopeOther,
                            String projectNature, String projectNatureOther,
                            LocalDate plannedDateFrom, LocalDate plannedDateTo, String location,
                            Integer participantsInvolved, Integer participantsBenefiting,
                            String supervisorName, String supervisorEmail,
                            String supervisorPhone, String supervisorDepartment) {
        this.realizerType = realizerType;
        this.projectKind = projectKind;
        this.projectKindOther = projectKindOther;
        this.projectScope = projectScope;
        this.projectScopeOther = projectScopeOther;
        this.projectNature = projectNature;
        this.projectNatureOther = projectNatureOther;
        this.plannedDateFrom = plannedDateFrom;
        this.plannedDateTo = plannedDateTo;
        this.location = location;
        this.participantsInvolved = participantsInvolved;
        this.participantsBenefiting = participantsBenefiting;
        this.supervisorName = supervisorName;
        this.supervisorEmail = supervisorEmail;
        this.supervisorPhone = supervisorPhone;
        this.supervisorDepartment = supervisorDepartment;
    }

    public void addTask(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                        BigDecimal plannedCost, String actions) {
        this.tasks.add(new RequestTask(this, taskNo, name, dateFrom, dateTo, plannedCost, actions));
    }

    public void addCostItem(Integer taskNo, String itemName, Integer quantity,
                            BigDecimal unitCost, String notes) {
        this.costItems.add(new RequestCostItem(this, taskNo, itemName, quantity, unitCost, notes));
    }

    public void addFunding(String sourceName, BigDecimal amountRequested, BigDecimal amountGranted) {
        this.fundings.add(new RequestFunding(this, sourceName, amountRequested, amountGranted));
    }

    public void setCostCategoryOther(String costCategoryOther) {
        this.costCategoryOther = costCategoryOther;
    }
}