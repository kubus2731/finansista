package pl.pb.finansista.request.web;

import java.time.ZonedDateTime;
import pl.pb.finansista.request.ActivityLog;

public record ActivityLogResponse(
    String newStatus,
    String oldStatus,
    String description,
    String userFullName,
    ZonedDateTime createdAt) {
  public static ActivityLogResponse of(ActivityLog log) {
    return new ActivityLogResponse(
        log.getNewStatus().getName(),
        log.getOldStatus() != null ? log.getOldStatus().getName() : null,
        log.getDescription(),
        log.getUser().getName() + " " + log.getUser().getSurname(),
        log.getCreatedAt());
  }
}
