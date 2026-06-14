package pl.pb.finansista.common.storage;

import org.springframework.core.io.Resource;

/**
 * Storage port for binary file content. Implementations decide the physical
 * layout; callers only ever hold the opaque {@code storageKey} returned by
 * {@link #store}. Swapping local disk for MinIO/S3 is a matter of providing a
 * different implementation - no caller, entity, or schema change required.
 */
public interface FileStorage {

    /**
     * Persists the given bytes and returns an opaque key that uniquely
     * identifies them for later retrieval or deletion.
     */
    String store(byte[] content, String originalFilename);

    /**
     * Opens the stored bytes for the given key as a streamable resource.
     *
     * @throws java.io.UncheckedIOException if nothing is stored under the key
     */
    Resource load(String storageKey);

    /**
     * Removes the stored bytes for the given key. No-op if nothing is stored.
     */
    void delete(String storageKey);
}
