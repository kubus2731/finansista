package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseTimeAuditedEntity;
import pl.pb.finansista.user.User;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeAuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(nullable = false, length = 1000)
    private String content;

    public Comment(Request request, User user, String content) {
        this.request = request;
        this.user = user;
        this.content = content;
    }
}