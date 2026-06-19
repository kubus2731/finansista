package pl.pb.finansista.request;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "request_task")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestTask extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_id", nullable = false)
  private Request request;

  @Column(nullable = false)
  private Integer taskNo;

  private String name;
  private LocalDate dateFrom;
  private LocalDate dateTo;
  private BigDecimal plannedCost;

  @Lob private String actions;

  public RequestTask(
      Request request,
      Integer taskNo,
      String name,
      LocalDate dateFrom,
      LocalDate dateTo,
      BigDecimal plannedCost,
      String actions) {
    Assert.isTrue(
        dateFrom == null || dateTo == null || !dateTo.isBefore(dateFrom),
        "Task end date cannot be before its start date");
    this.request = request;
    this.taskNo = taskNo;
    this.name = name;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.plannedCost = plannedCost;
    this.actions = actions;
  }
}
