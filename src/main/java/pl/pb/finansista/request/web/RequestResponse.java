package pl.pb.finansista.request.web;

import pl.pb.finansista.request.Request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import pl.pb.finansista.common.ExternalIdEncoder;

public record RequestResponse(
        String id,
        String title,
        String description,
        BigDecimal amount,
        String status,
        String templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static RequestResponse of(Request request) {
        return new RequestResponse(
                ExternalIdEncoder.encode("req", request.getExternalId()),
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getStatus().getName(),
                request.getTemplate() != null ? ExternalIdEncoder.encode("tpl", request.getTemplate().getExternalId()) : null,
                request.getDepartment().getId(),
                request.getCostCategory().getId(),
                request.getFundingSource() != null ? request.getFundingSource().getId() : null,
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
