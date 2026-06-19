package pl.pb.finansista.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalFileStorage implements FileStorage {

  private final Path baseDir;

  public LocalFileStorage(@Value("${storage.local.base-dir}") String baseDir) {
    this.baseDir = Path.of(baseDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.baseDir);
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Could not create attachment storage directory: " + this.baseDir, e);
    }
    log.info("LocalFileStorage initialised at {}", this.baseDir);
  }

  @Override
  public String store(InputStream content, long sizeBytes, String originalFilename) {
    String storageKey = UUID.randomUUID() + "_" + sanitize(originalFilename);
    Path target = resolve(storageKey);
    try {
      Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to store file at " + target, e);
    }
    return storageKey;
  }

  @Override
  public Resource load(String storageKey) {
    Path source = resolve(storageKey);
    if (!Files.isReadable(source)) {
      throw new UncheckedIOException(
          new IOException("Stored file not found for key " + storageKey));
    }
    return new FileSystemResource(source);
  }

  @Override
  public void delete(String storageKey) {
    try {
      Files.deleteIfExists(resolve(storageKey));
    } catch (IOException e) {
      log.warn("Failed to delete stored file for key {}", storageKey, e);
    }
  }

  private Path resolve(String storageKey) {
    Path resolved = baseDir.resolve(storageKey).normalize();
    if (!resolved.startsWith(baseDir)) {
      throw new IllegalArgumentException("Resolved path escapes storage root: " + storageKey);
    }
    return resolved;
  }

  private String sanitize(String filename) {
    if (filename == null || filename.isBlank()) {
      return "file";
    }
    String name = Path.of(filename).getFileName().toString();
    return name.replaceAll("[^a-zA-Z0-9._-]", "_");
  }
}
