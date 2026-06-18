package pl.pb.finansista.common.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface FileStorage {

    String store(InputStream content, long sizeBytes, String originalFilename);

    Resource load(String storageKey);

    void delete(String storageKey);
}
