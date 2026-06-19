package pl.pb.finansista.frontend.viewmodel;

import java.time.ZonedDateTime;

public record CommentResponse(
    String id, String content, String userFullName, String userEmail, ZonedDateTime createdAt) {}
