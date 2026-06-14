package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;

public record CostItemData(Integer taskNo, String itemName, Integer quantity,
                           BigDecimal unitCost, String notes) {
}
