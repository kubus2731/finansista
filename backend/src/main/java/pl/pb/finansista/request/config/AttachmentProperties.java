package pl.pb.finansista.request.config;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.attachments")
public record AttachmentProperties(Set<String> allowedContentTypes) {}
