package pl.pb.finansista.request.web;

import pl.pb.finansista.request.DepartmentRequestsSummary;

import java.math.BigDecimal;

public record DepartmentRequestsSummaryResponse(
    Long departmentId,
    String departmentName,
    Long totalRequestsCount,
    BigDecimal totalRequestsAmount,
    Long acceptedRequestsCount,
    BigDecimal acceptedRequestsAmount,
    Long rejectedRequestsCount,
    BigDecimal rejectedRequestsAmount) {
  public static DepartmentRequestsSummaryResponse of(DepartmentRequestsSummary summary) {
    return new DepartmentRequestsSummaryResponse(
        summary.getId(),
        summary.getDepartmentName(),
        summary.getTotalRequestsCount(),
        summary.getTotalRequestsAmount(),
        summary.getAcceptedRequestsCount(),
        summary.getAcceptedRequestsAmount(),
        summary.getRejectedRequestsCount(),
        summary.getRejectedRequestsAmount());
  }
}
