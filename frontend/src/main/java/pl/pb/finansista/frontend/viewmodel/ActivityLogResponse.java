package pl.pb.finansista.frontend.viewmodel;

import java.time.ZonedDateTime;

public record ActivityLogResponse(
    String newStatus,
    String oldStatus,
    String description,
    String userFullName,
    ZonedDateTime createdAt) {}
