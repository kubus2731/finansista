package pl.pb.finansista.common.storage;

import java.io.InputStream;
import org.springframework.core.io.Resource;

public interface FileStorage {

    String store(InputStream content, long sizeBytes, String originalFilename);

    Resource load(String storageKey);

    void delete(String storageKey);
}
