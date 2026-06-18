package pl.pb.finansista.request.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "app.attachments")
public record AttachmentProperties(Set<String> allowedContentTypes) {
}
