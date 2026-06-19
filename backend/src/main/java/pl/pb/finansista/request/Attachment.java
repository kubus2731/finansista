package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.ExposableCreationAuditedEntity;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends ExposableCreationAuditedEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_id", nullable = false)
  private Request request;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "storage_key", nullable = false, length = 500, updatable = false, unique = true)
  private String storageKey;

  @Column(name = "content_type", nullable = false, length = 150)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  public Attachment(
      Request request, String fileName, String storageKey, String contentType, long sizeBytes) {
    this.request = request;
    this.fileName = fileName;
    this.storageKey = storageKey;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
  }
}
