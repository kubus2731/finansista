package pl.pb.finansista.request.web;

import pl.pb.finansista.common.ExternalIdEncoder;
import pl.pb.finansista.request.Comment;

import java.time.ZonedDateTime;

public record CommentResponse(
        String id,
        String content,
        String userFullName,
        String userEmail,
        ZonedDateTime createdAt
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                ExternalIdEncoder.encode("com", comment.getExternalId()),
                comment.getContent(),
                comment.getUser().getName() + " " + comment.getUser().getSurname(),
                comment.getUser().getEmail(),
                comment.getCreatedAt()
        );
    }
}
