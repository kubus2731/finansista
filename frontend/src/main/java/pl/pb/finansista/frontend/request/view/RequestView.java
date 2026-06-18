package pl.pb.finansista.frontend.request.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RequestView(
        String id,
        String title,
        BigDecimal amount,
        String status,
        String departmentName,
        String applicantName,
        LocalDate createdAt
) {
}
