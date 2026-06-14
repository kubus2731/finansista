package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaskData(Integer taskNo, String name, LocalDate dateFrom, LocalDate dateTo,
                       BigDecimal plannedCost, String actions) {
}
