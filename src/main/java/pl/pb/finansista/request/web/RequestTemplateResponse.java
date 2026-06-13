package pl.pb.finansista.request.web;

import pl.pb.finansista.common.ExternalIdEncoder;
import pl.pb.finansista.request.RequestTemplate;

public record RequestTemplateResponse(
        String id,
        String title,
        String description,
        boolean active
) {
    public static RequestTemplateResponse of(RequestTemplate template) {
        return new RequestTemplateResponse(
                ExternalIdEncoder.encode("tpl", template.getExternalId()),
                template.getTitle(),
                template.getDescription(),
                template.isActive()
        );
    }
}
