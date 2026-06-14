package pl.pb.finansista.common.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Filesystem-backed {@link FileStorage}. Active by default; selected when
 * {@code storage.type=local} (or unset). A future S3/MinIO implementation would
 * be annotated {@code @ConditionalOnProperty(name = "storage.type", havingValue = "s3")}
 * and the backend swapped by changing that single property.
 */
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
            throw new UncheckedIOException("Could not create attachment storage directory: " + this.baseDir, e);
        }
        log.info("LocalFileStorage initialised at {}", this.baseDir);
    }

    @Override
    public String store(byte[] content, String originalFilename) {
        // Key carries a random prefix so it is collision-proof and unguessable;
        // the sanitised original name is kept only as a human-readable suffix.
        String storageKey = UUID.randomUUID() + "_" + sanitize(originalFilename);
        Path target = resolve(storageKey);
        try {
            Files.write(target, content);
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
            // Best-effort: a leftover orphan file is harmless and sweepable.
            log.warn("Failed to delete stored file for key {}", storageKey, e);
        }
    }

    /** Resolves a key under the base dir and guards against path traversal. */
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
        // Strip any directory components and keep only safe characters.
        String name = Path.of(filename).getFileName().toString();
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
