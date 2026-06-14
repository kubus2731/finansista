package pl.pb.finansista.request;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "v_department_requests_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentRequestsSummary {

    @Id
    private Long id;

    private String departmentName;
    private Long totalRequestsCount;
    private BigDecimal totalRequestsAmount;
    private Long acceptedRequestsCount;
    private BigDecimal acceptedRequestsAmount;
    private Long rejectedRequestsCount;
    private BigDecimal rejectedRequestsAmount;

}
