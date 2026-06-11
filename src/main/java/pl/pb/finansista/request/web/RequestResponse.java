package pl.pb.finansista.request.web;

import pl.pb.finansista.request.Request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record RequestResponse(
        Long id,
        String title,
        String description,
        BigDecimal amount,
        String status,
        Long templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static RequestResponse of(Request request) {
        return new RequestResponse(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getStatus().getName(),
                request.getTemplate() != null ? request.getTemplate().getId() : null,
                request.getDepartment().getId(),
                request.getCostCategory().getId(),
                request.getFundingSource() != null ? request.getFundingSource().getId() : null,
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
