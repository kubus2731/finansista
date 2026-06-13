package pl.pb.finansista.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.ExposableModificationAuditedEntity;

@Entity
@Table(name = "request_templates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestTemplate extends ExposableModificationAuditedEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private boolean active = true;



    public RequestTemplate(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateDetails(String newTitle, String newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }

    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("Already inactive.");
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new IllegalStateException("Already active.");
        }
        this.active = true;
    }
}