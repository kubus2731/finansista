package pl.pb.finansista.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class ExposableModificationAuditedEntity extends ModificationAuditedEntity {

    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "external_id", updatable = false, nullable = false, unique = true)
    private UUID externalId;

}
