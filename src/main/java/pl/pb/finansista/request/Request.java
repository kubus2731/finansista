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
}