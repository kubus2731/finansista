package pl.pb.finansista.request.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RequestView(
        UUID id,
        String title,
        BigDecimal amount,
        String status,
        String departmentName,
        String applicantName,
        LocalDate createdAt
) {
}
