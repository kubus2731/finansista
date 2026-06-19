package pl.pb.finansista.common.config;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.pb.finansista.common.web.ExternalIdEncoder;

@Component
public class ExternalIdConverter implements Converter<String, UUID> {
  @Override
  public UUID convert(@NonNull String source) {
    if (source.isBlank()) {
      return null;
    }

    return ExternalIdEncoder.decode(source);
  }
}
