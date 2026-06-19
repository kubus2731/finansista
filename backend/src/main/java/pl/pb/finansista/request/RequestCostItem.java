package pl.pb.finansista.request;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import pl.pb.finansista.common.BaseEntity;

@Entity
@Table(name = "request_cost_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCostItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_id", nullable = false)
  private Request request;

  private Integer taskNo;
  private String itemName;
  private Integer quantity;
  private BigDecimal unitCost;
  private String notes;

  public RequestCostItem(
      Request request,
      Integer taskNo,
      String itemName,
      Integer quantity,
      BigDecimal unitCost,
      String notes) {
    Assert.isTrue(quantity == null || quantity > 0, "Cost item quantity must be positive");
    Assert.isTrue(
        unitCost == null || unitCost.signum() >= 0, "Cost item unit cost cannot be negative");
    this.request = request;
    this.taskNo = taskNo;
    this.itemName = itemName;
    this.quantity = quantity;
    this.unitCost = unitCost;
    this.notes = notes;
  }
}
