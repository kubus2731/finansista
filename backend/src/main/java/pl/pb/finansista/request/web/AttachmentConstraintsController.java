package pl.pb.finansista.request.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.finansista.request.config.AttachmentProperties;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentConstraintsController {

  private final AttachmentProperties attachmentProperties;
  private final Environment environment;

  @GetMapping("/constraints")
  public ResponseEntity<AttachmentConstraintsResponse> getConstraints() {
    DataSize maxFileSize =
        DataSize.parse(environment.getProperty("spring.servlet.multipart.max-file-size", "1MB"));
    return ResponseEntity.ok(
        new AttachmentConstraintsResponse(
            attachmentProperties.allowedContentTypes(), maxFileSize.toBytes()));
  }
}
