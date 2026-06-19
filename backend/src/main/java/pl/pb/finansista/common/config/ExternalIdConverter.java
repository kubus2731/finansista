package pl.pb.finansista.common.config;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.pb.finansista.common.web.ExternalIdEncoder;

import java.util.UUID;

@Component
public class ExternalIdConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(@NonNull String source) {
        if (source.isBlank()) {
            return null;
        }
        
        try {
            return ExternalIdEncoder.decode(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ID format", e);
        }
    }
}
