package pl.pb.finansista.request.web;

import pl.pb.finansista.request.Comment;

import java.time.ZonedDateTime;

public record CommentResponse(
        Long id,
        String content,
        String userFullName,
        String userEmail,
        ZonedDateTime createdAt
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getName() + " " + comment.getUser().getSurname(),
                comment.getUser().getEmail(),
                comment.getCreatedAt()
        );
    }
}
