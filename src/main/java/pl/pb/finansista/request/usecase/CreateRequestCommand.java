package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;

public record CreateRequestCommand(
        String title,
        String description,
        BigDecimal amount,
        String userEmail,
        Long templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId
) {
}
